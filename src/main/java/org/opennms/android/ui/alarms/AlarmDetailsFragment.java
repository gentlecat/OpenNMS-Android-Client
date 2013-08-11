package org.opennms.android.ui.alarms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.net.Client;
import org.opennms.android.net.Response;
import org.opennms.android.parsing.AlarmsParser;
import org.opennms.android.provider.Contract;
import org.opennms.android.ui.events.EventDetailsActivity;
import org.opennms.android.ui.nodes.NodeDetailsActivity;

import java.net.HttpURLConnection;

public class AlarmDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "AlarmDetailsFragment";
    private static final int LOADER_ID = 0x1;
    private long alarmId;
    private LoaderManager loaderManager;
    private Menu menu;
    private MenuItem ackMenuItem;
    private MenuItem unackMenuItem;

    // Do not remove!
    public AlarmDetailsFragment() {
    }

    public AlarmDetailsFragment(long alarmId) {
        this.alarmId = alarmId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        return new CursorLoader(getActivity(),
                                Uri.withAppendedPath(Contract.Alarms.CONTENT_URI,
                                                     String.valueOf(alarmId)),
                                null, null, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!isAdded()) {
            return;
        }

        /** Checking if data has been loaded from the DB */
        if (cursor != null && cursor.moveToFirst()) {
            updateContent(cursor);
        } else {
            /** If not, trying to get information from the server */
            new GetDetailsFromServer().execute();
        }

        cursor.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        loaderManager = getLoaderManager();
        loaderManager.restartLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_loading, container, false);
    }

    private void showErrorMessage() {
        // TODO: Implement
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.alarm, menu);
        ackMenuItem = menu.findItem(R.id.menu_ack_alarm);
        unackMenuItem = menu.findItem(R.id.menu_unack_alarm);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateMenu(boolean acked) {
        if (ackMenuItem != null) {
            ackMenuItem.setVisible(!acked);
        }
        if (unackMenuItem != null) {
            unackMenuItem.setVisible(acked);
        }
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
            new AcknowledgementTask(this).execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.alarm_ack_fail_offline),
                           Toast.LENGTH_LONG).show();
        }
    }

    public void unacknowledge() {
        if (Utils.isOnline(getActivity())) {
            new UnacknowledgementTask(this).execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.alarm_unack_fail_offline),
                           Toast.LENGTH_LONG).show();
        }
    }

    public void updateContent(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }

        RelativeLayout detailsContainer =
                (RelativeLayout) getActivity().findViewById(R.id.details_container);
        detailsContainer.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.alarm_details, detailsContainer);

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
        final int nodeId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms.NODE_ID));
        String nodeLabel = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Alarms.NODE_LABEL));
        TextView node = (TextView) getActivity().findViewById(R.id.alarm_node);
        node.setText(nodeLabel + " (#" + nodeId + ")");
        node.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNodeDetails(nodeId);
            }
        });

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
        final int lastEventId = cursor.getInt(
                cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_ID));
        String lastEventSeverity = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Alarms.LAST_EVENT_SEVERITY));
        TextView lastEvent = (TextView) getActivity().findViewById(R.id.alarm_last_event);
        lastEvent.setText("#" + lastEventId + " " + lastEventSeverity + "\n"
                          + Utils.parseDate(lastEventTimeString, "yyyy-MM-dd'T'HH:mm:ssZ"));
        lastEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventDetails(lastEventId);
            }
        });

        updateMenu(ackTime != null);
    }

    private void showNodeDetails(long nodeId) {
        // TODO: Adjust for tablets
        Intent intent = new Intent(getActivity(), NodeDetailsActivity.class);
        intent.putExtra(NodeDetailsActivity.EXTRA_NODE_ID, nodeId);
        startActivity(intent);
    }

    private void showEventDetails(long eventId) {
        // TODO: Adjust for tablets
        Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, eventId);
        startActivity(intent);
    }

    private class GetDetailsFromServer extends AsyncTask<Void, Void, Response> {

        protected Response doInBackground(Void... voids) {
            try {
                return new Client(getActivity()).get("alarms/" + alarmId);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred while loading info about alarm from server", e);
                showErrorMessage();
                return null;
            }
        }

        protected void onPostExecute(Response response) {
            /** If information is available, updating DB */
            if (response != null) {
                if (response.getMessage() != null
                    && response.getCode() == HttpURLConnection.HTTP_OK) {
                    ContentValues[] values = new ContentValues[1];
                    values[0] = AlarmsParser.parseSingle(response.getMessage());
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI, values);

                    Cursor newCursor = getActivity().getContentResolver().query(
                            Uri.withAppendedPath(Contract.Alarms.CONTENT_URI,
                                                 String.valueOf(alarmId)),
                            null, null, null, null);
                    updateContent(newCursor);
                    newCursor.close();
                } else {
                    showErrorMessage();
                }
            } else {
                showErrorMessage();
            }
        }
    }

    private class AcknowledgementTask extends AsyncTask<Void, Void, Response> {

        private final MenuItem ackMenuItem = menu.findItem(R.id.menu_ack_alarm);
        private AlarmDetailsFragment fragment;

        public AcknowledgementTask(AlarmDetailsFragment fragment) {
            this.fragment = fragment;
        }

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
                loaderManager.restartLoader(LOADER_ID, null, fragment);
            } else {
                Toast.makeText(getActivity(),
                               "Error occurred during acknowledgement process!",
                               Toast.LENGTH_LONG).show();
                updateMenu(false);
            }
        }

    }

    private class UnacknowledgementTask extends AsyncTask<Void, Void, Response> {

        private final MenuItem unackMenuItem = menu.findItem(R.id.menu_unack_alarm);
        private AlarmDetailsFragment fragment;

        public UnacknowledgementTask(AlarmDetailsFragment fragment) {
            this.fragment = fragment;
        }

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
                loaderManager.restartLoader(LOADER_ID, null, fragment);
            } else {
                Toast.makeText(getActivity(),
                               "Error occurred during unacknowledgement process!",
                               Toast.LENGTH_LONG).show();
                updateMenu(true);
            }
        }
    }

}