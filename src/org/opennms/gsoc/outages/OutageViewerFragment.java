package org.opennms.gsoc.outages;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsOutage;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class OutageViewerFragment extends Fragment{
	private View viewer = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.viewer = inflater.inflate(R.layout.outage_view,
				container, false);
		this.viewer.setFocusableInTouchMode(true);
		this.viewer.requestFocus();

		return this.viewer;
	}

	public void updateUrl(OnmsOutage newOutage) {
		if(this.viewer != null) {
			TextView idTextView = (TextView)this.viewer.findViewById(R.id.outageView);
			idTextView.setText(printOutage(newOutage));
		}
	}

	private String printOutage(OnmsOutage newNode) {
		StringBuilder builder = new StringBuilder();
		builder.append("OnmsOutage Id : " + newNode.getId());
		return builder.toString();
	}
}
