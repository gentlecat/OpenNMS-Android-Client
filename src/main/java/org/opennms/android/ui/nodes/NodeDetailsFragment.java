package org.opennms.android.ui.nodes;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.provider.Contract;

public class NodeDetailsFragment extends SherlockFragment {
    private static final String TAG = "NodeDetailsFragment";
    private long nodeId;

    // Do not remove
    public NodeDetailsFragment() {
    }

    public NodeDetailsFragment(long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.node_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateContent(nodeId);
    }

    public void updateContent(long nodeId) {
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(Contract.Nodes.CONTENT_URI, String.valueOf(nodeId)),
                null, null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.NAME));
            TextView nameView = (TextView) getActivity().findViewById(R.id.node_name);
            nameView.setText(name);

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Nodes._ID));
            TextView idView = (TextView) getActivity().findViewById(R.id.node_id);
            idView.setText(String.valueOf(id));

            String sysContact = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.SYS_CONTACT));
            TextView sysContactView = (TextView) getActivity().findViewById(R.id.node_contact);
            sysContactView.setText(sysContact);

            String createdTime = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.CREATED_TIME));
            TextView timeView = (TextView) getActivity().findViewById(R.id.node_creation_time);
            timeView.setText(Utils.parseDate(createdTime).toString());

            String labelSource = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.LABEL_SOURCE));
            TextView labelSourceView = (TextView) getActivity().findViewById(R.id.node_label_source);
            labelSourceView.setText(labelSource);

            String type = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.TYPE));
            TextView typeView = (TextView) getActivity().findViewById(R.id.node_type);
            typeView.setText(type);

            String desc = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.DESCRIPTION));
            TextView descView = (TextView) getActivity().findViewById(R.id.node_description);
            descView.setText(desc);

            String location = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.LOCATION));
            TextView locationView = (TextView) getActivity().findViewById(R.id.node_location);
            locationView.setText(location);
        }
        cursor.close();
    }

}