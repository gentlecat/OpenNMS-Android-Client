package org.opennms.android.sync;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.opennms.android.R;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.parsing.AlarmsParser;
import org.opennms.android.parsing.EventsParser;
import org.opennms.android.parsing.NodesParser;
import org.opennms.android.parsing.OutagesParser;
import org.opennms.android.provider.Contract;
import org.opennms.android.ui.alarms.AlarmsActivity;

import java.util.ArrayList;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";
    public static final String SYNC_TYPE_EXTRA_KEY = "sync_type";
    public static final int SYNC_TYPE_NODES = 1;
    public static final int SYNC_TYPE_EVENTS = 2;
    public static final int SYNC_TYPE_ALARMS = 3;
    public static final int SYNC_TYPE_OUTAGES = 4;
    private static final int ALARM_NOTIFICATION_ID = 1;
    private static final int WARNING_NOTIFICATION_ID = 2;
    private ContentResolver contentResolver;
    private ServerCommunication serverCommunication;
    private Context context;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        contentResolver = context.getContentResolver();
        serverCommunication = new ServerCommunication(context);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
        contentResolver = context.getContentResolver();
        serverCommunication = new ServerCommunication(context);
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d(TAG, "Performing sync...");
        int syncType = extras.getInt(SYNC_TYPE_EXTRA_KEY);
        switch (syncType) {
            case SYNC_TYPE_NODES:
                syncNodes();
                break;
            case SYNC_TYPE_EVENTS:
                syncEvents();
                break;
            case SYNC_TYPE_ALARMS:
                syncAlarms(extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL));
                break;
            case SYNC_TYPE_OUTAGES:
                syncOutages();
                break;
            default:
                Log.wtf(TAG, "Wrong sync type extra value (" + syncType + ")!");
                break;
        }
    }

    private void syncNodes() {
        Log.d(TAG, "Synchronizing nodes...");
        String result;
        try {
            result = serverCommunication.get("nodes/?limit=0");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during node synchronization process", e);
            return;
        }
        contentResolver.delete(Contract.Nodes.CONTENT_URI, null, null);
        ArrayList<ContentValues> values = NodesParser.parse(result);
        contentResolver.bulkInsert(Contract.Nodes.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

    private void syncEvents() {
        Log.d(TAG, "Synchronizing events...");
        String result;
        try {
            result = serverCommunication.get("events?orderBy=id&order=desc&limit=25");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during event synchronization process", e);
            return;
        }
        contentResolver.delete(Contract.Events.CONTENT_URI, null, null);
        ArrayList<ContentValues> values = EventsParser.parse(result);
        contentResolver.bulkInsert(Contract.Events.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

    private void syncAlarms(  boolean isManual) {
        Log.d(TAG, "Synchronizing alarms...");

        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        // TODO: Figure out if this action is required everywhere
        if (networkInfo == null || !networkInfo.isConnected()) {
            if (!isManual) issueWarningNotification(context,
                    context.getString(R.string.sync_failed_notif_title),
                    context.getString(R.string.sync_failed_notif_text));
            return;
        }

        if (!isManual) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPref.getBoolean("wifi_only", context.getResources().getBoolean(R.bool.wifi_only))) {
                NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (!wifi.isConnected()) {
                    issueWarningNotification(context,
                            context.getString(R.string.sync_failed_notif_title),
                            context.getString(R.string.sync_failed_notif_text_wifi));
                    return;
                }
            }
        }

        String result;
        try {
            result = serverCommunication.get("alarms?orderBy=id&order=desc&limit=0");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during alarm synchronization process", e);
            return;
        }
        ArrayList<ContentValues> values = AlarmsParser.parse(result);
        contentResolver.delete(Contract.Alarms.CONTENT_URI, null, null); // Deleting old data
        contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI, values.toArray(new ContentValues[values.size()]));


        if (!isManual) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            int latestShownAlarmId = sharedPref.getInt("latest_shown_alarm_id", 0);
            String minimalSeverity = sharedPref.getString("minimal_severity", context.getString(R.string.default_minimal_severity));
            String[] severityValues = context.getResources().getStringArray(R.array.severity_values);
            int newAlarmsCount = 0, maxId = 0;
            for (ContentValues currentValues : values) {
                int id = currentValues.getAsInteger(Contract.Alarms._ID);
                if (id > latestShownAlarmId) {
                    String severity = currentValues.getAsString(Contract.Alarms.SEVERITY);
                    for (String curSeverityVal : severityValues) {
                        if (curSeverityVal.equals(severity)) {
                            newAlarmsCount++;
                            break;
                        }
                        if (curSeverityVal.equals(minimalSeverity)) break;
                    }
                }
                if (id > maxId) maxId = id;
            }

            if (latestShownAlarmId != maxId)
                sharedPref.edit().putInt("latest_shown_alarm_id", maxId).commit();
            boolean notificationsOn = sharedPref.getBoolean("notifications_on", context.getResources().getBoolean(R.bool.default_notifications));
            if (newAlarmsCount > 0 && notificationsOn)
                issueNewAlarmsNotification(context, newAlarmsCount);
        }
    }

    private void syncOutages() {
        Log.d(TAG, "Synchronizing outages...");
        String result;
        try {
            result = serverCommunication.get("outages?orderBy=id&order=desc&limit=25");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during outage synchronization process", e);
            return;
        }
        contentResolver.delete(Contract.Outages.CONTENT_URI, null, null);
        ArrayList<ContentValues> values = OutagesParser.parse(result);
        contentResolver.bulkInsert(Contract.Outages.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

    private void issueNewAlarmsNotification(Context context, int newAlarmsCount) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(context.getString(R.string.new_alarms_notif_title))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission

        if (newAlarmsCount == 1)
            builder.setContentText(context.getString(R.string.new_alarms_notif_text_singular));
        else
            builder.setContentText(String.format(
                    context.getString(R.string.new_alarms_notif_text_plural),
                    newAlarmsCount));

        // Clicking the notification itself displays AlarmsActivity
        Intent resultIntent = new Intent(context, AlarmsActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ALARM_NOTIFICATION_ID, builder.build());
    }

    private void issueWarningNotification(Context context, String title, String text) {
        // TODO: Notify only once
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission

        // Clicking the notification itself displays AlarmsActivity
        Intent resultIntent = new Intent(context, AlarmsActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(WARNING_NOTIFICATION_ID, builder.build());
    }

}