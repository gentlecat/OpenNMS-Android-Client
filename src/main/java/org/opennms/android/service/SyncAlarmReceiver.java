package org.opennms.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import org.opennms.android.R;
import org.opennms.android.ui.alarms.AlarmsActivity;

public class SyncAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "SyncAlarmReceiver";
    private static final int WARNING_NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received!");
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPref.getBoolean("wifi_only", context.getResources().getBoolean(R.bool.wifi_only))) {
                NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifi.isConnected()) startService(context);
                else
                    issueNotification(context,
                            context.getString(R.string.sync_failed_notif_title),
                            context.getString(R.string.sync_failed_notif_text_wifi));
            } else {
                startService(context);
            }
        } else {
            issueNotification(context,
                    context.getString(R.string.sync_failed_notif_title),
                    context.getString(R.string.sync_failed_notif_text));
        }
    }

    private void startService(Context context) {
        Intent serviceIntent = new Intent(context, AlarmsSyncService.class);
        context.startService(serviceIntent);
    }

    private void issueNotification(Context context, String title, String text) {
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