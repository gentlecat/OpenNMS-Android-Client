package org.opennms.gsoc.nodes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.gsoc.R;
import org.opennms.gsoc.model.Node;

public class NodeDetailsFragment extends SherlockFragment {

    private View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,       Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.node_details, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        return view;
    }

    public void show(Node newNode) {
        if (view != null) {
            TextView idTextView = (TextView) view.findViewById(R.id.node_info);
            idTextView.setText(printNodeInfo(newNode));
        }
    }

    private String printNodeInfo(Node newNode) {
        StringBuilder builder = new StringBuilder();
        builder.append("Node ID: " + newNode.getId() + "\n");
        builder.append("Label: " + newNode.getLabel() + "\n");
        builder.append("Type: " + newNode.getType() + "\n");
        builder.append("Creation time: " + newNode.getCreateTime() + "\n");
        builder.append("Sys. contact: " + newNode.getSysContact() + "\n");
        builder.append("Label source: " + newNode.getLabelSource() + "\n");
        return builder.toString();
    }

}
