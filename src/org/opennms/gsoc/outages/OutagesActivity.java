package org.opennms.gsoc.outages;

import java.util.ArrayList;
import java.util.List;

import org.opennms.gsoc.nodes.model.OnmsOutage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;

public class OutagesActivity extends SherlockActivity{
	private ListView listView;
	private ArrayAdapter<OnmsOutage> adapter;
	private Intent intent;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = new ListView(this);
		setContentView(listView);
		adapter = new ArrayAdapter<OnmsOutage>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new ArrayList<OnmsOutage>());

		listView.setAdapter(adapter);
		intent = new Intent(this, OutagesService.class);
	}
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

	private void updateUI(Intent intent) {
		adapter.clear();
		List<OnmsOutage> values = (List<OnmsOutage>)intent.getSerializableExtra(OutagesService.OUTAGES_RESPONSE_STRING);

		if (values != null) {

			for (OnmsOutage s : values) {
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
				OutagesService.BROADCAST_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		stopService(intent);
	}
}
