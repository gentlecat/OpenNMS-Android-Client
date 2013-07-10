package org.opennms.android.ui.alarms;

import android.os.Bundle;
import org.opennms.android.R;
import org.opennms.android.ui.BaseActivity;

public class AlarmsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new AlarmsListFragment()).commit();
        getSupportActionBar().setTitle(R.string.alarms);
    }

}
