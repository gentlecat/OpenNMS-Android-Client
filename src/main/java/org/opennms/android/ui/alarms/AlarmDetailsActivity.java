package org.opennms.android.ui.alarms;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import org.opennms.android.R;
import org.opennms.android.dao.Alarm;
import org.opennms.android.ui.DetailsActivity;

public class AlarmDetailsActivity extends DetailsActivity {

    private Alarm alarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarm = (Alarm) getIntent().getSerializableExtra(AlarmsListFragment.EXTRA_ALARM);
        actionBar.setTitle(getResources().getString(R.string.alarm_details) + alarm.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        AlarmDetailsFragment detailsFragment = new AlarmDetailsFragment(alarm);
        fragmentTransaction.replace(R.id.details_activity_fragment_container, detailsFragment);
        fragmentTransaction.commit();
    }

}
