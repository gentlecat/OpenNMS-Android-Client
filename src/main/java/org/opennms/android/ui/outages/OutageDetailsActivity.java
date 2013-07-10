package org.opennms.android.ui.outages;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import org.opennms.android.R;
import org.opennms.android.dao.outages.Outage;
import org.opennms.android.ui.DetailsActivity;

public class OutageDetailsActivity extends DetailsActivity {

    private Outage outage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        outage = (Outage) getIntent().getSerializableExtra(OutagesListFragment.EXTRA_OUTAGE);
        actionBar.setTitle(getResources().getString(R.string.outage_details) + outage.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        OutageDetailsFragment fragment = new OutageDetailsFragment();
        fragment.bindOutage(outage);
        fragmentTransaction.replace(R.id.details_activity_fragment_container, fragment);
        fragmentTransaction.commit();
    }

}
