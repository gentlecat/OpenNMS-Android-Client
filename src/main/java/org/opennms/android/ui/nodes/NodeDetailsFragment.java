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
    private Node node;

    public NodeDetailsFragment(Node node) {
        this.node = node;
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

}