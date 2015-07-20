package org.opennms.android.ui.events;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import org.opennms.android.R;
import org.opennms.android.ui.DetailsActivity;

public class EventDetailsActivity extends DetailsActivity {

    public static final String EXTRA_EVENT_ID = "event";
    private long eventId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = getIntent().getLongExtra(EXTRA_EVENT_ID, -1);
        actionBar.setTitle(getResources().getString(R.string.event_details) + eventId);
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        EventDetailsFragment detailsFragment = new EventDetailsFragment(eventId);
        fragmentTransaction.replace(R.id.content_frame, detailsFragment);
        fragmentTransaction.commit();
    }

}
