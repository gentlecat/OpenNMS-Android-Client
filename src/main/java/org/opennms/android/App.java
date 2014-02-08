package org.opennms.android;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.opennms.android.data.sync.UpdateManager;

import dagger.ObjectGraph;

public class App extends Application {
    private ObjectGraph objectGraph;

    public UpdateManager loadManager;
    public boolean serviceConnected = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            loadManager = ((UpdateManager.LocalBinder) service).getService();
            serviceConnected = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            loadManager = null;
            serviceConnected = false;
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // Initializing objectGraph here because we'll need it early in ContentProvider.
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent serviceIntent = new Intent(this, UpdateManager.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

}
