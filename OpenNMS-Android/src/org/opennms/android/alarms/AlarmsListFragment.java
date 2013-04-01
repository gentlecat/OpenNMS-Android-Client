package org.opennms.android.alarms;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import org.opennms.android.MainService;
import org.opennms.android.R;
import org.opennms.android.alarms.dao.AlarmsListProvider;
import org.opennms.android.dao.DatabaseHelper;

public class AlarmsListFragment extends SherlockListFragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 3;
    MainService service;
    boolean bound = false;
    SimpleCursorAdapter adapter;
    ListView list;
    FrameLayout detailsContainer;
    boolean isDualPane = false;
    private MenuItem refreshItem;
    private String currentFilter;
    private OnAlarmsListSelectedListener alarmsListSelectedListener;
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
            @Override
            public void onAlarmSelected(Alarm alarm) {
                displayDetails(alarm);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarms_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        list = (ListView) getSherlockActivity().findViewById(android.R.id.list);
        detailsContainer = (FrameLayout) getSherlockActivity().findViewById(R.id.details_fragment_container);
        isDualPane = detailsContainer != null && detailsContainer.getVisibility() == View.VISIBLE;

        adapter = new SimpleCursorAdapter(
                getSherlockActivity(),
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
            alarmsListSelectedListener.onAlarmSelected(alarm);
        }
        alarmsCursor.close();
    }

    private void displayDetails(Alarm alarm) {
        if (isDualPane) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            FrameLayout detailsContainer = (FrameLayout) getSherlockActivity().findViewById(R.id.details_fragment_container);
            detailsContainer.removeAllViews();
            AlarmDetailsFragment detailsFragment = new AlarmDetailsFragment();
            detailsFragment.bindAlarm(alarm);
            fragmentTransaction.add(R.id.details_fragment_container, detailsFragment);
            fragmentTransaction.commit();
        } else {
            Intent detailsIntent = new Intent(getSherlockActivity(), AlarmDetailsActivity.class);
            detailsIntent.putExtra("alarm", alarm);
            startActivity(detailsIntent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.alarms, menu);
        MenuItem searchItem = menu.add("Search");
        searchItem.setIcon(getResources().getDrawable(R.drawable.ic_action_search));
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        SearchView search = new SearchView(getActivity());
        search.setOnQueryTextListener(this);
        searchItem.setActionView(search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh_alarms:
                refreshItem = item;
                refreshList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshList() {
        if (bound) {
            LayoutInflater inflater = (LayoutInflater) getSherlockActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
            Animation rotation = AnimationUtils.loadAnimation(getSherlockActivity(), R.anim.refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            iv.startAnimation(rotation);
            refreshItem.setActionView(iv);
            service.refreshAlarms();
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
        if (refreshItem != null && refreshItem.getActionView() != null) {
            refreshItem.getActionView().clearAnimation();
            refreshItem.setActionView(null);
        }
        adapter.swapCursor(cursor);
        if (cursor.getColumnCount() > 0) {
            // TODO: Activate first item in list
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public interface OnAlarmsListSelectedListener {
        void onAlarmSelected(Alarm alarm);
    }

}