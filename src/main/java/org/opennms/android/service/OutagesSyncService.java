package org.opennms.android.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.parsing.OutagesParser;
import org.opennms.android.provider.Contract;

import java.util.ArrayList;

public class OutagesSyncService extends IntentService {

    private static final String TAG = "OutagesSyncService";

    public OutagesSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Synchronizing outages...");
        try {
            String result = new ServerCommunication(getApplicationContext()).get("outages?orderBy=id&order=desc&limit=25");
            ArrayList<ContentValues> values = OutagesParser.parse(result);
            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(Contract.Outages.CONTENT_URI, null, null); // Deleting old data
            contentResolver.bulkInsert(Contract.Outages.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
            Log.i(TAG, "Done!");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during synchronization process", e);
        }
    }

}
