package org.opennms.gsoc.outages;

import java.util.ArrayList;
import java.util.List;

import org.opennms.gsoc.model.OnmsNode;
import org.opennms.gsoc.model.OnmsOutage;
import org.opennms.gsoc.nodes.NodesListFragment.OnNodesListSelectedListener;

import android.app.Activity;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OutagesListFragment extends ListFragment{
	private ArrayAdapter<OnmsOutage> adapter;
	private Intent intent;
	private OnOutagesListSelectedListener outagesListSelectedListener;
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		OnmsOutage selection = (OnmsOutage)l.getItemAtPosition(position);
	    outagesListSelectedListener.onOutageSelected(selection);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new ArrayAdapter<OnmsOutage>(getActivity().getApplicationContext(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new ArrayList<OnmsOutage>());
		this.setListAdapter(adapter);
		intent = new Intent(getActivity().getApplicationContext(), OutagesService.class);
	}
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

	public interface OnOutagesListSelectedListener {
        void onOutageSelected(OnmsOutage outage);
    }
	
	private void updateUI(Intent intent) {
		adapter.clear();
		List<OnmsOutage> values = (List<OnmsOutage>)intent.getSerializableExtra(OutagesService.OUTAGES_RESPONSE_STRING);

		if (values != null) {

			for (OnmsOutage s : values) {
				adapter.add(s);
			}
		}

		this.setListAdapter(adapter);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	outagesListSelectedListener = (OnOutagesListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOutagesSelectedListener");
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getApplicationContext().startService(intent);
		getActivity().getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter(
				OutagesService.BROADCAST_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
		getActivity().getApplicationContext().stopService(intent);
	}
}
