package org.opennms.gsoc.about;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.opennms.gsoc.R;

public class SettingsViewerActivity extends SherlockFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_settings_details);
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

}
