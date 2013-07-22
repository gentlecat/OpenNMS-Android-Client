package org.opennms.android.ui.alarms;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.communication.ServerCommunication;
import org.opennms.android.provider.Contract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmDetailsFragment extends SherlockFragment {

    public static final String TAG = "AlarmDetailsFragment";
    private long alarmId;
    private Cursor cursor;

    // Do not remove
    public AlarmDetailsFragment() {
    }

    public AlarmDetailsFragment(long alarmId) {
        this.alarmId = alarmId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(Contract.Alarms.CONTENT_URI, String.valueOf(alarmId)),
                null, null, null, null);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStop() {
        cursor.close();
        super.onStop();
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
        if (cursor.moveToFirst()) {
            String ackTime = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.ACK_TIME));
            if (ackTime == null) {
                if (menu.findItem(R.id.menu_acknowledge_alarm) == null) inflater.inflate(R.menu.alarm, menu);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_acknowledge_alarm:
                acknowledge(alarmId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void acknowledge(final long alarmId) {
        // TODO: Hide menu item
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new ServerCommunication(getActivity()).put(String.format("alarms/%d?ack=true", alarmId));
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), "Alarm #" + alarmId + " has been acknowledged.", Toast.LENGTH_LONG).show();
                        }
                    });
                    // TODO: Refresh details view
                } catch (Exception e) {
                    Log.e(TAG, "Error occurred during acknowledgement process!", e);
                }
            }
        }).start();
    }

    public void updateContent() {
        if (cursor.moveToFirst()) {
            // Alarm ID
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms._ID));
            TextView idView = (TextView) getActivity().findViewById(R.id.alarm_id);
            idView.setText(getString(R.string.alarm_details_id) + id);

            // Severity
            String severity = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.SEVERITY));
            TextView severityView = (TextView) getActivity().findViewById(R.id.alarm_severity);
            severityView.setText(String.valueOf(severity));
            LinearLayout severityRow = (LinearLayout) getActivity().findViewById(R.id.alarm_severity_row);
            if (severity.equals("CLEARED")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_cleared));
            } else if (severity.equals("MINOR")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_minor));
            } else if (severity.equals("NORMAL")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_normal));
            } else if (severity.equals("INDETERMINATE")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_minor));
            } else if (severity.equals("WARNING")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_warning));
            } else if (severity.equals("MAJOR")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_major));
            } else if (severity.equals("CRITICAL")) {
                severityRow.setBackgroundColor(getResources().getColor(R.color.severity_critical));
            }

            // Acknowledgement info
            String ackTime = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.ACK_TIME));
            String ackUser = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.ACK_USER));
            TextView ackStatus = (TextView) getActivity().findViewById(R.id.alarm_ack_status);
            if (ackTime != null) {
                ackStatus.setText(getString(R.string.alarm_details_acked));
                TextView ackMessage = (TextView) getActivity().findViewById(R.id.alarm_ack_message);
                ackMessage.setText(Utils.parseDate(ackTime).toString() + " " + getString(R.string.alarm_details_acked_by) + " " + ackUser);
            } else {
                ackStatus.setText(getString(R.string.alarm_details_not_acked));
            }

            // Description
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.DESCRIPTION));
            TextView descView = (TextView) getActivity().findViewById(R.id.alarm_description);
            descView.setText(Html.fromHtml(desc));

            // Log message
            String logMessage = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.LOG_MESSAGE));
            TextView logMessageView = (TextView) getActivity().findViewById(R.id.alarm_log_message);
            logMessageView.setText(logMessage);

            // Node
            int nodeId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms.NODE_ID));
            String nodeLabel = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.NODE_LABEL));
            TextView node = (TextView) getActivity().findViewById(R.id.alarm_node);
            node.setText(nodeLabel + " (#" + nodeId + ")");

            // Service type
            int serviceTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms.SERVICE_TYPE_ID));
            String serviceTypeName = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.SERVICE_TYPE_NAME));
            TextView serviceType = (TextView) getActivity().findViewById(R.id.alarm_service_type);
            serviceType.setText(serviceTypeName + " (#" + serviceTypeId + ")");

            // Last event
            String lastEventTimeString = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_TIME));
            int lastEventId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_ID));
            String lastEventSeverity = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_SEVERITY));
            TextView lastEvent = (TextView) getActivity().findViewById(R.id.alarm_last_event);
            lastEvent.setText("#" + lastEventId + " " + lastEventSeverity + "\n" + Utils.parseDate(lastEventTimeString).toString());
        }
    }

}