package org.opennms.android.nodes;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.android.R;

public class NodeDetailsActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Node node = (Node) getIntent().getSerializableExtra("node");
        NodeDetailsFragment fragment = new NodeDetailsFragment();
        fragment.bindNode(node);
        fragmentTransaction.add(R.id.details_activity_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
