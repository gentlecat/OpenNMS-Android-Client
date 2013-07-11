package org.opennms.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "SyncAlarmReceiver";
    private static final int DELAY_SECONDS = 20;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean("notifications_on", context.getResources().getBoolean(R.bool.default_notifications))) {
            Log.i(TAG, "Enabling notifications");
            Utils.enableNotifications(context, DELAY_SECONDS);
        }
    }

}