package org.opennms.gsoc.nodes;

import java.util.ArrayList;
import java.util.List;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsNode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class NodesListFragment extends SherlockListFragment{
	private OnNodesListSelectedListener nodesListSelectedListener;
	private Intent intent;
	private ArrayAdapter<OnmsNode> adapter;
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		OnmsNode selection = (OnmsNode)l.getItemAtPosition(position);
	    nodesListSelectedListener.onNodeSelected(selection);
	}
	 
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new ArrayAdapter<OnmsNode>(getActivity().getApplicationContext(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new ArrayList<OnmsNode>());
		getListView().setAdapter(adapter);
					
		intent = new Intent(getActivity().getApplicationContext(), NodesService.class);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	nodesListSelectedListener = new OnNodesListSelectedListener() {
				
				@Override
				public void onNodeSelected(OnmsNode node) {
					NodeViewerFragment viewer = (NodeViewerFragment) getActivity().getFragmentManager()
				            .findFragmentById(R.id.details);

				    if (viewer == null || !viewer.isInLayout()) {
				        Intent showContent = new Intent(getActivity().getApplicationContext(),
				        		NodeViewerActivity.class);
				        showContent.putExtra("onmsnode", node);
				        startActivity(showContent);
				    } else {
				        viewer.updateUrl(node);
				    }
				}
			};
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNodesSelectedListener");
        }
    }
	
	public interface OnNodesListSelectedListener {
		void onNodeSelected(OnmsNode nodeUrl);
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
				Log.i("Nodes list fragment", s + "");
			}
		}
		getListView().setAdapter(adapter);
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.nodes_list, container, false);
	}
}