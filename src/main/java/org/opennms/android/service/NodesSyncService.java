package org.opennms.android.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.parsing.NodesParser;
import org.opennms.android.provider.Contract;

import java.util.ArrayList;

public class NodesSyncService extends SyncService {

    private static final String TAG = "NodesSyncService";

    public NodesSyncService() {
        super(TAG);
    }

    @Override
    protected void synchronize() {
        String result;
        try {
            result = new ServerCommunication(getApplicationContext()).get("nodes/?limit=0");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during synchronization process", e);
            return;
        }
        ArrayList<ContentValues> values = NodesParser.parse(result);
        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(Contract.Nodes.CONTENT_URI, null, null); // Deleting old data
        contentResolver.bulkInsert(Contract.Nodes.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
        Log.i(TAG, "Done!");
    }

}
