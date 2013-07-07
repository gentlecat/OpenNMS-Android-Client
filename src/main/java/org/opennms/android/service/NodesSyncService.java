package org.opennms.android.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.opennms.android.communication.nodes.NodesServerCommunication;
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
        NodesServerCommunication nodesServer = new NodesServerCommunication(getApplicationContext());
        Log.i(TAG, "Synchronizing nodes...");
        try {
            List<Node> nodes = nodesServer.getNodes("nodes/?limit=0");
            contentResolver.delete(NodesListProvider.CONTENT_URI, null, null);
            for (Node node : nodes) insertNode(contentResolver, node);
            Log.i(TAG, "Done!");
        } catch (UnknownHostException e) {
            Log.e(TAG, "UnknownHostException", e);
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException", e);
        } catch (ExecutionException e) {
            Log.e(TAG, "ExecutionException", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
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
