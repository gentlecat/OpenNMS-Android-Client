package org.opennms.android.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.parsing.EventsParser;
import org.opennms.android.provider.Contract;

import java.util.ArrayList;

public class EventsSyncService extends SyncService {

    private static final String TAG = "EventsSyncService";

    public EventsSyncService() {
        super(TAG);
    }

    @Override
    protected void synchronize() {
        String result;
        try {
            result = new ServerCommunication(getApplicationContext()).get("events?orderBy=id&order=desc&limit=25");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during synchronization process", e);
            return;
        }
        ArrayList<ContentValues> values = EventsParser.parse(result);
        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(Contract.Events.CONTENT_URI, null, null); // Deleting old data
        contentResolver.bulkInsert(Contract.Events.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
        Log.i(TAG, "Done!");
    }

}
