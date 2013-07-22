package org.opennms.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import org.opennms.android.service.SyncAlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static final String TAG = "Utils";

    public static void enableNotifications(Context context) {
        enableNotifications(context, 0);
    }

    public static void enableNotifications(Context context, int triggerDelay) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Intent intent = new Intent(context, SyncAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int defaultRefreshRate = context.getResources().getInteger(R.integer.default_refresh_rate);
        int refreshRate = Integer.parseInt(sharedPref.getString("refresh_rate", String.valueOf(defaultRefreshRate)));

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + triggerDelay * DateUtils.SECOND_IN_MILLIS,
                refreshRate * DateUtils.MINUTE_IN_MILLIS,
                pendingIntent
        );
    }

    public static void disableNotifications(Context context) {
        Intent intent = new Intent(context, SyncAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    public static Date parseDate(String input) {
        // Example: "2013-07-22T04:03:04-04:00"
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = new Date();
        try {
            date = format.parse(input);
        } catch (ParseException e) {
            Log.e(TAG, "Date parsing error", e);
        }
        return date;
    }

}
