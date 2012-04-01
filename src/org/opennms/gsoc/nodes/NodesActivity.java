package org.opennms.gsoc.nodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * The class represents the activity to take place when the nodes tab is
 * selected. It displays the nodes retrieved from the demo.opennms.org server.
 * 
 * @author melania galea
 * 
 */
public class NodesActivity extends Activity {
	private static final String URL = "http://demo.opennms.org:8980/opennms/rest/nodes";
	private static final String USERNAME = "demo";
	private static final String PASSWORD = "demo";
	private ListView listView;
	private ArrayAdapter<String> adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = new ListView(this);
		setContentView(listView);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new ArrayList<String>());

		listView.setAdapter(adapter);
		getNodes();

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
			if (response != null) {
				HttpEntity entity = response.getEntity();
				InputStream is = null;
				if (entity != null) {
					try {
						is = entity.getContent();
					} catch (IllegalStateException e2) {
						Log.i("NodesActivity : entity.getContent()", e2.getMessage());
					} catch (IOException e2) {
						Log.i("NodesActivity : entity.getContent()", e2.getMessage());
					}

					List<String> values = NodesParser.parse(is);

					for (String s : values) {
						adapter.add(s);
					}
				} else {
					adapter.add("There are no nodes to display");
				}
			} else {
				adapter.add("The connection with the server couldn't be establish");
			}
			listView.setAdapter(adapter);

		}
	}

	public void getNodes() {
		RetrieveNodesAsyncTask task = new RetrieveNodesAsyncTask();
		task.execute();

	}

}
