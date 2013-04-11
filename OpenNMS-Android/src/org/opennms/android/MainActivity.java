package org.opennms.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.android.alarms.AlarmsListFragment;
import org.opennms.android.events.EventsListFragment;
import org.opennms.android.nodes.NodesListFragment;
import org.opennms.android.notifications.NotificationsListFragment;
import org.opennms.android.outages.OutagesListFragment;

public class MainActivity extends SherlockFragmentActivity {

    public Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("is_first_launch", true)) {
            sharedPref.edit().putBoolean("is_first_launch", false).commit();
            showWelcomeDialog();
        }

        serviceIntent = new Intent(getApplicationContext(), MainService.class);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText(R.string.nodes).setTabListener(
                new TabListener<NodesListFragment>(this, "nodes", NodesListFragment.class)));
        actionBar.addTab(actionBar.newTab().setText(R.string.outages).setTabListener(
                new TabListener<OutagesListFragment>(this, "outages", OutagesListFragment.class)));
        actionBar.addTab(actionBar.newTab().setText(R.string.alarms).setTabListener(
                new TabListener<AlarmsListFragment>(this, "alarms", AlarmsListFragment.class)));
        actionBar.addTab(actionBar.newTab().setText(R.string.events).setTabListener(
                new TabListener<EventsListFragment>(this, "events", EventsListFragment.class)));
        actionBar.addTab(actionBar.newTab().setText(R.string.notifications).setTabListener(
                new TabListener<NotificationsListFragment>(this, "notifications", NotificationsListFragment.class)));

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("active_tab", 0));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getApplicationContext().startService(serviceIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        getApplicationContext().stopService(serviceIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("active_tab", getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(getApplicationContext(),
                        SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.menu_about:
                showAboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAboutDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.layout.about));
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(layout).setNeutralButton(
                getString(R.string.close_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    private void showWelcomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.welcome_message))
                .setPositiveButton(
                        getResources().getString(R.string.welcome_message_pos_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(settingsIntent);
                            }
                        })
                .setNegativeButton(
                        getResources().getString(R.string.welcome_message_neg_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog aboutDialog = builder.create();
        aboutDialog.show();
    }

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {

        private final SherlockFragmentActivity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private Fragment mFragment;

        public TabListener(SherlockFragmentActivity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }

}