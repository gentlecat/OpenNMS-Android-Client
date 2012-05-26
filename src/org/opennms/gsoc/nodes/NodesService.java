package org.opennms.gsoc.nodes;

import org.opennms.gsoc.ServerConfiguration;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

public class NodesService extends Service {

	private static final String TAG = "NodesService";
	public static final String BROADCAST_ACTION = "org.opennms.gsoc.nodes";
	private Intent intent;
	public static final String NODES_RESPONSE_STRING = "response";
	private ServerConfiguration serverConfiguration = ServerConfiguration
			.getInstance();
	private NodesServerCommunication nodesServer;

	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);
		nodesServer = new NodesServerCommunicationImpl();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(TAG, "Service started...");
		getNodes();

	}

	public void getNodes() {
		intent.putExtra(NODES_RESPONSE_STRING, nodesServer.getNodes("nodes"));
		sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Service stopped...");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
