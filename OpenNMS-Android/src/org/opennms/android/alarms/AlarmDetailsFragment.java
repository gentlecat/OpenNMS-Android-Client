package org.opennms.android.alarms;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.android.R;

public class AlarmDetailsFragment extends SherlockFragment {

    Alarm alarm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarm_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateContent();
    }

    public void bindAlarm(Alarm alarm) {
        this.alarm = alarm;
        if (this.isVisible()) updateContent();
    }

    public void updateContent() {
        if (alarm != null) {
            // Alarm ID
            TextView id = (TextView) getActivity().findViewById(R.id.alarm_id);
            id.setText(getString(R.string.alarms_details_id) + alarm.getId());

            // Severity
            TextView severity = (TextView) getActivity().findViewById(R.id.alarm_severity);
            severity.setText(String.valueOf(alarm.getSeverity()));
            TableRow severityRow = (TableRow) getActivity().findViewById(R.id.alarm_severity_row);
            if (alarm.getSeverity().equals("CLEARED")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_cleared));
            } else if (alarm.getSeverity().equals("MINOR")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_minor));
            } else if (alarm.getSeverity().equals("NORMAL")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_normal));
            } else if (alarm.getSeverity().equals("INDETERMINATE")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_minor));
            } else if (alarm.getSeverity().equals("WARNING")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_warning));
            } else if (alarm.getSeverity().equals("MAJOR")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_major));
            } else if (alarm.getSeverity().equals("CRITICAL")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_critical));
            }

            // Description
            TextView description = (TextView) getActivity().findViewById(R.id.alarm_description);
            description.setText(Html.fromHtml(alarm.getDescription()));

            // Log message
            TextView message = (TextView) getActivity().findViewById(R.id.alarm_message);
            message.setText(String.valueOf(alarm.getLogMessage()));
        }
    }

}