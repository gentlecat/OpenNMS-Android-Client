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
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.gsoc.alarms.dao.AlarmsListProvider;
import org.opennms.gsoc.dao.DatabaseHelper;
import org.opennms.gsoc.model.Alarm;
import org.opennms.gsoc.model.Node;
import org.opennms.gsoc.model.Outage;
import org.opennms.gsoc.nodes.dao.NodesListProvider;
import org.opennms.gsoc.outages.dao.OutagesListProvider;

public class MainActivity extends SherlockFragmentActivity
        implements ActionBar.TabListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    public Intent serviceIntent;
    MainService service;
    boolean bound = false;
    ListView list;
    Tab activeTab;
    private boolean isDualPane = false;
    private String currentFilter;
    private SimpleCursorAdapter adapter;
    private FrameLayout detailsLayout;
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
        detailsLayout = (FrameLayout) findViewById(R.id.details_layout);
        isDualPane = detailsLayout != null && detailsLayout.getVisibility() == View.VISIBLE;

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
        if (activeTab.getText().equals(getString(R.string.nodes))) {
            String projection[] = {
                    DatabaseHelper.COL_NODE_ID,
                    DatabaseHelper.COL_TYPE,
                    DatabaseHelper.COL_LABEL,
                    DatabaseHelper.COL_CREATED_TIME,
                    DatabaseHelper.COL_SYS_CONTACT,
                    DatabaseHelper.COL_LABEL_SOURCE
            };
            Cursor nodesCursor = getContentResolver().query(
                    Uri.withAppendedPath(NodesListProvider.CONTENT_URI, String.valueOf(listItemId)),
                    projection, null, null, null);
            if (nodesCursor.moveToFirst()) {
                Integer nodeId = nodesCursor.getInt(0);
                String nodeType = nodesCursor.getString(1);
                String nodeLabel = nodesCursor.getString(2);
                String nodeCreatedTime = nodesCursor.getString(3);
                String nodeSysContact = nodesCursor.getString(4);
                String nodeLabelSource = nodesCursor.getString(5);
                Node node = new Node(nodeId, nodeLabel, nodeType, nodeCreatedTime, nodeSysContact, nodeLabelSource);

                if (isDualPane) {
                    showDetails(node.toString());
                } else {
                    Intent showContent = new Intent(getApplicationContext(), DetailsViewerActivity.class);
                    //showContent.putExtra("node", node);
                    showContent.putExtra("details", node.toString());
                    startActivity(showContent);
                }
            }
            nodesCursor.close();
        } else if (activeTab.getText().equals(getString(R.string.outages))) {
            String projection[] = {
                    DatabaseHelper.COL_OUTAGE_ID,
                    DatabaseHelper.COL_IP_ADDRESS,
                    DatabaseHelper.COL_IF_REGAINED_SERVICE,
                    DatabaseHelper.COL_SERVICE_TYPE_NAME,
                    DatabaseHelper.COL_IF_LOST_SERVICE
            };
            Cursor outagesCursor = getContentResolver().query(
                    Uri.withAppendedPath(OutagesListProvider.CONTENT_URI, String.valueOf(listItemId)),
                    projection, null, null, null);
            if (outagesCursor.moveToFirst()) {
                Integer outageId = outagesCursor.getInt(0);
                String outageIpAddress = outagesCursor.getString(1);
                String outageIfRegainedService = outagesCursor.getString(2);
                String outageServiceTypeName = outagesCursor.getString(3);
                String outageIfLostService = outagesCursor.getString(4);
                Outage outage = new Outage(outageId, outageIpAddress, outageIfLostService, outageIfRegainedService, outageServiceTypeName);

                if (isDualPane) {
                    showDetails(outage.toString());
                } else {
                    Intent showContent = new Intent(getApplicationContext(), DetailsViewerActivity.class);
                    //showContent.putExtra("outage", outage);
                    showContent.putExtra("details", outage.toString());
                    startActivity(showContent);
                }
            }
            outagesCursor.close();
        } else if (activeTab.getText().equals(getString(R.string.alarms))) {
            String projection[] = {
                    DatabaseHelper.COL_ALARM_ID,
                    DatabaseHelper.COL_SEVERITY,
                    DatabaseHelper.COL_DESCRIPTION,
                    DatabaseHelper.COL_LOG_MESSAGE
            };
            Cursor alarmsCursor = getContentResolver().query(
                    Uri.withAppendedPath(AlarmsListProvider.CONTENT_URI, String.valueOf(listItemId)),
                    projection,
                    null, null, null);
            if (alarmsCursor.moveToFirst()) {
                Integer alarmId = alarmsCursor.getInt(0);
                String alarmSeverity = alarmsCursor.getString(1);
                String alarmDescription = alarmsCursor.getString(2);
                String alarmLogMessage = alarmsCursor.getString(3);
                Alarm alarm = new Alarm(alarmId, alarmSeverity, alarmDescription, alarmLogMessage);

                if (isDualPane) {
                    showDetails(alarm.toString());
                } else {
                    Intent showContent = new Intent(getApplicationContext(), DetailsViewerActivity.class);
                    //showContent.putExtra("alarm", alarm);
                    showContent.putExtra("details", alarm.toString());
                    startActivity(showContent);
                }
            }
            alarmsCursor.close();
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
            adapter = new SimpleCursorAdapter(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    null,
                    new String[]{DatabaseHelper.COL_NODE_ID, DatabaseHelper.COL_LABEL},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        } else if (activeTab.getText().equals(getString(R.string.outages))) {
            adapter = new SimpleCursorAdapter(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    null,
                    new String[]{DatabaseHelper.COL_OUTAGE_ID, DatabaseHelper.COL_IP_ADDRESS},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        } else if (activeTab.getText().equals(getString(R.string.alarms))) {
            adapter = new SimpleCursorAdapter(getApplicationContext(),
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
            case R.id.menu_refresh_nodes:
                if (bound) {
                    service.refreshNodes();
                }
                return true;
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(getApplicationContext(),
                        SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.menu_about:
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.layout.about));
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(layout).setNeutralButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
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

    public void showDetails(String details) {
        TextView detailsView = (TextView) detailsLayout.findViewById(R.id.main_info);
        detailsView.setText(details);
    }

}