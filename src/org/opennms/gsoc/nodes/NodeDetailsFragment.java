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

    private View viewer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.viewer = inflater.inflate(R.layout.node_view,
                container, false);
        this.viewer.setFocusableInTouchMode(true);
        this.viewer.requestFocus();

        return this.viewer;
    }

    public void updateUrl(Node newNode) {
        if (this.viewer != null) {
            TextView idTextView = (TextView) this.viewer.findViewById(R.id.nodeView);
            idTextView.setText(printNode(newNode));
        }
    }

    private String printNode(Node newNode) {
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
