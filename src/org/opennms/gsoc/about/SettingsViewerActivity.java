package org.opennms.gsoc.about;

import org.opennms.gsoc.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SettingsViewerActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_settings_details);

	}

	@Override
	public void onBackPressed() {
		Log.i("back pressed", "i'm pressed");
		this.finish();
	}
}
