package org.opennms.android.ui.nodes;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.android.R;
import org.opennms.android.dao.Node;
import org.opennms.android.provider.Contract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NodeDetailsFragment extends SherlockFragment {
    private static final String TAG = "NodeDetailsFragment";
    private Node node;
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
        updateContent();
    }

    public void updateContent() {
        node = getNode(nodeId);

        if (node != null) {
            TextView name = (TextView) getActivity().findViewById(R.id.node_name);
            name.setText(node.getName());

            TextView id = (TextView) getActivity().findViewById(R.id.node_id);
            id.setText(String.valueOf(node.getId()));

            TextView contactText = (TextView) getActivity().findViewById(R.id.node_contact);
            contactText.setText(node.getSysContact());

            TextView timeView = (TextView) getActivity().findViewById(R.id.node_creation_time);
            // Example: "2011-09-27T12:15:32.363-04:00"
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String timeString = node.getCreateTime();
            if (timeString != null) {
                try {
                    Date createTime = format.parse(timeString);
                    timeView.setText(createTime.toString());
                } catch (ParseException e) {
                    Log.e(TAG, "Creation time parsing error");
                }
            }

            TextView labelSource = (TextView) getActivity().findViewById(R.id.node_label_source);
            labelSource.setText(node.getLabelSource());

            TextView type = (TextView) getActivity().findViewById(R.id.node_type);
            type.setText(node.getType());

            TextView description = (TextView) getActivity().findViewById(R.id.node_description);
            description.setText(node.getDescription());

            TextView location = (TextView) getActivity().findViewById(R.id.node_location);
            location.setText(node.getLocation());
        }
    }

    private Node getNode(long id) {
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(Contract.Nodes.CONTENT_URI, String.valueOf(id)),
                null, null, null, null
        );
        if (cursor.moveToFirst()) {
            Node node = new Node((cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Nodes._ID))));
            node.setType(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.TYPE)));
            node.setName(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.NAME)));
            node.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.CREATED_TIME)));
            node.setSysContact(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.SYS_CONTACT)));
            node.setLabelSource(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.LABEL_SOURCE)));
            node.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.LOCATION)));
            node.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Nodes.DESCRIPTION)));
            cursor.close();
            return node;
        }
        cursor.close();
        return null;
    }

}