package org.opennms.gsoc.outages;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.opennms.gsoc.R;
import org.opennms.gsoc.model.Outage;

/**
 * This activity is used to display outage details in case dual-pane layout is unavailable
 */
public class OutageViewerActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outage_details);

        OutageDetailsFragment viewer = (OutageDetailsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.outage_details_fragment);
        Outage content = (Outage) getIntent().getSerializableExtra("outage");
        viewer.show(content);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

}
