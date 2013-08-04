package org.opennms.android.ui.alarms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.net.Client;
import org.opennms.android.net.Response;
import org.opennms.android.parsing.AlarmsParser;
import org.opennms.android.provider.Contract;

import java.net.HttpURLConnection;

public class AlarmDetailsFragment extends Fragment {

    public static final String TAG = "AlarmDetailsFragment";
    private long alarmId;
    private Cursor cursor;
    private Menu menu;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarm_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateContent();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.alarm, menu);
        if (cursor.moveToFirst()) {
            String ackTime = cursor.getString(
                    cursor.getColumnIndexOrThrow(Contract.Alarms.ACK_TIME));
            if (ackTime == null) {
                menu.findItem(R.id.menu_unack_alarm).setVisible(false);
            } else {
                menu.findItem(R.id.menu_ack_alarm).setVisible(false);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ack_alarm:
                acknowledge();
                return true;
            case R.id.menu_unack_alarm:
                unacknowledge();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void acknowledge() {
        if (Utils.isOnline(getActivity())) {
            new AcknowledgementTask().execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.alarm_ack_fail_offline),
                           Toast.LENGTH_LONG).show();
        }
    }

    public void unacknowledge() {
        if (Utils.isOnline(getActivity())) {
            new UnacknowledgementTask().execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.alarm_unack_fail_offline),
                           Toast.LENGTH_LONG).show();
        }
    }

    public void updateContent() {
        if (cursor.moveToFirst()) {
            LinearLayout detailsLayout =
                    (LinearLayout) getActivity().findViewById(R.id.alarm_details);

            // Alarm ID
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms._ID));
            TextView idView = (TextView) getActivity().findViewById(R.id.alarm_id);
            idView.setText(getString(R.string.alarm_details_id) + id);

            // Severity
            String severity =
                    cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.SEVERITY));
            TextView severityView = (TextView) getActivity().findViewById(R.id.alarm_severity);
            severityView.setText(String.valueOf(severity));
            LinearLayout severityRow =
                    (LinearLayout) getActivity().findViewById(R.id.alarm_severity_row);
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
            String ackTime =
                    cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.ACK_TIME));
            String ackUser =
                    cursor.getString(cursor.getColumnIndexOrThrow(Contract.Alarms.ACK_USER));
            TextView ackStatus = (TextView) getActivity().findViewById(R.id.alarm_ack_status);
            TextView ackMessage = (TextView) getActivity().findViewById(R.id.alarm_ack_message);
            if (ackTime != null) {
                ackStatus.setText(getString(R.string.alarm_details_acked));
                ackMessage.setText(Utils.parseDate(ackTime, "yyyy-MM-dd'T'HH:mm:ss'.'SSSZ")
                                   + " " + getString(R.string.alarm_details_acked_by) + " "
                                   + ackUser);
            } else {
                ackStatus.setText(getString(R.string.alarm_details_not_acked));
                ackMessage.setText("");
            }

            // Description
            String desc = cursor.getString(
                    cursor.getColumnIndexOrThrow(Contract.Alarms.DESCRIPTION));
            TextView descView = (TextView) getActivity().findViewById(R.id.alarm_description);
            descView.setText(Html.fromHtml(desc));

            // Log message
            String logMessage = cursor.getString(
                    cursor.getColumnIndexOrThrow(Contract.Alarms.LOG_MESSAGE));
            TextView logMessageView = (TextView) getActivity().findViewById(R.id.alarm_log_message);
            logMessageView.setText(logMessage);

            // Node
            int nodeId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms.NODE_ID));
            String nodeLabel = cursor.getString(
                    cursor.getColumnIndexOrThrow(Contract.Alarms.NODE_LABEL));
            TextView node = (TextView) getActivity().findViewById(R.id.alarm_node);
            node.setText(nodeLabel + " (#" + nodeId + ")");

            // Service type
            int serviceTypeId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Contract.Alarms.SERVICE_TYPE_ID));
            String serviceTypeName = cursor.getString(
                    cursor.getColumnIndexOrThrow(Contract.Alarms.SERVICE_TYPE_NAME));
            TextView serviceType = (TextView) getActivity().findViewById(R.id.alarm_service_type);
            if (serviceTypeName != null) {
                serviceType.setText(serviceTypeName + " (#" + serviceTypeId + ")");
            } else {
                detailsLayout.removeView(serviceType);
                TextView title =
                        (TextView) getActivity().findViewById(R.id.alarm_service_type_title);
                detailsLayout.removeView(title);
            }

            // Last event
            String lastEventTimeString = cursor.getString(
                    cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_TIME));
            int lastEventId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_ID));
            String lastEventSeverity = cursor.getString(
                    cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_SEVERITY));
            TextView lastEvent = (TextView) getActivity().findViewById(R.id.alarm_last_event);
            lastEvent.setText("#" + lastEventId + " " + lastEventSeverity + "\n"
                              + Utils.parseDate(lastEventTimeString, "yyyy-MM-dd'T'HH:mm:ssZ"));
        }
    }

    private class AcknowledgementTask extends AsyncTask<Void, Void, Response> {

        private final MenuItem ackMenuItem = menu.findItem(R.id.menu_ack_alarm);

        @Override
        protected void onPreExecute() {
            if (ackMenuItem != null) {
                ackMenuItem.setVisible(false);
            }
        }

        @Override
        protected Response doInBackground(Void... voids) {
            try {
                return new Client(getActivity()).put(String.format("alarms/%d?ack=true", alarmId));
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during acknowledgement process!", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            if (response != null && response.getCode() == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getActivity(),
                               String.format(getString(R.string.alarm_ack_success), alarmId),
                               Toast.LENGTH_LONG).show();

                // Updating database
                ContentValues[] values = new ContentValues[1];
                values[0] = AlarmsParser.parseSingle(response.getMessage());
                ContentResolver contentResolver = getActivity().getContentResolver();
                contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI, values);

                // Updating details view
                cursor = getActivity().getContentResolver().query(
                        Uri.withAppendedPath(Contract.Alarms.CONTENT_URI, String.valueOf(alarmId)),
                        null, null, null, null);
                updateContent();

                menu.findItem(R.id.menu_unack_alarm).setVisible(true);
            } else {
                Toast.makeText(getActivity(),
                               "Error occurred during acknowledgement process!",
                               Toast.LENGTH_LONG).show();
                final MenuItem ackMenuItem = menu.findItem(R.id.menu_ack_alarm);
                if (ackMenuItem != null) {
                    ackMenuItem.setVisible(true);
                }
            }
        }

    }

    private class UnacknowledgementTask extends AsyncTask<Void, Void, Response> {

        private final MenuItem unackMenuItem = menu.findItem(R.id.menu_unack_alarm);

        @Override
        protected void onPreExecute() {
            if (unackMenuItem != null) {
                unackMenuItem.setVisible(false);
            }
        }

        @Override
        protected Response doInBackground(Void... voids) {
            try {
                return new Client(getActivity()).put(String.format("alarms/%d?ack=false", alarmId));
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during unacknowledgement process!", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            if (response != null && response.getCode() == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getActivity(),
                               String.format(getString(R.string.alarm_unack_success), alarmId),
                               Toast.LENGTH_LONG).show();

                // Updating database
                ContentValues[] values = new ContentValues[1];
                values[0] = AlarmsParser.parseSingle(response.getMessage());
                ContentResolver contentResolver = getActivity().getContentResolver();
                contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI, values);

                // Updating details view
                cursor = getActivity().getContentResolver().query(
                        Uri.withAppendedPath(Contract.Alarms.CONTENT_URI, String.valueOf(alarmId)),
                        null, null, null, null);
                updateContent();

                menu.findItem(R.id.menu_ack_alarm).setVisible(true);
            } else {
                Toast.makeText(getActivity(),
                               "Error occurred during unacknowledgement process!",
                               Toast.LENGTH_LONG).show();
                final MenuItem ackMenuItem = menu.findItem(R.id.menu_ack_alarm);
                if (ackMenuItem != null) {
                    ackMenuItem.setVisible(true);
                }
            }
        }

    }

}