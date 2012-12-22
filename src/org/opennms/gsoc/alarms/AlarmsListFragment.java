package org.opennms.gsoc.alarms;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.gsoc.MainService;
import org.opennms.gsoc.R;
import org.opennms.gsoc.alarms.dao.AlarmsListProvider;
import org.opennms.gsoc.dao.DatabaseHelper;
import org.opennms.gsoc.model.Alarm;

public class AlarmsListFragment extends SherlockListFragment
        implements OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    private OnAlarmsListSelectedListener alarmsListSelectedListener;
    private String currentFilter;
    private SimpleCursorAdapter adapter;
    private AlarmDetailsFragment detailsFragment;
    private static final int LOADER_ID = 3;
    MainService service;
    boolean bound = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.LocalBinder binder = (MainService.LocalBinder) service;
            AlarmsListFragment.this.service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(getActivity().getApplicationContext(), MainService.class);
        getSherlockActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        alarmsListSelectedListener = new OnAlarmsListSelectedListener() {
            boolean isInfoVisible = false;

            @Override
            public void onAlarmSelected(Alarm alarm) {
                detailsFragment = (AlarmDetailsFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.alarm_details_fragment);
                if (detailsFragment != null) {
                    // If details fragment is available, we're in two-pane layout...
                    // Updating info on the right pane
                    detailsFragment.show(alarm);
                    if (!isInfoVisible) {
                        getActivity().findViewById(R.id.alarms_info_noneselected).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.alarm_info).setVisibility(View.VISIBLE);
                        isInfoVisible = true;
                    }
                } else {
                    // Otherwise, we're in the one-pane layout and must start a separate activity...
                    Intent showContent = new Intent(getActivity().getApplicationContext(), AlarmViewerActivity.class);
                    showContent.putExtra("alarm", alarm);
                    startActivity(showContent);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        detailsFragment = (AlarmDetailsFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.alarm_details_fragment);
        return inflater.inflate(R.layout.list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[]{DatabaseHelper.COL_ALARM_ID, DatabaseHelper.COL_SEVERITY},
                new int[]{android.R.id.text1, android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        getListView().setAdapter(adapter);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        String projection[] = { // A list of which columns to return
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
            alarmsListSelectedListener.onAlarmSelected(alarm);
        }
        alarmsCursor.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.alarms, menu);
        MenuItem searchItem = menu.add("Search");
        searchItem.setIcon(getResources().getDrawable(R.drawable.ic_action_search));
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView search = new SearchView(getActivity());
        search.setOnQueryTextListener(this);
        searchItem.setActionView(search);
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
            // If details fragment is available, show info about first alarm...
            if (detailsFragment != null) {
                onListItemClick(getListView(), null, 0, 0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public interface OnAlarmsListSelectedListener {
        void onAlarmSelected(Alarm alarmUrl);
    }

}