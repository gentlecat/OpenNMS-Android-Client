package org.opennms.android.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.opennms.android.communication.nodes.NodesServerCommunication;
import org.opennms.android.communication.nodes.NodesServerCommunicationImpl;
import org.opennms.android.dao.Columns;
import org.opennms.android.dao.nodes.Node;
import org.opennms.android.dao.nodes.NodesListProvider;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NodesSyncService extends IntentService {

    private static final String TAG = "NodesSyncService";

    public NodesSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContentResolver contentResolver = getContentResolver();
        NodesServerCommunication nodesServer = new NodesServerCommunicationImpl(getApplicationContext());
        Log.d(TAG, "Synchronizing nodes...");
        try {
            List<Node> nodes = nodesServer.getNodes("nodes");
            for (Node node : nodes) insertNode(contentResolver, node);
        } catch (UnknownHostException e) {
            Log.i(TAG, e.getMessage());
            contentResolver.delete(NodesListProvider.CONTENT_URI, null, null);
        } catch (InterruptedException e) {
            Log.i(TAG, e.getMessage());
            contentResolver.delete(NodesListProvider.CONTENT_URI, null, null);
        } catch (ExecutionException e) {
            Log.i(TAG, e.getMessage());
            contentResolver.delete(NodesListProvider.CONTENT_URI, null, null);
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            contentResolver.delete(NodesListProvider.CONTENT_URI, null, null);
        }
        Log.d(TAG, "Done!");
    }

    private Uri insertNode(ContentResolver contentResolver, Node node) {
        ContentValues values = new ContentValues();
        values.put(Columns.NodeColumns.COL_NODE_ID, node.getId());
        values.put(Columns.NodeColumns.COL_TYPE, node.getType());
        values.put(Columns.NodeColumns.COL_LABEL, node.getLabel());
        values.put(Columns.NodeColumns.COL_CREATED_TIME, node.getCreateTime());
        values.put(Columns.NodeColumns.COL_SYS_CONTACT, node.getSysContact());
        values.put(Columns.NodeColumns.COL_LABEL_SOURCE, node.getLabelSource());
        return contentResolver.insert(NodesListProvider.CONTENT_URI, values);
    }

}
