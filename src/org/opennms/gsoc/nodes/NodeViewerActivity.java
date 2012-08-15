package org.opennms.gsoc.nodes;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsNode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class NodeViewerActivity extends SherlockFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);

		Intent launchingIntent = getIntent();
		OnmsNode content = (OnmsNode) launchingIntent.getSerializableExtra("onmsnode");

		NodeViewerFragment viewer = (NodeViewerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details);

		viewer.updateUrl(content);
	}

	@Override
	public void onBackPressed() {
		Log.i("back pressed", "i'm pressed");
		this.finish();
	}

}
