package org.opennms.gsoc.nodes;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsNode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NodeViewerActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);

		Intent launchingIntent = getIntent();
		OnmsNode content = (OnmsNode) launchingIntent.getSerializableExtra("onmsnode");

		NodeViewerFragment viewer = (NodeViewerFragment) getFragmentManager()
				.findFragmentById(R.id.details);

		viewer.updateUrl(content);
	}

	@Override
	public void onBackPressed() {
		Log.i("back pressed", "i'm pressed");
		this.finish();
	}

}
