package org.opennms.gsoc.outages;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsOutage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class OutageViewerActivity extends SherlockFragmentActivity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.outages_details);

		Intent launchingIntent = getIntent();
		OnmsOutage content = (OnmsOutage) launchingIntent.getSerializableExtra("onmsoutage");

		OutageViewerFragment viewer = (OutageViewerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.outagesDetails);

		viewer.updateUrl(content);
	}

	@Override
	public void onBackPressed() {
		Log.i("back pressed", "i'm pressed");
		this.finish();
	}
}
