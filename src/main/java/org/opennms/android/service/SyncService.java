package org.opennms.android.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public abstract class SyncService extends IntentService {

    private final String tag;

    public SyncService(String name) {
        super(name);
        tag = name;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(tag, "Synchronizing...");
        if (isConnected()) {
            synchronize();
        } else {
            Log.w(tag, "No network connection");
        }
    }

    protected abstract void synchronize();

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
