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
    	
    	viewer = (View) inflater.inflate(R.layout.settings_view,
                container, false);
    	viewer.setFocusableInTouchMode(true);
    	viewer.requestFocus();
    	updateContent();
    	
        return viewer;
    }

	public void updateContent() {
		EditText idEditText = (EditText)viewer.findViewById(R.id.editTextHost);
		idEditText.setText(ServerConfiguration.getInstance().getHost());
		
	}
}
