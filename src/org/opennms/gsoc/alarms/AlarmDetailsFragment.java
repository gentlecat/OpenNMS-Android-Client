package org.opennms.gsoc.alarms;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.gsoc.R;
import org.opennms.gsoc.model.Alarm;

public class AlarmDetailsFragment extends SherlockFragment {

    private View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.alarm_details, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        return view;
    }

    public void show(Alarm alarm) {
        if (view != null) {
            // Alarm ID
            TextView id = (TextView) getActivity().findViewById(R.id.alarm_id);
            id.setText(getResources().getString(R.string.alarms_info_id) + alarm.getId());

            // Severity
            TextView severity = (TextView) getActivity().findViewById(R.id.alarm_severity);
            severity.setText(String.valueOf(alarm.getSeverity()));
            TableRow severityRow = (TableRow) getActivity().findViewById(R.id.alarm_severity_row);
            // TODO: Check for all possible conditions
            // TODO: Adjust colors
            if (alarm.getSeverity().equals("CLEARED")) {
                severityRow.setBackgroundColor(Color.GREEN);
            } else if (alarm.getSeverity().equals("MINOR")) {
                severityRow.setBackgroundColor(Color.YELLOW);
            } else if (alarm.getSeverity().equals("MAJOR")) {
                severityRow.setBackgroundColor(Color.RED);
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
