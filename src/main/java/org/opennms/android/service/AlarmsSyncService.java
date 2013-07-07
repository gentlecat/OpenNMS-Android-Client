package org.opennms.android.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import org.opennms.android.R;
import org.opennms.android.communication.alarms.AlarmsServerCommunication;
import org.opennms.android.dao.Columns.AlarmColumns;
import org.opennms.android.dao.alarms.Alarm;
import org.opennms.android.dao.alarms.AlarmsListProvider;
import org.opennms.android.ui.MainActivity;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class AlarmsSyncService extends IntentService {

    private static final String TAG = "AlarmsSyncService";
    private static final int ALARM_NOTIFICATION_ID = 1;

    public AlarmsSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContentResolver contentResolver = getContentResolver();
        AlarmsServerCommunication alarmsServer = new AlarmsServerCommunication(getApplicationContext());
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int latestShownAlarmId = sharedPref.getInt("latest_shown_alarm_id", 0);
        String minimalSeverity = sharedPref.getString("minimal_severity", getString(R.string.default_minimal_severity));
        String[] severityValues = getResources().getStringArray(R.array.severity_values);
        int newAlarmsCount = 0, maxId = 0;
        Log.i(TAG, "Synchronizing alarms...");
        try {
            List<Alarm> alarms = alarmsServer.getAlarms("alarms?orderBy=id&order=desc&limit=0", 25);
            contentResolver.delete(AlarmsListProvider.CONTENT_URI, null, null);
            for (Alarm alarm : alarms) {
                insertAlarm(contentResolver, alarm);
                if (alarm.getId() > latestShownAlarmId) {
                    for (String curSeverityVal : severityValues) {
                        if (curSeverityVal.equals(alarm.getSeverity())) {
                            newAlarmsCount++;
                            break;
                        }
                        if (curSeverityVal.equals(minimalSeverity)) break;
                    }
                }
                if (alarm.getId() > maxId) maxId = alarm.getId();
            }
            Log.i(TAG, "Done!");
        } catch (UnknownHostException e) {
            Log.e(TAG, "UnknownHostException", e);
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException", e);
        } catch (ExecutionException e) {
            Log.e(TAG, "ExecutionException", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } catch (TimeoutException e) {
            Log.e(TAG, "TimeoutException", e);
        }

        if (latestShownAlarmId != maxId) sharedPref.edit().putInt("latest_shown_alarm_id", maxId).commit();
        boolean notificationsOn = sharedPref.getBoolean("notifications_on", getResources().getBoolean(R.bool.default_notifications));
        if (newAlarmsCount > 0 && notificationsOn) issueNewAlarmsNotification(newAlarmsCount);
    }

    private Uri insertAlarm(ContentResolver contentResolver, Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(AlarmColumns.ALARM_ID, alarm.getId());
        values.put(AlarmColumns.SEVERITY, alarm.getSeverity());
        values.put(AlarmColumns.DESCRIPTION, alarm.getDescription());
        values.put(AlarmColumns.LOG_MESSAGE, alarm.getLogMessage());
        values.put(AlarmColumns.FIRST_EVENT_TIME, alarm.getFirstEventTime());
        values.put(AlarmColumns.LAST_EVENT_TIME, alarm.getLastEventTime());
        values.put(AlarmColumns.LAST_EVENT_ID, alarm.getLastEventId());
        values.put(AlarmColumns.LAST_EVENT_SEVERITY, alarm.getLastEventSeverity());
        values.put(AlarmColumns.NODE_ID, alarm.getNodeId());
        values.put(AlarmColumns.NODE_LABEL, alarm.getNodeLabel());
        values.put(AlarmColumns.SERVICE_TYPE_ID, alarm.getServiceTypeId());
        values.put(AlarmColumns.SERVICE_TYPE_NAME, alarm.getServiceTypeName());
        return contentResolver.insert(AlarmsListProvider.CONTENT_URI, values);
    }

    private void issueNewAlarmsNotification(int newAlarmsCount) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Constructs the Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(getString(R.string.alarms_notification_title))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission

        if (newAlarmsCount == 1) builder.setContentText(getString(R.string.alarms_notification_text_singular));
        else builder.setContentText(String.format(getString(R.string.alarms_notification_text_plural), newAlarmsCount));

        // Clicking the notification itself displays MainActivity.
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        /*
         * Because clicking the notification opens a new ("special") activity,
         * there's no need to create an artificial back stack.
         */
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(ALARM_NOTIFICATION_ID, builder.build());
    }

}
