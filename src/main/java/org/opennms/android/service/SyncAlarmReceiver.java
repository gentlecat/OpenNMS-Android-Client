package org.opennms.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import org.opennms.android.R;

public class SyncAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "SyncAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received!");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean("wifi_only", context.getResources().getBoolean(R.bool.wifi_only))) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifi.isConnected()) {
                startService(context);
            }
        } else {
            startService(context);
        }
    }

    private void startService(Context context) {
        Intent serviceIntent = new Intent(context, AlarmsSyncService.class);
        context.startService(serviceIntent);
    }

}