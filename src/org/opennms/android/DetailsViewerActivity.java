package org.opennms.android;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.android.alarms.Alarm;
import org.opennms.android.alarms.AlarmDetailsFragment;
import org.opennms.android.nodes.Node;
import org.opennms.android.nodes.NodeDetailsFragment;
import org.opennms.android.outages.Outage;
import org.opennms.android.outages.OutageDetailsFragment;

/**
 * This activity is used to display details in case dual-pane layout is unavailable
 */
public class DetailsViewerActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String type = (String) getIntent().getSerializableExtra("type");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        SherlockFragment fragment = null;
        if (type.equals(MainActivity.NODE)) {
            Node node = (Node) getIntent().getSerializableExtra(MainActivity.NODE);
            NodeDetailsFragment newFragment = new NodeDetailsFragment();
            newFragment.bindNode(node);
            fragment = newFragment;
            actionBar.setTitle(getResources().getString(R.string.node_details) + node.getId());
        } else if (type.equals(MainActivity.OUTAGE)) {
            Outage outage = (Outage) getIntent().getSerializableExtra(MainActivity.OUTAGE);
            OutageDetailsFragment newFragment = new OutageDetailsFragment();
            newFragment.bindOutage(outage);
            fragment = newFragment;
            actionBar.setTitle(getResources().getString(R.string.outage_details) + outage.getId());
        } else if (type.equals(MainActivity.ALARM)) {
            Alarm alarm = (Alarm) getIntent().getSerializableExtra(MainActivity.ALARM);
            AlarmDetailsFragment newFragment = new AlarmDetailsFragment();
            newFragment.bindAlarm(alarm);
            fragment = newFragment;
            actionBar.setTitle(getResources().getString(R.string.alarm_details) + alarm.getId());
        }
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
