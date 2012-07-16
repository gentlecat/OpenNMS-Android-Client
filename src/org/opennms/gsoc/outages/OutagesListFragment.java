package org.opennms.gsoc.outages;

import java.util.ArrayList;
import java.util.List;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsOutage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class OutagesListFragment extends SherlockListFragment{
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
        	outagesListSelectedListener = new OnOutagesListSelectedListener() {
				
				@Override
				public void onOutageSelected(OnmsOutage outage) {
					OutageViewerFragment viewer = (OutageViewerFragment) getActivity().getFragmentManager()
				            .findFragmentById(R.id.outagesDetails);

				    if (viewer == null || !viewer.isInLayout()) {
				        Intent showContent = new Intent(getActivity().getApplicationContext(),
				        		OutageViewerActivity.class);
				        showContent.putExtra("onmsoutage", outage);
				        startActivity(showContent);
				    } else {
				        viewer.updateUrl(outage);
				    }
					
				}
			};
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.outages_list, container, false);
	}
}
