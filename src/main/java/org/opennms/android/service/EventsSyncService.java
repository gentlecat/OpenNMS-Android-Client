package org.opennms.android.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.communication.EventsParser;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.provider.Contract;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class EventsSyncService extends IntentService {

    private static final String TAG = "EventsSyncService";
    private static final int TIMEOUT_SEC = 20;

    public EventsSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Synchronizing events...");

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> future = executorService.submit(
                new ServerCommunication(getApplicationContext(), "events?orderBy=id&order=desc&limit=25"));
        try {
            ContentResolver contentResolver = getContentResolver();
            ServiceResponse response = future.get(TIMEOUT_SEC, TimeUnit.SECONDS);
            ArrayList<ContentValues> values = EventsParser.parse(response.getContentData().getContentInString());
            contentResolver.delete(Contract.Events.CONTENT_URI, null, null); // Deleting old data
            contentResolver.bulkInsert(Contract.Events.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
            Log.i(TAG, "Done!");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during synchronization process", e);
        }
    }

}
