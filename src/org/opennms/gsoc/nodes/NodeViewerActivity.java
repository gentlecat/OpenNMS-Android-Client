package org.opennms.gsoc.nodes;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.opennms.gsoc.R;
import org.opennms.gsoc.model.Node;

/**
 * This activity is used to display node details in case dual-pane layout is unavailable
 */
public class NodeViewerActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.node_details);

        NodeDetailsFragment viewer = (NodeDetailsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.node_details_fragment);
        Node content = (Node) getIntent().getSerializableExtra("node");
        viewer.updateUrl(content);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

}
