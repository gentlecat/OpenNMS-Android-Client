package org.opennms.gsoc.nodes;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * The class represents the activity to take place when the nodes tab is
 * selected. It displays the nodes retrieved from the demo.opennms.org server.
 * 
 * @author melania galea
 * 
 */
public class NodesActivity extends SherlockActivity {
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private Intent intent;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = new ListView(this);
		setContentView(listView);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new ArrayList<String>());

		listView.setAdapter(adapter);
		intent = new Intent(this, NodesService.class);
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

	private void updateUI(Intent intent) {
		adapter.clear();
		List<String> values = intent
				.getStringArrayListExtra(NodesService.NODES_RESPONSE_STRING);

		if (values != null) {

			for (String s : values) {
				adapter.add(s);
			}
		}

		listView.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		startService(intent);
		registerReceiver(broadcastReceiver, new IntentFilter(
				NodesService.BROADCAST_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		stopService(intent);
	}
}
