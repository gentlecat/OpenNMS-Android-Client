package org.opennms.gsoc.about;

import org.opennms.gsoc.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * Class contains the activity performed when the about tab is selected.
 * @author melania galea
 *
 */

public class AboutFragment extends SherlockFragment{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.about_settings, container, false);

		Button button = (Button)v.findViewById(R.id.buttonSettings);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingsViewerFragment viewer = (SettingsViewerFragment)getActivity().getFragmentManager()
						.findFragmentById(R.id.about_settings_details);

				if (viewer == null || !viewer.isInLayout()) {
					Intent showContent = new Intent(getActivity().getApplicationContext(),
							SettingsViewerActivity.class);
					startActivity(showContent);
				} 
			}
		});

		return v;
	}
}
