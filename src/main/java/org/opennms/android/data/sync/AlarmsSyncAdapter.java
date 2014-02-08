package org.opennms.android.data.sync;

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

import org.opennms.android.App;
import org.opennms.android.R;
import org.opennms.android.data.api.ServerInterface;
import org.opennms.android.data.api.model.Alarm;
import org.opennms.android.data.ContentValuesGenerator;
import org.opennms.android.data.storage.Contract;
import org.opennms.android.settings.NotificationSettings;
import org.opennms.android.ui.alarms.AlarmsActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Handle the transfer of alarm data between a server and an app, using the Android sync adapter
 * framework.
 */
public class AlarmsSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String TAG = "AlarmsSyncAdapter";
    private static final int ALARM_NOTIFICATION_ID = 0x1;
    private static final int WARNING_NOTIFICATION_ID = 0x2;
    private ContentResolver contentResolver;
    private Context context;
    @Inject
    ServerInterface server;

    /**
     * Set up the sync adapter
     */
    public AlarmsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        contentResolver = context.getContentResolver();

        App app = App.get(context);
        app.inject(this);
    }

    /**
     * Set up the sync adapter
     * This form of the constructor maintains compatibility with Android 3.0 and later platform versions
     */
    public AlarmsSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d(TAG, "Synchronizing alarms...");

        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (NotificationSettings.wifiOnly(context)) {
            NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!wifi.isConnected()) {
                issueWarningNotification(
                        context,
                        context.getString(R.string.sync_failed_notif_title),
                        context.getString(R.string.sync_failed_notif_text_wifi));
                return;
            }
        }

        List<Alarm> alarms;
        try {
            alarms = server.alarmsAll().alarms;
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during alarm synchronization process", e);
            return;
        }
        contentResolver.delete(Contract.Alarms.CONTENT_URI, null, null);
        ArrayList<ContentValues> values = ContentValuesGenerator.fromAlarms(alarms);
        contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI,
                values.toArray(new ContentValues[values.size()]));


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        final String STATE_LATEST_SHOWN_ALARM = "latest_shown_alarm_id";
        int latestShownAlarmId = sharedPref.getInt(STATE_LATEST_SHOWN_ALARM, 0);
        String minimalSeverity = NotificationSettings.minSeverity(context);
        String[] severityValues =
                context.getResources().getStringArray(R.array.severity_values);
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
                    if (curSeverityVal.equals(minimalSeverity)) {
                        break;
                    }
                }
            }
            if (id > maxId) {
                maxId = id;
            }
        }

        if (latestShownAlarmId != maxId) {
            sharedPref.edit().putInt(STATE_LATEST_SHOWN_ALARM, maxId).commit();
        }

        if (newAlarmsCount > 0 && NotificationSettings.enabled(context)) {
            issueNewAlarmsNotification(context, newAlarmsCount);
        }

        Log.d(TAG, "Alarm sync complete.");
    }

    private void issueNewAlarmsNotification(Context context, int newAlarmsCount) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(context.getString(R.string.new_alarms_notif_title))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission

        if (newAlarmsCount == 1) {
            builder.setContentText(context.getString(R.string.new_alarms_notif_text_singular));
        } else {
            builder.setContentText(String.format(
                    context.getString(R.string.new_alarms_notif_text_plural),
                    newAlarmsCount));
        }

        // Clicking the notification itself displays AlarmsActivity
        Intent resultIntent = new Intent(context, AlarmsActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent = PendingIntent
                .getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
        PendingIntent resultPendingIntent = PendingIntent
                .getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(WARNING_NOTIFICATION_ID, builder.build());
    }

}