package org.opennms.android.ui.events;

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
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.android.R;
import org.opennms.android.provider.Contract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventDetailsFragment extends SherlockFragment {
    private static final String TAG = "EventDetailsFragment";
    private long eventId;

    // Do not remove
    public EventDetailsFragment() {
    }

    public EventDetailsFragment(long eventId) {
        this.eventId = eventId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateContent(eventId);
    }

    public void updateContent(long eventId) {
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(Contract.Events.CONTENT_URI, String.valueOf(eventId)),
                null, null, null, null);
        if (cursor.moveToFirst()) {
            // Event ID
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Events._ID));
            TextView idView = (TextView) getActivity().findViewById(R.id.event_id);
            idView.setText(getString(R.string.event_details_id) + id);

            // Severity
            String severity = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Events.SEVERITY));
            TextView severityView = (TextView) getActivity().findViewById(R.id.event_severity);
            severityView.setText(String.valueOf(severity));
            LinearLayout severityRow = (LinearLayout) getActivity().findViewById(R.id.event_severity_row);
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

            // Creation time
            TextView timeTextView = (TextView) getActivity().findViewById(R.id.event_create_time);
            // Example: "2011-09-27T12:15:32.363-04:00"
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String createTimeString = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Events.CREATE_TIME));
            if (createTimeString != null) {
                try {
                    Date createTime = format.parse(createTimeString);
                    timeTextView.setText(createTime.toString());
                } catch (ParseException e) {
                    Log.e(TAG, "Creation time parsing error");
                }
            }

            // Log message
            String logMessage = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Events.LOG_MESSAGE));
            TextView logMessageView = (TextView) getActivity().findViewById(R.id.event_log_message);
            logMessageView.setText(Html.fromHtml(logMessage));

            // Description
            String description = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Events.DESCRIPTION));
            TextView descriptionView = (TextView) getActivity().findViewById(R.id.event_desc);
            descriptionView.setText(Html.fromHtml(description));

            // Host
            String host = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Events.HOST));
            TextView hostView = (TextView) getActivity().findViewById(R.id.event_host);
            hostView.setText(host);

            // IP address
            String ipAddress = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Events.IP_ADDRESS));
            TextView ipAddressView = (TextView) getActivity().findViewById(R.id.event_ip_address);
            ipAddressView.setText(ipAddress);

            // Node
            int nodeId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Events.NODE_ID));
            String nodeLabel = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Events.NODE_LABEL));
            TextView nodeView = (TextView) getActivity().findViewById(R.id.event_node);
            nodeView.setText(nodeLabel + " (#" + nodeId + ")");

            // Service type                               
            int serviceTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Events.SERVICE_TYPE_ID));
            String serviceTypeName = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Events.SERVICE_TYPE_NAME));
            TextView serviceTypeView = (TextView) getActivity().findViewById(R.id.event_service_type);
            serviceTypeView.setText(serviceTypeName + " (#" + serviceTypeId + ")");
        }
        cursor.close();
    }

}