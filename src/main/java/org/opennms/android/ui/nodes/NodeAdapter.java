package org.opennms.android.ui.nodes;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opennms.android.R;
import org.opennms.android.provider.Contract;

public class NodeAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;

    public NodeAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.node_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
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
            viewHolder.id = (TextView) convertView.findViewById(R.id.node_list_item_id);
            viewHolder.name = (TextView) convertView.findViewById(R.id.node_list_item_name);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(Contract.Nodes._ID));
        viewHolder.id.setText(String.valueOf(id));

        String name = mCursor.getString(mCursor.getColumnIndexOrThrow(Contract.Nodes.NAME));
        viewHolder.name.setText(name);

        return convertView;
    }

    static class ViewHolder {
        TextView id;
        TextView name;
    }

}