package org.opennms.android.ui.outages;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.android.R;
import org.opennms.android.dao.outages.Outage;

public class OutageDetailsActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Outage outage = (Outage) getIntent().getSerializableExtra("outage");
        OutageDetailsFragment fragment = new OutageDetailsFragment();
        fragment.bindOutage(outage);
        actionBar.setTitle(getResources().getString(R.string.outage_details) + outage.getId());
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
