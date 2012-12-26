package org.opennms.gsoc.outages;

import android.os.Bundle;
import android.widget.TextView;
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

        TextView info = (TextView) findViewById(R.id.outage_info);
        Outage node = (Outage) getIntent().getSerializableExtra("outage");
        info.setText(node.toString());
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

}
