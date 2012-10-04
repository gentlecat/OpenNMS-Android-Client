package org.opennms.gsoc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.gsoc.alarms.AlarmsListFragment;
import org.opennms.gsoc.nodes.NodesListFragment;
import org.opennms.gsoc.outages.OutagesListFragment;
import org.opennms.gsoc.settings.SettingsActivity;

/**
 * The class contains the tab management of the application. The assignment of each tab to its activity is performed through intents. 
 * @author melania galea
 */
public class MainActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(actionBar.newTab()
                .setText("Nodes")
                .setTabListener(new TabListener<NodesListFragment>(
                        this, "Nodes", NodesListFragment.class)));
        actionBar.addTab(actionBar.newTab()
                .setText("Outages")
                .setTabListener(new TabListener<OutagesListFragment>(
                        this, "Outages", OutagesListFragment.class)));
        actionBar.addTab(actionBar.newTab()
                .setText("Alarms")
                .setTabListener(new TabListener<AlarmsListFragment>(
                        this, "Alarms", AlarmsListFragment.class)));

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
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
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
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

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final FragmentActivity m_activity;
        private final String m_tag;
        private final Class<T> m_class;
        private Fragment m_fragment;

        public TabListener(FragmentActivity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(FragmentActivity activity, String tag, Class<T> clz, Bundle args) {
            m_activity = activity;
            m_tag = tag;
            m_class = clz;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            m_fragment = m_activity.getSupportFragmentManager().findFragmentByTag(m_tag);
            if (m_fragment != null && !m_fragment.isDetached()) {
                FragmentTransaction ft = m_activity.getSupportFragmentManager().beginTransaction();
                ft.detach(m_fragment);
                ft.commit();
            }
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ignoredFt) {
            FragmentManager fragMgr = ((FragmentActivity)m_activity).getSupportFragmentManager();
            FragmentTransaction ft = fragMgr.beginTransaction();

            // Check if the fragment is already initialized
            if (m_fragment == null) {
                // If not, instantiate and add it to the activity
                m_fragment = Fragment.instantiate(m_activity, m_class.getName());

                ft.add(android.R.id.content, m_fragment, m_tag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(m_fragment);
            }

            ft.commit();
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (m_fragment != null) {
                ft.detach(m_fragment);
            }
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub
        }

    }

}