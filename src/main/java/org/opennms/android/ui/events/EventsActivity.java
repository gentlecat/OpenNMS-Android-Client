package org.opennms.android.ui.events;

import android.os.Bundle;

import org.opennms.android.R;
import org.opennms.android.ui.BaseActivity;

public class EventsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new EventsListFragment()).commit();
        getSupportActionBar().setTitle(R.string.latest_events);
    }

}
