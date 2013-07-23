package org.opennms.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import org.opennms.android.Utils;

public abstract class SyncService extends IntentService {

    private final String tag;

    public SyncService(String name) {
        super(name);
        tag = name;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(tag, "Synchronizing...");
        if (Utils.isOnline(getApplicationContext())) {
            synchronize();
        } else {
            Log.w(tag, "No network connection");
        }
    }

    protected abstract void synchronize();

}
