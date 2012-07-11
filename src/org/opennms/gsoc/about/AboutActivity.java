package org.opennms.gsoc.about;

import org.opennms.gsoc.R;
import org.opennms.gsoc.nodes.NodeViewerActivity;
import org.opennms.gsoc.nodes.NodeViewerFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * Class contains the activity performed when the about tab is selected.
 * @author melania galea
 *
 */
		
public class AboutActivity extends SherlockActivity{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_settings);
        
        Button button = (Button)findViewById(R.id.buttonSettings);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SettingsViewerFragment viewer = (SettingsViewerFragment) getFragmentManager()
			            .findFragmentById(R.id.about_settings_details);

			    if (viewer == null || !viewer.isInLayout()) {
			        Intent showContent = new Intent(getApplicationContext(),
			        		SettingsViewerActivity.class);
			        startActivity(showContent);
			    } 
			}
		});
        
}

}
