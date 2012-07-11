package org.opennms.gsoc.nodes;

import java.util.ArrayList;
import java.util.List;

import org.opennms.gsoc.OpenNMSAndroidAppActivity;
import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsNode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
/**
 * The class represents the activity to take place when the nodes tab is
 * selected. It displays the nodes retrieved from the demo.opennms.org server.
 * 
 * @author melania galea
 * 
 */
public class NodesActivity extends SherlockActivity implements NodesListFragment.OnNodesListSelectedListener{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nodes_list);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
	}

	@Override
	public void onNodeSelected(OnmsNode node) {
		NodeViewerFragment viewer = (NodeViewerFragment) getFragmentManager()
	            .findFragmentById(R.id.details);

	    if (viewer == null || !viewer.isInLayout()) {
	        Intent showContent = new Intent(getApplicationContext(),
	        		NodeViewerActivity.class);
	        showContent.putExtra("onmsnode", node);
	        startActivity(showContent);
	    } else {
	        viewer.updateUrl(node);
	    }
	}
}
