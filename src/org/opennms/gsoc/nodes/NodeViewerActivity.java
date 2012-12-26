package org.opennms.gsoc.nodes;

import android.os.Bundle;
import android.widget.TextView;
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

        TextView info = (TextView) findViewById(R.id.node_info);
        Node node = (Node) getIntent().getSerializableExtra("node");
        info.setText(node.toString());
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

}
