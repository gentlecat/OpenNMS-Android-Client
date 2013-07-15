package org.opennms.android.ui.nodes;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import org.opennms.android.R;
import org.opennms.android.dao.Node;
import org.opennms.android.ui.DetailsActivity;

public class NodeDetailsActivity extends DetailsActivity {

    private Node node;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        node = (Node) getIntent().getSerializableExtra(NodesListFragment.EXTRA_NODE);
        actionBar.setTitle(getResources().getString(R.string.node_details) + node.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        NodeDetailsFragment fragment = new NodeDetailsFragment(node);
        fragmentTransaction.replace(R.id.details_activity_fragment_container, fragment);
        fragmentTransaction.commit();
    }

}
