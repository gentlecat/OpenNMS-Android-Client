package org.opennms.gsoc.alarms;

import org.opennms.gsoc.R;
import org.opennms.gsoc.model.OnmsAlarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class AlarmViewerFragment extends SherlockFragment{
	private View viewer = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.viewer = inflater.inflate(R.layout.alarm_view,
				container, false);
		this.viewer.setFocusableInTouchMode(true);
		this.viewer.requestFocus();

		return this.viewer;
	}

	public void updateUrl(OnmsAlarm newAlarm) {
		if(this.viewer != null) {
			TextView idTextView = (TextView)this.viewer.findViewById(R.id.alarmView);
			idTextView.setText(printAlarm(newAlarm));
		}
	}

	private String printAlarm(OnmsAlarm newAlarm) {
		StringBuilder builder = new StringBuilder();
		builder.append("Alarm Id : " + newAlarm.getId() + "\n");
		builder.append("Severity : " + newAlarm.getSeverity() + "\n");
		builder.append("Description : " + newAlarm.getDescription() + "\n");
		builder.append("Log Message : " + newAlarm.getLogMessage() + "\n");

		return builder.toString();
	}

}
