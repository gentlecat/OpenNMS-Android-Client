package org.opennms.gsoc.outages;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OutagesService extends Service{
	private static final String TAG = "OutagesService";
	public static final String BROADCAST_ACTION = "org.opennms.gsoc.outages";
	private Intent intent;
	public static final String OUTAGES_RESPONSE_STRING = "response";
	private OutagesServerCommunication outagesServer;

	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);
		outagesServer = new OutagesServerCommunicationImpl();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(TAG, "Service started...");
		getOutages();

	}

	public void getOutages() {
		intent.putExtra(OUTAGES_RESPONSE_STRING, outagesServer.getOutages("outages"));
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
