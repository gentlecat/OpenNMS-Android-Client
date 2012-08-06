package org.opennms.gsoc.about;

import org.opennms.gsoc.R;
import org.opennms.gsoc.ServerConfiguration;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SettingsViewerFragment extends Fragment{
	private View viewer = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.viewer = inflater.inflate(R.layout.settings_view,
				container, false);
		this.viewer.setFocusableInTouchMode(true);
		this.viewer.requestFocus();
		updateContent();

		return this.viewer;
	}

	public void updateContent() {
		EditText idEditText = (EditText)this.viewer.findViewById(R.id.editTextHost);
		idEditText.setText(ServerConfiguration.getInstance().getHost());

	}
}
