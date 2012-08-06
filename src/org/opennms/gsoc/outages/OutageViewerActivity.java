package org.opennms.gsoc.outages;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsOutage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class OutageViewerActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.outages_details);

		Intent launchingIntent = getIntent();
		OnmsOutage content = (OnmsOutage) launchingIntent.getSerializableExtra("onmsoutage");

		OutageViewerFragment viewer = (OutageViewerFragment) getFragmentManager()
				.findFragmentById(R.id.outagesDetails);

		viewer.updateUrl(content);
	}

	@Override
	public void onBackPressed() {
		Log.i("back pressed", "i'm pressed");
		this.finish();
	}
}
