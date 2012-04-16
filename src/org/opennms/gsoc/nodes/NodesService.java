package org.opennms.gsoc.nodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

public class NodesService extends Service {

	private static final String TAG = "NodesService";
	private static final String URL = "http://demo.opennms.org/opennms/rest/nodes";
	private static final String USERNAME = "demo";
	private static final String PASSWORD = "demo";
	public static final String BROADCAST_ACTION = "org.opennms.gsoc.nodes";
	private Intent intent;
	public static final String NODES_RESPONSE_STRING = "response";

	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(TAG, "Service started...");
		getNodes();

	}
	
	public void getNodes() {
		RetrieveNodesAsyncTask task = new RetrieveNodesAsyncTask();
		task.execute();

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

	private class RetrieveNodesAsyncTask extends
			AsyncTask<Void, Void, HttpResponse> {

		protected HttpResponse doInBackground(Void... params) {
			HttpGet httpget;
			httpget = new HttpGet(URL);

			String auth = new String(Base64.encode(
					(USERNAME + ":" + PASSWORD).getBytes(), Base64.URL_SAFE
							| Base64.NO_WRAP));
			httpget.addHeader("Authorization", "Basic " + auth);

			HttpClient client = new DefaultHttpClient();
			HttpResponse response = null;
			try {
				response = client.execute(httpget);
			} catch (ClientProtocolException e1) {
				Log.i("NodesActivity HttpResponse", e1.getMessage());
			} catch (IOException e1) {
				Log.i("NodesActivity HttpResponse ", e1.getMessage());
			}

			return response;
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			ArrayList<String> values = null;
			if (response != null) {
				HttpEntity entity = response.getEntity();
				InputStream is = null;
				if (entity != null) {
					try {
						is = entity.getContent();
					} catch (IllegalStateException e2) {
						Log.i("NodesActivity : entity.getContent()",
								e2.getMessage());
					} catch (IOException e2) {
						Log.i("NodesActivity : entity.getContent()",
								e2.getMessage());
					}

					values = NodesParser.parse(is);
				}
			}

			intent.putExtra(NODES_RESPONSE_STRING, values);
			sendBroadcast(intent);
		}
	}

}
