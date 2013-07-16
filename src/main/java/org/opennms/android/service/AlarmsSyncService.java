package org.opennms.android.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.R;
import org.opennms.android.communication.AlarmsParser;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.provider.Contract;
import org.opennms.android.ui.alarms.AlarmsActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AlarmsSyncService extends IntentService {

    private static final String TAG = "AlarmsSyncService";
    private static final int ALARM_NOTIFICATION_ID = 1;
    private static final int TIMEOUT_SEC = 25;

    public AlarmsSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Synchronizing alarms...");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int latestShownAlarmId = sharedPref.getInt("latest_shown_alarm_id", 0);
        String minimalSeverity = sharedPref.getString("minimal_severity", getString(R.string.default_minimal_severity));
        String[] severityValues = getResources().getStringArray(R.array.severity_values);
        int newAlarmsCount = 0, maxId = 0;

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> future = executorService.submit(
                new ServerCommunication(getApplicationContext(), "alarms?orderBy=id&order=desc&limit=0"));
        try {
            ContentResolver contentResolver = getContentResolver();
            ServiceResponse response = future.get(TIMEOUT_SEC, TimeUnit.SECONDS);
            ArrayList<ContentValues> values = AlarmsParser.parse(response.getContentData().getContentInString());
            contentResolver.delete(Contract.Alarms.CONTENT_URI, null, null); // Deleting old data
            contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
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
            Log.i(TAG, "Done!");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during synchronization process", e);
        }

        if (latestShownAlarmId != maxId) sharedPref.edit().putInt("latest_shown_alarm_id", maxId).commit();
        boolean notificationsOn = sharedPref.getBoolean("notifications_on", getResources().getBoolean(R.bool.default_notifications));
        if (newAlarmsCount > 0 && notificationsOn) issueNewAlarmsNotification(newAlarmsCount);
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
        Intent resultIntent = new Intent(this, AlarmsActivity.class);
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
