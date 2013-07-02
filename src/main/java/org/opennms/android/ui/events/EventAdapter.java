package org.opennms.android.ui.events;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.opennms.android.R;
import org.opennms.android.dao.Columns;

public class EventAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;

    public EventAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.event_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // ID
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.EventColumns.COL_EVENT_ID));
        TextView idText = (TextView) view.findViewById(R.id.event_list_item_id);
        idText.setText(String.valueOf(id));

        // Log message
        String log = cursor.getString(cursor.getColumnIndexOrThrow(Columns.EventColumns.COL_LOG_MESSAGE));
        TextView logText = (TextView) view.findViewById(R.id.event_list_item_log);
        logText.setText(log);

        // Severity
        String severity = cursor.getString(cursor.getColumnIndexOrThrow(Columns.EventColumns.COL_SEVERITY));
        Resources res = context.getResources();
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
        SurfaceView severityIndicator = (SurfaceView) view.findViewById(R.id.event_list_item_severity);
        severityIndicator.setBackgroundColor(severityColor);
    }
}