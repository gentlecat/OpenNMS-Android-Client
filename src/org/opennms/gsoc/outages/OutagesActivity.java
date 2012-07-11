package org.opennms.gsoc.outages;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsOutage;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class OutagesActivity extends SherlockActivity implements OutagesListFragment.OnOutagesListSelectedListener{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.outages_list);
	}

	@Override
	public void onOutageSelected(OnmsOutage outage) {
		OutageViewerFragment viewer = (OutageViewerFragment) getFragmentManager()
	            .findFragmentById(R.id.outagesDetails);

	    if (viewer == null || !viewer.isInLayout()) {
	        Intent showContent = new Intent(getApplicationContext(),
	        		OutageViewerActivity.class);
	        showContent.putExtra("onmsoutage", outage);
	        startActivity(showContent);
	    } else {
	        viewer.updateUrl(outage);
	    }
	}
}
