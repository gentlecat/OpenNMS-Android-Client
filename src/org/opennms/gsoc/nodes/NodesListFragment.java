package org.opennms.gsoc.nodes;

import java.util.ArrayList;
import java.util.List;

import org.opennms.gsoc.nodes.model.OnmsNode;

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

public class NodesListFragment extends ListFragment{
	private OnNodesListSelectedListener nodesListSelectedListener;
	private Intent intent;
	private ArrayAdapter<OnmsNode> adapter;
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		OnmsNode selection = (OnmsNode)l.getItemAtPosition(position);
	    nodesListSelectedListener.onNodeSelected(selection);
	}
	 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new ArrayAdapter<OnmsNode>(getActivity().getApplicationContext(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new ArrayList<OnmsNode>());
		setListAdapter(adapter);

		intent = new Intent(getActivity().getApplicationContext(), NodesService.class);
	}

	public interface OnNodesListSelectedListener {
        void onNodeSelected(OnmsNode nodeUrl);
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	nodesListSelectedListener = (OnNodesListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTutSelectedListener");
        }
    }
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

	private void updateUI(Intent intent) {
		adapter.clear();
		List<OnmsNode> values = (List<OnmsNode>) intent.getSerializableExtra(NodesService.NODES_RESPONSE_STRING);

		if (values != null) {

			for (OnmsNode s : values) {
				adapter.add(s);
			}
		}

		this.setListAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getApplicationContext().startService(intent);
		getActivity().getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter(
				NodesService.BROADCAST_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
		getActivity().getApplicationContext().stopService(intent);
	}
}
