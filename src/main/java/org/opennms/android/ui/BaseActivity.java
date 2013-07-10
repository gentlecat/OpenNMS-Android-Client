package org.opennms.android.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.android.R;
import org.opennms.android.ui.alarms.AlarmsActivity;
import org.opennms.android.ui.dialogs.AboutDialog;
import org.opennms.android.ui.dialogs.WelcomeDialog;
import org.opennms.android.ui.events.EventsActivity;
import org.opennms.android.ui.nodes.NodesActivity;
import org.opennms.android.ui.outages.OutagesActivity;

public abstract class BaseActivity extends SherlockFragmentActivity {
    private static final String STATE_TITLE = "title";
    private static final String STATE_IS_NAV_OPEN = "is_nav_open";
    private DrawerLayout navigationLayout;
    private ListView navigationList;
    private ActionBarDrawerToggle navigationToggle;
    private CharSequence title;
    private String[] navigationItems;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final CharSequence drawerTitle = title = getTitle();
        navigationLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationList = (ListView) findViewById(R.id.navigation_drawer);
        navigationItems = getResources().getStringArray(R.array.navigation_items);

        navigationLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navigationList.setAdapter(new ArrayAdapter<String>(this, R.layout.nav_drawer_list_item, navigationItems));
        navigationList.setOnItemClickListener(new DrawerItemClickListener());

        actionBar = getSupportActionBar();

        // Enable ActionBar home button to behave as action to toggle navigation drawer
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        navigationToggle = new ActionBarDrawerToggle(
                this,
                navigationLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                actionBar.setTitle(title);
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(drawerTitle);
            }
        };
        navigationLayout.setDrawerListener(navigationToggle);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("is_first_launch", true)) {
            sharedPref.edit().putBoolean("is_first_launch", false).commit();
            showWelcomeDialog();
        }

        if (savedInstanceState != null) {
            title = savedInstanceState.getCharSequence(STATE_TITLE);
            if (savedInstanceState.getBoolean(STATE_IS_NAV_OPEN)) actionBar.setTitle(drawerTitle);
            else actionBar.setTitle(title);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putCharSequence(STATE_TITLE, title);
        savedInstanceState.putBoolean(STATE_IS_NAV_OPEN, navigationLayout.isDrawerOpen(navigationList));
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (navigationLayout.isDrawerOpen(navigationList)) {
                    navigationLayout.closeDrawer(navigationList);
                } else {
                    navigationLayout.openDrawer(navigationList);
                }
                return true;
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.menu_about:
                showAboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectItem(int position) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(getApplicationContext(), NodesActivity.class);
                break;
            case 1:
                intent = new Intent(getApplicationContext(), AlarmsActivity.class);
                break;
            case 2:
                intent = new Intent(getApplicationContext(), OutagesActivity.class);
                break;
            case 3:
                intent = new Intent(getApplicationContext(), EventsActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
        navigationLayout.closeDrawer(navigationList);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        actionBar.setTitle(this.title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        navigationToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navigationToggle.onConfigurationChanged(newConfig);
    }

    public void showAboutDialog() {
        AboutDialog dialog = new AboutDialog();
        dialog.show(getSupportFragmentManager(), AboutDialog.TAG);
    }

    public void showWelcomeDialog() {
        WelcomeDialog dialog = new WelcomeDialog();
        dialog.show(getSupportFragmentManager(), WelcomeDialog.TAG);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
