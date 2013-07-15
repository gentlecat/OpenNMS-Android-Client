package org.opennms.android.ui.events;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import org.opennms.android.R;
import org.opennms.android.dao.Event;
import org.opennms.android.ui.DetailsActivity;

public class EventDetailsActivity extends DetailsActivity {

    private Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = (Event) getIntent().getSerializableExtra(EventsListFragment.EXTRA_EVENT);
        actionBar.setTitle(getResources().getString(R.string.event_details) + event.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        EventDetailsFragment detailsFragment = new EventDetailsFragment();
        detailsFragment.bindEvent(event);
        fragmentTransaction.replace(R.id.details_activity_fragment_container, detailsFragment);
        fragmentTransaction.commit();
    }

}
