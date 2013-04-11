package org.opennms.android.ui.nodes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.android.R;
import org.opennms.android.dao.nodes.Node;

public class NodeDetailsFragment extends SherlockFragment {

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

            TextView creationTime = (TextView) getActivity().findViewById(R.id.node_creation_time);
            creationTime.setText(getString(R.string.node_details_creation_time) + " " + node.getCreateTime());

            TextView label = (TextView) getActivity().findViewById(R.id.node_label);
            label.setText(getString(R.string.node_details_label) + " " + node.getLabel());

            TextView labelSource = (TextView) getActivity().findViewById(R.id.node_label_source);
            labelSource.setText(getString(R.string.node_details_label_source) + " " + node.getLabelSource());

            TextView type = (TextView) getActivity().findViewById(R.id.node_type);
            type.setText(getString(R.string.node_details_type) + " " + node.getType());
        }
    }

}