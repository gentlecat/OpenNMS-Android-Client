package org.opennms.gsoc.about;

import java.net.InetAddress;

import org.opennms.gsoc.R;
import org.opennms.gsoc.ServerConfiguration;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

		checkHost();
		//createAlertWindow();

		Button button = (Button)v.findViewById(R.id.buttonSettings);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingsViewerFragment viewer = (SettingsViewerFragment)getActivity().getSupportFragmentManager()
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

	private void checkHost() {
		try { 
			InetAddress.getByName(ServerConfiguration.getInstance().getHost());
		}catch(Exception e) {
			createAlertWindow();
		}
	}

	private void createAlertWindow() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setMessage("The host " + ServerConfiguration.getInstance().getHost() + " is unreachable.")
		.setCancelable(false)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				//AboutFragment.this.getActivity().finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
