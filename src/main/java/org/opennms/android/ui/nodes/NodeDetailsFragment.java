package org.opennms.android.ui.nodes;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.net.Client;
import org.opennms.android.net.Response;
import org.opennms.android.parsing.AlarmsParser;
import org.opennms.android.parsing.NodesParser;
import org.opennms.android.parsing.OutagesParser;
import org.opennms.android.provider.Contract;
import org.opennms.android.provider.DatabaseHelper;
import org.opennms.android.ui.alarms.AlarmDetailsActivity;
import org.opennms.android.ui.events.EventDetailsActivity;
import org.opennms.android.ui.outages.OutageDetailsActivity;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NodeDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "NodeDetailsFragment";
    private static final int LOADER_ID = 0x4;
    private long nodeId;
    private LoaderManager loaderManager;
    private String nodeName;
    private AlarmsLoader alarmsLoader;
    private OutagesLoader outagesLoader;
    private EventsLoader eventsLoader;
    private SQLiteDatabase db;

    // Do not remove
    public NodeDetailsFragment() {
    }

    public NodeDetailsFragment(long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        return new CursorLoader(getActivity(),
                Uri.withAppendedPath(Contract.Nodes.CONTENT_URI,
                        String.valueOf(nodeId)),
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
            alarmsLoader = new AlarmsLoader();
            alarmsLoader.execute();
            eventsLoader = new EventsLoader();
            eventsLoader.execute();
            outagesLoader = new OutagesLoader();
            outagesLoader.execute();
        } else {
            /** If not, trying to get information from the server */
            new GetDetailsFromServer().execute();
        }

        cursor.close();
    }

    private void showErrorMessage() {
        if (!isAdded()) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                RelativeLayout detailsContainer =
                        (RelativeLayout) getActivity().findViewById(R.id.details_container);
                if (detailsContainer == null) {
                    return;
                }
                detailsContainer.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.details_error, detailsContainer);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHelper(getActivity()).getReadableDatabase();

        loaderManager = getLoaderManager();
        loaderManager.restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        db.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_loading, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alarmsLoader != null) {
            alarmsLoader.cancel(true);
        }
        if (outagesLoader != null) {
            outagesLoader.cancel(true);
        }
        if (eventsLoader != null) {
            eventsLoader.cancel(true);
        }
    }

    public void updateContent(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }

        RelativeLayout detailsContainer =
                (RelativeLayout) getActivity().findViewById(R.id.details_container);
        if (detailsContainer == null) {
            return;
        }
        detailsContainer.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.node_details, detailsContainer);

        LinearLayout detailsLayout =
                (LinearLayout) getActivity().findViewById(R.id.node_details);

        String name = nodeName = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.NAME));
        TextView nameView = (TextView) getActivity().findViewById(R.id.node_name);
        nameView.setText(name);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Nodes._ID));
        TextView idView = (TextView) getActivity().findViewById(R.id.node_id);
        idView.setText(String.valueOf(id));

        String sysContact = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.CONTACT));
        TextView sysContactView = (TextView) getActivity().findViewById(R.id.node_contact);
        if (sysContact != null) {
            sysContactView.setText(sysContact);
        } else {
            detailsLayout.removeView(sysContactView);
            TextView title = (TextView) getActivity().findViewById(R.id.node_contact_title);
            detailsLayout.removeView(title);
        }

        String createdTime = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.CREATED_TIME));
        TextView timeView = (TextView) getActivity().findViewById(R.id.node_creation_time);
        timeView.setText(Utils.reformatDate(createdTime, "yyyy-MM-dd'T'HH:mm:ss'.'SSSZ"));

        String labelSource = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.LABEL_SOURCE));
        TextView labelSourceView =
                (TextView) getActivity().findViewById(R.id.node_label_source);
        if (labelSource != null) {
            labelSourceView.setText(labelSource);
        } else {
            detailsLayout.removeView(labelSourceView);
            TextView title =
                    (TextView) getActivity().findViewById(R.id.node_label_source_title);
            detailsLayout.removeView(title);
        }

        String type = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.TYPE));
        TextView typeView = (TextView) getActivity().findViewById(R.id.node_type);
        if (type != null) {
            typeView.setText(type);
        } else {
            detailsLayout.removeView(typeView);
            TextView title = (TextView) getActivity().findViewById(R.id.node_type_title);
            detailsLayout.removeView(title);
        }

        String desc = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.DESCRIPTION));
        TextView descView = (TextView) getActivity().findViewById(R.id.node_description);
        if (desc != null) {
            descView.setText(desc);
        } else {
            detailsLayout.removeView(descView);
            TextView title = (TextView) getActivity().findViewById(R.id.node_description_title);
            detailsLayout.removeView(title);
        }

        String location = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.LOCATION));
        TextView locationView = (TextView) getActivity().findViewById(R.id.node_location);
        if (location != null) {
            locationView.setText(location);
        } else {
            detailsLayout.removeView(locationView);
            TextView title = (TextView) getActivity().findViewById(R.id.node_location_title);
            detailsLayout.removeView(title);
        }

        String sysObjectId = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Nodes.SYS_OBJECT_ID));
        TextView sysObjectIdView =
                (TextView) getActivity().findViewById(R.id.node_sys_object_id);
        if (sysObjectId != null) {
            sysObjectIdView.setText(sysObjectId);
        } else {
            detailsLayout.removeView(sysObjectIdView);
            TextView title = (TextView) getActivity()
                    .findViewById(R.id.node_sys_object_id_title);
            detailsLayout.removeView(title);
        }
    }

    private void showAlarmDetails(long alarmId) {
        // TODO: Adjust for tablets
        Intent intent = new Intent(getActivity(), AlarmDetailsActivity.class);
        intent.putExtra(AlarmDetailsActivity.EXTRA_ALARM_ID, alarmId);
        startActivity(intent);
    }

    private void showOutageDetails(long outageId) {
        // TODO: Adjust for tablets
        Intent intent = new Intent(getActivity(), OutageDetailsActivity.class);
        intent.putExtra(OutageDetailsActivity.EXTRA_OUTAGE_ID, outageId);
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
                return new Client(getActivity()).get("nodes/" + nodeId);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred while loading info about node from server", e);
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
                    values[0] = NodesParser.parseSingle(response.getMessage());
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    contentResolver.bulkInsert(Contract.Nodes.CONTENT_URI, values);

                    Cursor newCursor = getActivity().getContentResolver().query(
                            Uri.withAppendedPath(Contract.Nodes.CONTENT_URI,
                                    String.valueOf(nodeId)),
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

    private class AlarmsLoader extends AsyncTask<Void, Void, Cursor> {

        protected Cursor doInBackground(Void... voids) {
            Response response = null;
            try {
                response = new Client(getActivity()).get(
                        "alarms/?query=" + URLEncoder.encode("nodeLabel = '" + nodeName + "'"));
            } catch (Exception e) {
                Log.e(TAG, "Error occurred while loading info from server", e);
            }

            if (response != null && response.getMessage() != null
                    && response.getCode() == HttpURLConnection.HTTP_OK) {
                ContentResolver contentResolver = getActivity().getContentResolver();
                ArrayList<ContentValues> values = AlarmsParser.parseMultiple(response.getMessage());
                contentResolver.bulkInsert(Contract.Alarms.CONTENT_URI,
                        values.toArray(new ContentValues[values.size()]));
            }

            /** Getting info from DB */
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(Contract.Tables.ALARMS);
            queryBuilder.appendWhere(Contract.Alarms.NODE_ID + "=" + nodeId
                    + " AND " + Contract.Alarms.ACK_USER + " IS NULL");
            String[] projection = {
                    Contract.Alarms._ID,
                    Contract.Alarms.LOG_MESSAGE,
                    Contract.Alarms.SEVERITY
            };
            return queryBuilder.query(db, projection, null, null, null, null, null);
        }

        protected void onPostExecute(Cursor cursor) {
            LinearLayout detailsLayout = (LinearLayout) getActivity()
                    .findViewById(R.id.node_details);
            if (detailsLayout == null) {
                return;
            }
            TextView alarmsPlaceholder =
                    (TextView) getActivity().findViewById(R.id.node_alarms_placeholder);
            if (!cursor.moveToFirst()) {
                alarmsPlaceholder.setText(getString(R.string.no_outstanding_alarms));
            } else {
                detailsLayout.removeView(alarmsPlaceholder);

                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                LinearLayout container = (LinearLayout) getActivity()
                        .findViewById(R.id.node_details_alarms_container);

                for (boolean b = cursor.moveToFirst(); b; b = cursor.moveToNext()) {
                    View item = inflater.inflate(R.layout.node_details_alarm, null);

                    final int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Alarms._ID));
                    TextView idText = (TextView) item.findViewById(R.id.node_details_alarm_id);
                    idText.setText("#" + id);

                    String severity = cursor.getString(
                            cursor.getColumnIndexOrThrow(Contract.Alarms.SEVERITY));
                    Resources res = getActivity().getResources();
                    int severityColor;
                    if (severity.equals("CLEARED")) {
                        severityColor = res.getColor(R.color.severity_cleared);
                    } else if (severity.equals("MINOR")) {
                        severityColor = res.getColor(R.color.severity_minor);
                    } else if (severity.equals("NORMAL")) {
                        severityColor = res.getColor(R.color.severity_normal);
                    } else if (severity.equals("INDETERMINATE")) {
                        severityColor = res.getColor(R.color.severity_minor);
                    } else if (severity.equals("WARNING")) {
                        severityColor = res.getColor(R.color.severity_warning);
                    } else if (severity.equals("MAJOR")) {
                        severityColor = res.getColor(R.color.severity_major);
                    } else {
                        severityColor = res.getColor(R.color.severity_critical);
                    }
                    idText.setBackgroundColor(severityColor);

                    String message = cursor
                            .getString(cursor.getColumnIndexOrThrow(Contract.Alarms.LOG_MESSAGE));
                    TextView messageText =
                            (TextView) item.findViewById(R.id.node_details_alarm_message);
                    messageText.setText(Html.fromHtml(message));

                    container.addView(item);

                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAlarmDetails(id);
                        }
                    });
                }
            }
        }
    }

    private class OutagesLoader extends AsyncTask<Void, Void, Cursor> {

        protected Cursor doInBackground(Void... voids) {
            Response response = null;
            try {
                response = new Client(getActivity()).get("outages/forNode/" + nodeId);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred while loading info from server", e);
            }

            if (response != null && response.getMessage() != null
                    && response.getCode() == HttpURLConnection.HTTP_OK) {
                ContentResolver contentResolver = getActivity().getContentResolver();
                ArrayList<ContentValues> values =
                        OutagesParser.parseMultiple(response.getMessage());
                contentResolver.bulkInsert(Contract.Outages.CONTENT_URI,
                        values.toArray(new ContentValues[values.size()]));
            }

            /** Getting info from DB */
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(Contract.Tables.OUTAGES);
            queryBuilder.appendWhere(Contract.Outages.NODE_ID + "=" + nodeId);
            String[] projection = {
                    Contract.Outages._ID,
                    Contract.Outages.SERVICE_TYPE_NAME
            };
            return queryBuilder.query(db, projection, null, null, null, null, null);
        }

        protected void onPostExecute(Cursor cursor) {
            LinearLayout detailsLayout = (LinearLayout) getActivity()
                    .findViewById(R.id.node_details);
            if (detailsLayout == null) {
                return;
            }
            TextView outagesPlaceholder =
                    (TextView) getActivity().findViewById(R.id.node_outages_placeholder);
            if (!cursor.moveToFirst()) {
                outagesPlaceholder.setText(getString(R.string.no_outages));
            } else {
                detailsLayout.removeView(outagesPlaceholder);

                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                LinearLayout container = (LinearLayout) getActivity()
                        .findViewById(R.id.node_details_outages_container);

                for (boolean b = cursor.moveToFirst(); b; b = cursor.moveToNext()) {
                    View item = inflater.inflate(R.layout.node_details_outage, null);

                    final int id =
                            cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages._ID));
                    TextView idText = (TextView) item.findViewById(R.id.node_details_outage_id);
                    idText.setText("#" + id);

                    String serviceType = cursor.getString(
                            cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_TYPE_NAME));
                    TextView serviceTypeText =
                            (TextView) item.findViewById(R.id.node_details_outage_service);
                    serviceTypeText.setText(serviceType);

                    container.addView(item);

                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showOutageDetails(id);
                        }
                    });
                }
            }
        }
    }

    private class EventsLoader extends AsyncTask<Void, Void, Cursor> {

        protected Cursor doInBackground(Void... voids) {
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(Contract.Tables.EVENTS);
            queryBuilder.appendWhere(Contract.Events.NODE_ID + "=" + nodeId);
            String[] projection = {
                    Contract.Events._ID,
                    Contract.Events.LOG_MESSAGE,
                    Contract.Events.SEVERITY
            };
            return queryBuilder.query(db, projection, null, null, null, null, null);
        }

        protected void onPostExecute(Cursor cursor) {
            LinearLayout detailsLayout = (LinearLayout) getActivity()
                    .findViewById(R.id.node_details);
            if (detailsLayout == null) {
                return;
            }
            TextView eventsPlaceholder =
                    (TextView) getActivity().findViewById(R.id.node_events_placeholder);
            if (!cursor.moveToFirst()) {
                eventsPlaceholder.setText(getString(R.string.no_events));
            } else {
                detailsLayout.removeView(eventsPlaceholder);

                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                LinearLayout container = (LinearLayout) getActivity()
                        .findViewById(R.id.node_details_events_container);

                for (boolean b = cursor.moveToFirst(); b; b = cursor.moveToNext()) {
                    View item = inflater.inflate(R.layout.node_details_event, null);

                    final int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Events._ID));
                    TextView idText = (TextView) item.findViewById(R.id.node_details_event_id);
                    idText.setText("#" + id);

                    String severity = cursor.getString(
                            cursor.getColumnIndexOrThrow(Contract.Events.SEVERITY));
                    Resources res = getActivity().getResources();
                    int severityColor;
                    if (severity.equals("CLEARED")) {
                        severityColor = res.getColor(R.color.severity_cleared);
                    } else if (severity.equals("MINOR")) {
                        severityColor = res.getColor(R.color.severity_minor);
                    } else if (severity.equals("NORMAL")) {
                        severityColor = res.getColor(R.color.severity_normal);
                    } else if (severity.equals("INDETERMINATE")) {
                        severityColor = res.getColor(R.color.severity_minor);
                    } else if (severity.equals("WARNING")) {
                        severityColor = res.getColor(R.color.severity_warning);
                    } else if (severity.equals("MAJOR")) {
                        severityColor = res.getColor(R.color.severity_major);
                    } else {
                        severityColor = res.getColor(R.color.severity_critical);
                    }
                    idText.setBackgroundColor(severityColor);

                    String message = cursor
                            .getString(cursor.getColumnIndexOrThrow(Contract.Events.LOG_MESSAGE));
                    TextView messageText =
                            (TextView) item.findViewById(R.id.node_details_event_message);
                    messageText.setText(Html.fromHtml(message));

                    container.addView(item);

                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showEventDetails(id);
                        }
                    });
                }
            }
        }
    }

}