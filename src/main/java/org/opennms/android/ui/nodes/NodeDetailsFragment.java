package org.opennms.android.ui.nodes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.android.R;
import org.opennms.android.dao.nodes.Node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NodeDetailsFragment extends SherlockFragment {
    private static final String TAG = "NodeDetailsFragment";
    Node node = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.node_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateContent();
    }

    public void bindNode(Node node) {
        this.node = node;
        if (this.isVisible()) updateContent();
    }

    public void updateContent() {
        if (node != null) {
            TextView id = (TextView) getActivity().findViewById(R.id.node_id);
            id.setText(getString(R.string.node_details_id) + node.getId());

            TextView sysContact = (TextView) getActivity().findViewById(R.id.node_contact);
            sysContact.setText(getString(R.string.node_details_contact) + " " + node.getSysContact());

            TextView timeView = (TextView) getActivity().findViewById(R.id.node_creation_time);
            // Example: "2011-09-27T12:15:32.363-04:00"
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String timeString = node.getCreateTime();
            if (timeString != null) {
                try {
                    Date createTime = format.parse(timeString);
                    timeView.setText(getString(R.string.node_details_creation_time) + " " + createTime.toString());
                } catch (ParseException e) {
                    Log.e(TAG, "Creation time parsing error");
                }
            }

            TextView label = (TextView) getActivity().findViewById(R.id.node_label);
            label.setText(node.getLabel());

            TextView labelSource = (TextView) getActivity().findViewById(R.id.node_label_source);
            labelSource.setText(getString(R.string.node_details_label_source) + " " + node.getLabelSource());

            TextView type = (TextView) getActivity().findViewById(R.id.node_type);
            type.setText(getString(R.string.node_details_type) + " " + node.getType());
        }
    }

}