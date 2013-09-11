package org.opennms.android;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.opennms.android.sync.LoadManager;

public class MainApplication extends Application {

    public LoadManager loadManager;
    public boolean serviceConnected = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            loadManager = ((LoadManager.LocalBinder) service).getService();
            serviceConnected = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            loadManager = null;
            serviceConnected = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Intent serviceIntent = new Intent(this, LoadManager.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

}
