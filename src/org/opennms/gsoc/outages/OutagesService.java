package org.opennms.gsoc.outages;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.opennms.gsoc.dao.OnmsDatabaseHelper;
import org.opennms.gsoc.model.OnmsOutage;
import org.opennms.gsoc.outages.dao.OutagesListProvider;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OutagesService extends Service{
	private static final String TAG = "OutagesService";
	private OutagesServerCommunication outagesServer;

	@Override
	public void onCreate() {
		super.onCreate();
		this.outagesServer = new OutagesServerCommunicationImpl(getApplicationContext());
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(OutagesService.TAG, "Service started...");
		try {
			getOutages();
		} catch (InterruptedException e) {
			Log.i(OutagesService.TAG, e.getMessage());
		} catch (ExecutionException e) {
			Log.i(OutagesService.TAG, e.getMessage());
		}

	}

	public void getOutages() throws InterruptedException, ExecutionException {
		List<OnmsOutage> outages = this.outagesServer.getOutages("outages");
		for(OnmsOutage outage : outages) {
			ContentValues tutorialData = new ContentValues();
			tutorialData.put(OnmsDatabaseHelper.COL_OUTAGE_ID, outage.getId());
			tutorialData.put(OnmsDatabaseHelper.COL_IP_ADDRESS, outage.getIpAddress());
			tutorialData.put(OnmsDatabaseHelper.COL_IF_REGAINED_SERVICE, outage.getIfRegainedService());
			tutorialData.put(OnmsDatabaseHelper.COL_SERVICE_TYPE_NAME, outage.getServiceTypeName());
			getContentResolver().insert(OutagesListProvider.CONTENT_URI, tutorialData);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(OutagesService.TAG, "Service stopped...");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
