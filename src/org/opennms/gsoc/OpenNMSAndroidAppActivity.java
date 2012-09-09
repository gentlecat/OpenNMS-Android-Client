package org.opennms.gsoc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.gsoc.about.AboutFragment;
import org.opennms.gsoc.about.SettingsViewerActivity;
import org.opennms.gsoc.about.SettingsViewerFragment;
import org.opennms.gsoc.alarms.AlarmsListFragment;
import org.opennms.gsoc.listeners.TabListener;
import org.opennms.gsoc.nodes.NodesListFragment;
import org.opennms.gsoc.outages.OutagesListFragment;

/**
 * The class contains the tab management of the application. The assignment of each tab to its activity is performed through intents. 
 * @author melania galea
 *
 */
public class OpenNMSAndroidAppActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        bar.addTab(bar.newTab()
                .setText("Nodes")
                .setTabListener(new TabListener<NodesListFragment>(
                        this, "Nodes", NodesListFragment.class)));
        bar.addTab(bar.newTab()
                .setText("Outages")
                .setTabListener(new TabListener<OutagesListFragment>(
                        this, "Outages", OutagesListFragment.class)));
        bar.addTab(bar.newTab()
                .setText("Alarms")
                .setTabListener(new TabListener<AlarmsListFragment>(
                        this, "Alarms", AlarmsListFragment.class)));

        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                SettingsViewerFragment viewer = (SettingsViewerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.about_settings_details);
                if (viewer == null || !viewer.isInLayout()) {
                    Intent showContent = new Intent(getApplicationContext(),
                            SettingsViewerActivity.class);
                    startActivity(showContent);
                }
                return true;
            case R.id.menu_about:
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.layout.main));
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setView(layout)
                        .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog aboutDialog = builder.create();
                aboutDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}