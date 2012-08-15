package org.opennms.gsoc.nodes;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.opennms.gsoc.dao.OnmsDatabaseHelper;
import org.opennms.gsoc.model.OnmsNode;
import org.opennms.gsoc.nodes.dao.NodesListProvider;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NodesService extends Service {

	private static final String TAG = "NodesService";
	private NodesServerCommunication nodesServer;

	@Override
	public void onCreate() {
		super.onCreate();
		this.nodesServer = new NodesServerCommunicationImpl();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(NodesService.TAG, "Service started...");
		try {
			getNodes();
		} catch (UnknownHostException e) {
			Log.i(NodesService.TAG, e.getMessage());
			getContentResolver().delete(NodesListProvider.CONTENT_URI, null, null);
		} catch (InterruptedException e) {
			Log.i(NodesService.TAG, e.getMessage());
			getContentResolver().delete(NodesListProvider.CONTENT_URI, null, null);
		} catch (ExecutionException e) {
			Log.i(NodesService.TAG, e.getMessage());
			getContentResolver().delete(NodesListProvider.CONTENT_URI, null, null);
		}catch (IOException e) {
			Log.i(NodesService.TAG, e.getMessage());
			getContentResolver().delete(NodesListProvider.CONTENT_URI, null, null);
		}

	}

	public void getNodes() throws InterruptedException, ExecutionException, IOException {
		List<OnmsNode> nodes = this.nodesServer.getNodes("nodes");
		for(OnmsNode node : nodes) {
			ContentValues tutorialData = new ContentValues();
			tutorialData.put(OnmsDatabaseHelper.COL_NODE_ID, node.getId());
			tutorialData.put(OnmsDatabaseHelper.COL_TYPE, node.getType());
			tutorialData.put(OnmsDatabaseHelper.COL_LABEL, node.getLabel());
			getContentResolver().insert(NodesListProvider.CONTENT_URI, tutorialData);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(NodesService.TAG, "Service stopped...");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
