package org.opennms.android.ui.nodes;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import org.opennms.android.R;
import org.opennms.android.ui.DetailsActivity;

public class NodeDetailsActivity extends DetailsActivity {

    private long nodeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nodeId = getIntent().getLongExtra(NodesListFragment.EXTRA_NODE_ID, -1);
        actionBar.setTitle(getResources().getString(R.string.node_details) + nodeId);
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        NodeDetailsFragment fragment = new NodeDetailsFragment(nodeId);
        fragmentTransaction.replace(R.id.details_activity_fragment_container, fragment);
        fragmentTransaction.commit();
    }

}
