package org.opennms.android.ui.nodes;

import android.os.Bundle;
import org.opennms.android.R;
import org.opennms.android.ui.BaseActivity;

public class NodesActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new NodesListFragment()).commit();
        getSupportActionBar().setTitle(R.string.nodes);
    }

}
