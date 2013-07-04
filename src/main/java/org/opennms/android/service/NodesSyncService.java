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
            contentResolver.delete(NodesListProvider.CONTENT_URI, null, null);
            for (Node node : nodes) insertNode(contentResolver, node);
        } catch (UnknownHostException e) {
            Log.i(TAG, e.getMessage());
        } catch (InterruptedException e) {
            Log.i(TAG, e.getMessage());
        } catch (ExecutionException e) {
            Log.i(TAG, e.getMessage());
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }
        Log.d(TAG, "Done!");
    }

    private Uri insertNode(ContentResolver contentResolver, Node node) {
        ContentValues values = new ContentValues();
        values.put(Columns.NodeColumns.NODE_ID, node.getId());
        values.put(Columns.NodeColumns.TYPE, node.getType());
        values.put(Columns.NodeColumns.NAME, node.getName());
        values.put(Columns.NodeColumns.CREATED_TIME, node.getCreateTime());
        values.put(Columns.NodeColumns.SYS_CONTACT, node.getSysContact());
        values.put(Columns.NodeColumns.LABEL_SOURCE, node.getLabelSource());
        values.put(Columns.NodeColumns.DESCRIPTION, node.getDescription());
        values.put(Columns.NodeColumns.LOCATION, node.getLocation());
        return contentResolver.insert(NodesListProvider.CONTENT_URI, values);
    }

}
