package org.opennms.android.ui.alarms;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.android.R;
import org.opennms.android.dao.alarms.Alarm;

public class AlarmDetailsActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Alarm alarm = (Alarm) getIntent().getSerializableExtra("alarm");
        AlarmDetailsFragment detailsFragment = new AlarmDetailsFragment();
        detailsFragment.bindAlarm(alarm);
        actionBar.setTitle(getResources().getString(R.string.alarm_details) + alarm.getId());
        fragmentTransaction.add(R.id.details_activity_fragment_container, detailsFragment);
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
