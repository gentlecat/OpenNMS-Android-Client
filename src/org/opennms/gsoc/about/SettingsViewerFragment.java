package org.opennms.gsoc.about;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.opennms.gsoc.R;
import org.opennms.gsoc.ServerConfiguration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class SettingsViewerFragment extends SherlockFragment{
	private ScrollView scrollView = null;
	private RelativeLayout mFormView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (this.scrollView == null) {
			this.scrollView = (ScrollView)inflater.inflate(R.layout.settings_view,
					container, false);
			this.mFormView = (RelativeLayout) this.scrollView.findViewById(R.id.form);

			updateContent();

			Button button = (Button) this.mFormView.findViewById(R.id.buttonSettings);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i("SettingsViewerFragment", "I was clicked");

					try{
						EditText hostEditText = (EditText)SettingsViewerFragment.this.mFormView.findViewById(R.id.editTextHost);
						InetAddress.getByName(hostEditText.getText().toString());
						ServerConfiguration.getInstance().setHost(hostEditText.getText().toString());

						EditText portEditText = (EditText)SettingsViewerFragment.this.mFormView.findViewById(R.id.editTextPort);
						ServerConfiguration.getInstance().setPort(Integer.parseInt(portEditText.getText().toString()));

						EditText pathEditText = (EditText)SettingsViewerFragment.this.mFormView.findViewById(R.id.editTextPath);
						ServerConfiguration.getInstance().setPath(pathEditText.getText().toString());

						EditText userEditText = (EditText)SettingsViewerFragment.this.mFormView.findViewById(R.id.editTextUser);
						ServerConfiguration.getInstance().setUsername(userEditText.getText().toString());

						EditText passEditText = (EditText)SettingsViewerFragment.this.mFormView.findViewById(R.id.editTextPass);
						ServerConfiguration.getInstance().setPassword(passEditText.getText().toString());


					} catch(UnknownHostException e) {
						createAlertWindow();
					}
				}
			});

			Button buttonCancel = (Button) this.mFormView.findViewById(R.id.buttonCancel);
			buttonCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SettingsViewerFragment.this.getActivity().finish();
				}
			});

		} else {
			ViewGroup parent = (ViewGroup) this.scrollView.getParent();
			parent.removeView(this.scrollView);
		}

		return this.scrollView;
	}

	private void createAlertWindow() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setMessage("Are you sure you want to exit?")
		.setCancelable(false)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				//SettingsViewerFragment.this.getActivity().finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void updateContent() {
		TextView hostTextView = (TextView)this.mFormView.findViewById(R.id.textViewHost);
		EditText hostEditText = (EditText)this.mFormView.findViewById(R.id.editTextHost);
		hostEditText.setText(ServerConfiguration.getInstance().getHost());

		TextView portTextView = (TextView)this.mFormView.findViewById(R.id.textViewPort);
		EditText portEditText = (EditText)this.mFormView.findViewById(R.id.editTextPort);
		portEditText.setText(ServerConfiguration.getInstance().getPort() + "");

		TextView pathTextView = (TextView)this.mFormView.findViewById(R.id.textViewPath);
		EditText pathEditText = (EditText)this.mFormView.findViewById(R.id.editTextPath);
		pathEditText.setText(ServerConfiguration.getInstance().getPath());

		TextView userTextView = (TextView)this.mFormView.findViewById(R.id.textViewUser);
		EditText usernameEditText = (EditText)this.mFormView.findViewById(R.id.editTextUser);
		usernameEditText.setText(ServerConfiguration.getInstance().getUsername());

		TextView passTextView = (TextView)this.mFormView.findViewById(R.id.textViewPass);
		EditText passwordEditText = (EditText)this.mFormView.findViewById(R.id.editTextPass);
		passwordEditText.setText(ServerConfiguration.getInstance().getPassword());

		TextView httpsTextView = (TextView)this.mFormView.findViewById(R.id.textViewHttps);
		Spinner feedbackSpinner = (Spinner)this.mFormView.findViewById(R.id.spinnerHttps);

	}
}
