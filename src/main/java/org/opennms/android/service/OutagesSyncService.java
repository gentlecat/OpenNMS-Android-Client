package org.opennms.android.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import com.google.resting.component.impl.ServiceResponse;
import org.opennms.android.communication.OutagesParser;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.provider.Contract;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class OutagesSyncService extends IntentService {

    private static final String TAG = "OutagesSyncService";
    private static final int TIMEOUT_SEC = 20;

    public OutagesSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Synchronizing outages...");

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ServiceResponse> future = executorService.submit(
                new ServerCommunication(getApplicationContext(), "outages?orderBy=id&order=desc&limit=25"));
        try {
            ContentResolver contentResolver = getContentResolver();
            ServiceResponse response = future.get(TIMEOUT_SEC, TimeUnit.SECONDS);
            ArrayList<ContentValues> values = OutagesParser.parse(response.getContentData().getContentInString());
            contentResolver.delete(Contract.Outages.CONTENT_URI, null, null); // Deleting old data
            contentResolver.bulkInsert(Contract.Outages.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
            Log.i(TAG, "Done!");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during synchronization process", e);
        }
    }

}
