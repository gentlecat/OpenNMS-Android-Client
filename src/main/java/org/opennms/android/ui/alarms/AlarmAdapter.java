package org.opennms.android.ui.alarms;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.opennms.android.R;
import org.opennms.android.provider.Contract;

public class AlarmAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;
    private Context context;

    public AlarmAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.alarm_list_item, parent, false);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Can't move cursor to position " + position);
        }

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = newView(mContext, mCursor, parent);
            viewHolder = new ViewHolder();
            viewHolder.id = (TextView) convertView.findViewById(R.id.alarm_list_item_id);
            viewHolder.description = (TextView) convertView.findViewById(R.id.alarm_list_item_desc);
            viewHolder.severityIndicator = (ImageView) convertView.findViewById(R.id.alarm_list_item_severity);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(Contract.Alarms.ALARM_ID));
        viewHolder.id.setText(String.valueOf(id));

        String description = mCursor.getString(mCursor.getColumnIndexOrThrow(Contract.Alarms.DESCRIPTION));
        viewHolder.description.setText(Html.fromHtml(description));

        String severity = mCursor.getString(mCursor.getColumnIndexOrThrow(Contract.Alarms.SEVERITY));
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
        viewHolder.severityIndicator.setBackgroundColor(severityColor);

        return convertView;
    }

    static class ViewHolder {
        TextView id;
        TextView description;
        ImageView severityIndicator;
    }

}