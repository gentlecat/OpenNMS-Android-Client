package org.opennms.gsoc.alarms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.gsoc.MainService;
import org.opennms.gsoc.R;
import org.opennms.gsoc.alarms.dao.AlarmsListProvider;
import org.opennms.gsoc.dao.DatabaseHelper;
import org.opennms.gsoc.model.Alarm;

public class AlarmsFragment extends SherlockFragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 3;
    MainService service;
    boolean bound = false;
    private boolean isDualPane = false;
    private String currentFilter;
    private SimpleCursorAdapter adapter;
    private ListView list;
    private TableLayout detailsLayout;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.LocalBinder binder = (MainService.LocalBinder) service;
            AlarmsFragment.this.service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.alarms, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        detailsLayout = (TableLayout) getSherlockActivity().findViewById(R.id.alarm_details_layout);
        list = (ListView) getSherlockActivity().findViewById(android.R.id.list);

        isDualPane = detailsLayout != null && detailsLayout.getVisibility() == View.VISIBLE;

        adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[]{DatabaseHelper.COL_ALARM_ID, DatabaseHelper.COL_SEVERITY},
                new int[]{android.R.id.text1, android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        list.setAdapter(this.adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                String projection[] = {
                        DatabaseHelper.COL_ALARM_ID,
                        DatabaseHelper.COL_SEVERITY,
                        DatabaseHelper.COL_DESCRIPTION,
                        DatabaseHelper.COL_LOG_MESSAGE
                };
                Cursor alarmsCursor = getActivity().getContentResolver().query(
                        Uri.withAppendedPath(AlarmsListProvider.CONTENT_URI, String.valueOf(id)),
                        projection,
                        null, null, null);
                if (alarmsCursor.moveToFirst()) {
                    Integer alarmId = alarmsCursor.getInt(0);
                    String alarmSeverity = alarmsCursor.getString(1);
                    String alarmDescription = alarmsCursor.getString(2);
                    String alarmLogMessage = alarmsCursor.getString(3);
                    Alarm alarm = new Alarm(alarmId, alarmSeverity, alarmDescription, alarmLogMessage);

                    if (isDualPane) {
                        showDetails(alarm);
                    } else {
                        Intent showContent = new Intent(getActivity().getApplicationContext(), AlarmViewerActivity.class);
                        showContent.putExtra("alarm", alarm);
                        startActivity(showContent);
                    }
                }
                alarmsCursor.close();
            }
        });

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(getActivity().getApplicationContext(), MainService.class);
        getSherlockActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound) {
            getSherlockActivity().unbindService(serviceConnection);
            bound = false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.alarms, menu);
        // Adding search
        MenuItem item = menu.add("Search");
        item.setIcon(getResources().getDrawable(R.drawable.ic_action_search));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        SearchView sv = new SearchView(getActivity());
        sv.setOnQueryTextListener(this);
        item.setActionView(sv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh_alarms:
                if (bound) {
                    service.refreshAlarms();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        if (currentFilter == null && newFilter == null) {
            return true;
        }
        if (currentFilter != null && currentFilter.equals(newFilter)) {
            return true;
        }
        currentFilter = newFilter;
        getActivity().getSupportLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        if (currentFilter != null) {
            baseUri = Uri.withAppendedPath(
                    Uri.withAppendedPath(AlarmsListProvider.CONTENT_URI, "severity"),
                    Uri.encode(currentFilter)
            );
        } else {
            baseUri = AlarmsListProvider.CONTENT_URI;
        }
        String[] projection = {
                DatabaseHelper.TABLE_ALARMS_ID,
                DatabaseHelper.COL_ALARM_ID,
                DatabaseHelper.COL_SEVERITY,
                DatabaseHelper.COL_DESCRIPTION,
                DatabaseHelper.COL_LOG_MESSAGE
        };
        return new CursorLoader(getActivity(), baseUri, projection, null, null, null);
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
        this.adapter.swapCursor(null);
    }

    public void showDetails(Alarm alarm) {
                // Alarm ID
                TextView id = (TextView) getActivity().findViewById(R.id.alarm_id);
                id.setText(getResources().getString(R.string.alarms_info_id) + alarm.getId());

                // Severity
                TextView severity = (TextView) getActivity().findViewById(R.id.alarm_severity);
                severity.setText(String.valueOf(alarm.getSeverity()));
                TableRow severityRow = (TableRow) getActivity().findViewById(R.id.alarm_severity_row);
                // TODO: Check for all possible conditions
                // TODO: Adjust colors
                if (alarm.getSeverity().equals("CLEARED")) {
                    severityRow.setBackgroundColor(Color.GREEN);
                } else if (alarm.getSeverity().equals("MINOR")) {
                    severityRow.setBackgroundColor(Color.YELLOW);
                } else if (alarm.getSeverity().equals("MAJOR")) {
                    severityRow.setBackgroundColor(Color.RED);
                }

                // Description
                TextView description = (TextView) getActivity().findViewById(R.id.alarm_description);
                description.setText(Html.fromHtml(alarm.getDescription()));

                // Log message
                TextView message = (TextView) getActivity().findViewById(R.id.alarm_message);
                message.setText(String.valueOf(alarm.getLogMessage()));
   }

}
