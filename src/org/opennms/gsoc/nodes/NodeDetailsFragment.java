package org.opennms.gsoc.nodes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.gsoc.R;

public class NodeDetailsFragment extends SherlockFragment {

    Node node = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_node, container, false);
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
        TextView details = (TextView) getSherlockActivity().findViewById(R.id.node_details_text);
        if (details != null && node != null) details.setText(node.toString());
    }

}