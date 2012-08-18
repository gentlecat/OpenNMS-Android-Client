package org.opennms.gsoc.alarms;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsAlarm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class AlarmViewerActivity extends SherlockFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarms_details);

		Intent launchingIntent = getIntent();
		OnmsAlarm content = (OnmsAlarm) launchingIntent.getSerializableExtra("onmsalarm");

		AlarmViewerFragment viewer = (AlarmViewerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.alarmsDetails);

		viewer.updateUrl(content);
	}

	@Override
	public void onBackPressed() {
		Log.i("back pressed", "i'm pressed");
		this.finish();
	}

}
