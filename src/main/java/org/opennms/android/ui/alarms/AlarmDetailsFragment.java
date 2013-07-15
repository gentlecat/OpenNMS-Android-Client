package org.opennms.android.ui.alarms;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.android.R;
import org.opennms.android.dao.Alarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmDetailsFragment extends SherlockFragment {
    public static final String TAG = "AlarmDetailsFragment";
    private Alarm alarm;

    // Do not remove
    public AlarmDetailsFragment() {
    }

    public AlarmDetailsFragment(Alarm alarm) {
        this.alarm = alarm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarm_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateContent();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO: Uncomment when feature is implemented
        //inflater.inflate(R.menu.alarm, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_acknowledge_alarm:
                // TODO: Implement
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateContent() {
        if (alarm != null) {
            // Alarm ID
            TextView id = (TextView) getActivity().findViewById(R.id.alarm_id);
            id.setText(getString(R.string.alarm_details_id) + alarm.getId());

            // Severity
            TextView severity = (TextView) getActivity().findViewById(R.id.alarm_severity);
            severity.setText(String.valueOf(alarm.getSeverity()));
            LinearLayout severityRow = (LinearLayout) getActivity().findViewById(R.id.alarm_severity_row);
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
            TextView message = (TextView) getActivity().findViewById(R.id.alarm_log_message);
            message.setText(alarm.getLogMessage());

            // Node
            TextView node = (TextView) getActivity().findViewById(R.id.alarm_node);
            node.setText(alarm.getNodeLabel() + " (#" + alarm.getNodeId() + ")");

            // Service type
            TextView serviceType = (TextView) getActivity().findViewById(R.id.alarm_service_type);
            serviceType.setText(alarm.getServiceTypeName() + " (#" + alarm.getServiceTypeId() + ")");

            // Last event
            String lastEventTimeString = alarm.getLastEventTime();
            // Example: "2011-09-27T12:15:32.363-04:00"
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Date lastEventTime = new Date();
            if (lastEventTimeString != null) {
                try {
                    lastEventTime = format.parse(lastEventTimeString);
                } catch (ParseException e) {
                    Log.e(TAG, "Creation time parsing error");
                }
            }
            TextView lastEvent = (TextView) getActivity().findViewById(R.id.alarm_last_event);
            lastEvent.setText("#" + alarm.getLastEventId() + " " + alarm.getLastEventSeverity() + "\n" + lastEventTime.toString());
        }
    }

}