package org.opennms.android.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import org.opennms.android.R;
import org.opennms.android.ui.MainActivity;

public class NotificationService extends IntentService {
    NotificationCompat.Builder builder;

    public NotificationService() {
        // The super call is required. The background thread that IntentService
        // starts is labeled with the string argument you pass.
        super("org.opennms.android");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        issueNotification(intent);
    }

    private void issueNotification(Intent intent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Constructs the Builder object.
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission

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

        // Including the notification ID allows you to update the notification later on.
        notificationManager.notify(1, builder.build());
    }

}
