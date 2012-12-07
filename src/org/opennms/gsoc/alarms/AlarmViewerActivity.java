package org.opennms.gsoc.alarms;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.opennms.gsoc.R;
import org.opennms.gsoc.model.Alarm;

/**
 * This activity is used to display alarm details in case dual-pane layout is unavailable
 */
public class AlarmViewerActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_details);

        AlarmDetailsFragment detailsFragment = (AlarmDetailsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.alarm_details_fragment);
        Alarm content = (Alarm) getIntent().getSerializableExtra("alarm");
        detailsFragment.show(content);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

}
