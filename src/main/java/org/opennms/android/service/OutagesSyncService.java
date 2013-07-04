package org.opennms.android.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.opennms.android.communication.outages.OutagesServerCommunication;
import org.opennms.android.communication.outages.OutagesServerCommunicationImpl;
import org.opennms.android.dao.Columns;
import org.opennms.android.dao.outages.Outage;
import org.opennms.android.dao.outages.OutagesListProvider;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OutagesSyncService extends IntentService {

    private static final String TAG = "OutagesSyncService";

    public OutagesSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContentResolver contentResolver = getContentResolver();
        OutagesServerCommunication outagesServer = new OutagesServerCommunicationImpl(getApplicationContext());
        Log.d(TAG, "Synchronizing outages...");
        try {
            List<Outage> outages = outagesServer.getOutages("outages");
            contentResolver.delete(OutagesListProvider.CONTENT_URI, null, null);
            for (Outage outage : outages) insertOutage(contentResolver, outage);
        } catch (InterruptedException e) {
            Log.i(TAG, e.getMessage());
        } catch (ExecutionException e) {
            Log.i(TAG, e.getMessage());
        }
        Log.d(TAG, "Done!");
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
