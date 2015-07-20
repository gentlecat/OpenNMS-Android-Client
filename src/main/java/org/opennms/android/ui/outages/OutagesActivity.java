package org.opennms.android.ui.outages;

import android.os.Bundle;

import org.opennms.android.R;
import org.opennms.android.ui.BaseActivity;

public class OutagesActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new OutagesListFragment()).commit();
        getSupportActionBar().setTitle(R.string.outages);
    }

}
