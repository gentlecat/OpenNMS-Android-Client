package org.opennms.android.ui.outages;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import org.opennms.android.R;
import org.opennms.android.ui.DetailsActivity;

public class OutageDetailsActivity extends DetailsActivity {

    private long outageId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        outageId = getIntent().getLongExtra(OutagesListFragment.EXTRA_OUTAGE_ID, -1);
        actionBar.setTitle(getResources().getString(R.string.outage_details) + outageId);
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        OutageDetailsFragment fragment = new OutageDetailsFragment(outageId);
        fragmentTransaction.replace(R.id.details_activity_fragment_container, fragment);
        fragmentTransaction.commit();
    }

}
