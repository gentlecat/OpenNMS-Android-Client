package org.opennms.android.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.opennms.android.communication.outages.OutagesServerCommunication;
import org.opennms.android.dao.Columns;
import org.opennms.android.dao.outages.Outage;
import org.opennms.android.dao.outages.OutagesListProvider;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class OutagesSyncService extends IntentService {

    private static final String TAG = "OutagesSyncService";

    public OutagesSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContentResolver contentResolver = getContentResolver();
        OutagesServerCommunication outagesServer = new OutagesServerCommunication(getApplicationContext());
        Log.i(TAG, "Synchronizing outages...");
        try {
            List<Outage> outages = outagesServer.getOutages("outages?orderBy=id&order=desc&limit=25", 20);
            contentResolver.delete(OutagesListProvider.CONTENT_URI, null, null);
            for (Outage outage : outages) insertOutage(contentResolver, outage);
            Log.i(TAG, "Done!");
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException", e);
        } catch (ExecutionException e) {
            Log.e(TAG, "ExecutionException", e);
        } catch (TimeoutException e) {
            Log.e(TAG, "TimeoutException", e);
        }
    }

    private Uri insertOutage(ContentResolver contentResolver, Outage outage) {
        ContentValues values = new ContentValues();
        values.put(Columns.OutageColumns.OUTAGE_ID, outage.getId());
        values.put(Columns.OutageColumns.IP_ADDRESS, outage.getIpAddress());
        values.put(Columns.OutageColumns.IF_REGAINED_SERVICE, outage.getIfRegainedService());
        values.put(Columns.OutageColumns.IF_LOST_SERVICE, outage.getIfRegainedService());
        values.put(Columns.OutageColumns.SERVICE_TYPE_NAME, outage.getServiceTypeName());
        return contentResolver.insert(OutagesListProvider.CONTENT_URI, values);
    }

}
