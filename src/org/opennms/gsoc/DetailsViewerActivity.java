package org.opennms.gsoc;

import android.os.Bundle;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.opennms.gsoc.model.Outage;


import android.os.Bundle;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.opennms.gsoc.R;
import org.opennms.gsoc.model.Outage;

/**
 * This activity is used to display details in case dual-pane layout is unavailable
 */
public class DetailsViewerActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        TextView info = (TextView) findViewById(R.id.info);
        String details = (String) getIntent().getSerializableExtra("details");
        info.setText(details);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

}
