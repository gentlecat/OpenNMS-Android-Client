package org.opennms.android.ui.nodes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.opennms.android.R;
import org.opennms.android.provider.Contract;
import org.opennms.android.provider.DatabaseHelper;

public class NodeAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;
    private SQLiteDatabase db;

    public NodeAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        db = new DatabaseHelper(context).getReadableDatabase();
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

        if (convertView == null || convertView.getTag() == null) {
            convertView = newView(mContext, mCursor, parent);
            viewHolder = new ViewHolder();
            viewHolder.id = (TextView) convertView.findViewById(R.id.node_list_item_id);
            viewHolder.name = (TextView) convertView.findViewById(R.id.node_list_item_name);
            viewHolder.warning = (ImageView) convertView.findViewById(R.id.node_list_item_warning);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(Contract.Nodes._ID));
        viewHolder.id.setText(String.valueOf(id));

        String name = mCursor.getString(mCursor.getColumnIndexOrThrow(Contract.Nodes.NAME));
        viewHolder.name.setText(name);

        if (anyAlarms(id)) {
            viewHolder.warning.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private boolean anyAlarms(int nodeId) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contract.Tables.ALARMS);
        queryBuilder.appendWhere(Contract.Alarms.NODE_ID + "=" + nodeId
                                 + " AND " + Contract.Alarms.ACK_USER + " IS NULL");
        return queryBuilder.query(db, null, null, null, null, null, null).moveToFirst();
    }

    static class ViewHolder {

        TextView id;
        TextView name;
        ImageView warning;
    }

}