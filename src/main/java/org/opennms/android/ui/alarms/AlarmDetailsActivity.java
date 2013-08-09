package org.opennms.android.ui.alarms;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import org.opennms.android.R;
import org.opennms.android.ui.DetailsActivity;

public class AlarmDetailsActivity extends DetailsActivity {

    public static final String EXTRA_ALARM_ID = "alarm";
    private long alarmId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmId = getIntent().getLongExtra(EXTRA_ALARM_ID, -1);
        actionBar.setTitle(getResources().getString(R.string.alarm_details) + alarmId);
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        AlarmDetailsFragment detailsFragment = new AlarmDetailsFragment(alarmId);
        fragmentTransaction.replace(R.id.details_activity_fragment_container, detailsFragment);
        fragmentTransaction.commit();
    }

}
