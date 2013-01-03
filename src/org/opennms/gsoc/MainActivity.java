package org.opennms.gsoc;

import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.gsoc.alarms.Alarm;
import org.opennms.gsoc.alarms.AlarmDetailsFragment;
import org.opennms.gsoc.alarms.dao.AlarmsListProvider;
import org.opennms.gsoc.dao.DatabaseHelper;
import org.opennms.gsoc.nodes.Node;
import org.opennms.gsoc.nodes.NodeDetailsFragment;
import org.opennms.gsoc.nodes.dao.NodesListProvider;
import org.opennms.gsoc.outages.Outage;
import org.opennms.gsoc.outages.OutageDetailsFragment;
import org.opennms.gsoc.outages.dao.OutagesListProvider;

public class MainActivity extends SherlockFragmentActivity
        implements ActionBar.TabListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ALARM = "alarm";
    public static final String OUTAGE = "outage";
    public static final String NODE = "node";
    public Intent serviceIntent;
    FrameLayout detailsContainer;
    private MainService service;
    private boolean bound = false;
    private ListView list;
    private Tab activeTab;
    private boolean isDualPane = false;
    private String currentFilter;
    private SimpleCursorAdapter adapter;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.LocalBinder binder = (MainService.LocalBinder) service;
            MainActivity.this.service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("is_first_launch", true)) {
            sharedPref.edit().putBoolean("is_first_launch", false).commit();
            showWelcomeDialog();
        }

        serviceIntent = new Intent(getApplicationContext(), MainService.class);
        list = (ListView) findViewById(android.R.id.list);
        detailsContainer = (FrameLayout) findViewById(R.id.details_fragment_container);
        isDualPane = detailsContainer != null && detailsContainer.getVisibility() == View.VISIBLE;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText(R.string.nodes).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.outages).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.alarms).setTabListener(this));

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("active_tab", 0));
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                displayDetails(id);
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void displayDetails(long listItemId) {
        if (isDualPane) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            FrameLayout detailsContainer = (FrameLayout) findViewById(R.id.details_fragment_container);
            detailsContainer.removeAllViews();
            SherlockFragment fragment = null;
            if (activeTab.getText().equals(getString(R.string.nodes))) {
                Node node = new Node(getContentResolver(), listItemId);
                NodeDetailsFragment newFragment = new NodeDetailsFragment();
                newFragment.bindNode(node);
                fragment = newFragment;
            } else if (activeTab.getText().equals(getString(R.string.outages))) {
                Outage outage = new Outage(getContentResolver(), listItemId);
                OutageDetailsFragment newFragment = new OutageDetailsFragment();
                newFragment.bindOutage(outage);
                fragment = newFragment;
            } else if (activeTab.getText().equals(getString(R.string.alarms))) {
                Alarm alarm = new Alarm(getContentResolver(), listItemId);
                AlarmDetailsFragment newFragment = new AlarmDetailsFragment();
                newFragment.bindAlarm(alarm);
                fragment = newFragment;
            }
            fragmentTransaction.add(R.id.details_fragment_container, fragment);
            fragmentTransaction.commit();
        } else {
            Intent detailsIntent = new Intent(getApplicationContext(), DetailsViewerActivity.class);
            if (activeTab.getText().equals(getString(R.string.nodes))) {
                Node node = new Node(getContentResolver(), listItemId);
                detailsIntent.putExtra("type", NODE);
                detailsIntent.putExtra(NODE, node);
            } else if (activeTab.getText().equals(getString(R.string.outages))) {
                Outage outage = new Outage(getContentResolver(), listItemId);
                detailsIntent.putExtra("type", OUTAGE);
                detailsIntent.putExtra(OUTAGE, outage);
            } else if (activeTab.getText().equals(getString(R.string.alarms))) {
                Alarm alarm = new Alarm(getContentResolver(), listItemId);
                detailsIntent.putExtra("type", ALARM);
                detailsIntent.putExtra(ALARM, alarm);
            }
            startActivity(detailsIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getApplicationContext().startService(serviceIntent);
        Intent serviceIntent = new Intent(getApplicationContext(), MainService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (bound) {
            service.refreshNodes();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
        getApplicationContext().stopService(serviceIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("active_tab", getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        activeTab = tab;
        if (activeTab.getText().equals(getString(R.string.nodes))) {
            adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,
                    null,
                    new String[]{DatabaseHelper.COL_NODE_ID, DatabaseHelper.COL_LABEL},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        } else if (activeTab.getText().equals(getString(R.string.outages))) {
            adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,
                    null,
                    new String[]{DatabaseHelper.COL_OUTAGE_ID, DatabaseHelper.COL_IP_ADDRESS},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        } else if (activeTab.getText().equals(getString(R.string.alarms))) {
            adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,
                    null,
                    new String[]{DatabaseHelper.COL_ALARM_ID, DatabaseHelper.COL_SEVERITY},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        }
        list.setAdapter(adapter);
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        if (this.currentFilter == null && newFilter == null) {
            return true;
        }
        if (this.currentFilter != null && this.currentFilter.equals(newFilter)) {
            return true;
        }
        this.currentFilter = newFilter;
        getSupportLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = null;
        String[] projection = null;
        if (activeTab.getText().equals(getString(R.string.nodes))) {
            if (this.currentFilter != null) {
                baseUri = Uri.withAppendedPath(Uri.withAppendedPath(
                        NodesListProvider.CONTENT_URI, "label"),
                        Uri.encode(this.currentFilter)
                );
            } else {
                baseUri = NodesListProvider.CONTENT_URI;
            }
            String[] nodesProjection = {
                    DatabaseHelper.TABLE_NODES_ID,
                    DatabaseHelper.COL_NODE_ID,
                    DatabaseHelper.COL_TYPE,
                    DatabaseHelper.COL_LABEL,
                    DatabaseHelper.COL_CREATED_TIME,
                    DatabaseHelper.COL_SYS_CONTACT,
                    DatabaseHelper.COL_LABEL_SOURCE
            };
            projection = nodesProjection;
        } else if (activeTab.getText().equals(getString(R.string.outages))) {
            if (this.currentFilter != null) {
                baseUri = Uri.withAppendedPath(
                        Uri.withAppendedPath(OutagesListProvider.CONTENT_URI, "ipaddress"),
                        Uri.encode(this.currentFilter)
                );
            } else {
                baseUri = OutagesListProvider.CONTENT_URI;
            }
            String[] outageProjection = {
                    DatabaseHelper.TABLE_OUTAGES_ID,
                    DatabaseHelper.COL_OUTAGE_ID,
                    DatabaseHelper.COL_IP_ADDRESS,
                    DatabaseHelper.COL_IF_REGAINED_SERVICE,
                    DatabaseHelper.COL_SERVICE_TYPE_NAME,
                    DatabaseHelper.COL_IF_LOST_SERVICE
            };
            projection = outageProjection;
        } else if (activeTab.getText().equals(getString(R.string.alarms))) {
            if (currentFilter != null) {
                baseUri = Uri.withAppendedPath(
                        Uri.withAppendedPath(AlarmsListProvider.CONTENT_URI, "severity"),
                        Uri.encode(currentFilter)
                );
            } else {
                baseUri = AlarmsListProvider.CONTENT_URI;
            }
            String[] alarmsProjection = {
                    DatabaseHelper.TABLE_ALARMS_ID,
                    DatabaseHelper.COL_ALARM_ID,
                    DatabaseHelper.COL_SEVERITY,
                    DatabaseHelper.COL_DESCRIPTION,
                    DatabaseHelper.COL_LOG_MESSAGE
            };
            projection = alarmsProjection;
        }
        return new CursorLoader(getApplicationContext(), baseUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        if (cursor.getColumnCount() > 0) {
            if (isDualPane) {
                // TODO Show info about first item
            }
        } else {
            // TODO Improve
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.add("Search");
        item.setIcon(getResources().getDrawable(R.drawable.ic_action_search));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(this);
        sv.setOnQueryTextListener(this);
        item.setActionView(sv);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshList();
                return true;
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

    private void refreshList() {
        if (bound) {
            if (activeTab.getText().equals(getString(R.string.nodes))) {
                service.refreshNodes();
            } else if (activeTab.getText().equals(getString(R.string.outages))) {
                service.refreshOutages();
            } else if (activeTab.getText().equals(getString(R.string.alarms))) {
                service.refreshAlarms();
            }
        }
    }

    private void showAboutDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.layout.about));
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(layout).setNeutralButton(
                "Close",
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

}