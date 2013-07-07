package org.opennms.android.ui.alarms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import org.opennms.android.R;
import org.opennms.android.dao.Columns;
import org.opennms.android.dao.alarms.Alarm;
import org.opennms.android.dao.alarms.AlarmsListProvider;
import org.opennms.android.service.AlarmsSyncService;

public class AlarmsListFragment extends SherlockListFragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_ALARM = "alarm";
    private static final int LOADER_ID = 4;
    private static final String STATE_ACTIVE_ALARM_ID = "active_alarm_id";
    private AlarmAdapter adapter;
    private boolean isDualPane = false;
    private MenuItem refreshItem;
    private String currentFilter;
    private SharedPreferences sharedPref;
    private FrameLayout detailsContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        detailsContainer = (FrameLayout) getSherlockActivity().findViewById(R.id.details_fragment_container);
        isDualPane = detailsContainer != null && detailsContainer.getVisibility() == View.VISIBLE;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (isDualPane) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        adapter = new AlarmAdapter(getSherlockActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        getListView().setAdapter(adapter);

        TextView emptyText = (TextView) getActivity().findViewById(R.id.empty_list_text);
        emptyText.setText(getString(R.string.alarms_list_empty));

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        if (currentFilter != null) {
            baseUri = Uri.withAppendedPath(
                    Uri.withAppendedPath(AlarmsListProvider.CONTENT_URI, Columns.AlarmColumns.SEVERITY),
                    Uri.encode(currentFilter)
            );
        } else {
            baseUri = AlarmsListProvider.CONTENT_URI;
        }
        String[] projection = {
                Columns.AlarmColumns.TABLE_ID,
                Columns.AlarmColumns.ALARM_ID,
                Columns.AlarmColumns.DESCRIPTION,
                Columns.AlarmColumns.SEVERITY
        };
        return new CursorLoader(getActivity(), baseUri, projection, null, null,
                Columns.AlarmColumns.ALARM_ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        stopRefreshAnimation();
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isDualPane) {
            long activeAlarmId = sharedPref.getLong(STATE_ACTIVE_ALARM_ID, -1);
            if (activeAlarmId != -1) {
                showDetails(activeAlarmId);
            } else {
                detailsContainer.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                RelativeLayout emptyView = (RelativeLayout) inflater.inflate(R.layout.empty_details, null);
                detailsContainer.addView(emptyView);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRefreshAnimation();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    private void showDetails(int position) {
        getListView().setItemChecked(position, true);
        long id = getListView().getItemIdAtPosition(position);
        sharedPref.edit().putLong(STATE_ACTIVE_ALARM_ID, id).commit();
        showDetails(id);
    }

    private void showDetails(long id) {
        Alarm alarm = getAlarm(id);
        if (isDualPane) {
            detailsContainer.removeAllViews();
            AlarmDetailsFragment detailsFragment = new AlarmDetailsFragment(alarm);
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.details_fragment_container, detailsFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else {
            Intent detailsIntent = new Intent(getSherlockActivity(), AlarmDetailsActivity.class);
            detailsIntent.putExtra(EXTRA_ALARM, alarm);
            startActivity(detailsIntent);
        }
    }

    private Alarm getAlarm(long id) {
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(AlarmsListProvider.CONTENT_URI, String.valueOf(id)),
                null, null, null, null
        );
        if (cursor.moveToFirst()) {
            Alarm alarm = new Alarm(cursor.getInt(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.ALARM_ID)));
            alarm.setSeverity(cursor.getString(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.SEVERITY)));
            alarm.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.DESCRIPTION)));
            alarm.setLogMessage(cursor.getString(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.LOG_MESSAGE)));
            alarm.setFirstEventTime(cursor.getString(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.FIRST_EVENT_TIME)));
            alarm.setLastEventTime(cursor.getString(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.LAST_EVENT_TIME)));
            alarm.setLastEventId(cursor.getInt(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.LAST_EVENT_ID)));
            alarm.setLastEventSeverity(cursor.getString(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.LAST_EVENT_SEVERITY)));
            alarm.setNodeId(cursor.getInt(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.NODE_ID)));
            alarm.setNodeLabel(cursor.getString(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.NODE_LABEL)));
            alarm.setServiceTypeId(cursor.getInt(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.SERVICE_TYPE_ID)));
            alarm.setServiceTypeName(cursor.getString(cursor.getColumnIndexOrThrow(Columns.AlarmColumns.SERVICE_TYPE_NAME)));
            cursor.close();
            return alarm;
        }
        cursor.close();
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list, menu);
        MenuItem searchItem = menu.add("Search");
        searchItem.setIcon(getResources().getDrawable(R.drawable.ic_action_search));
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        SearchView search = new SearchView(getActivity());
        search.setOnQueryTextListener(this);
        searchItem.setActionView(search);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshItem = item;
                refreshList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshList() {
        startRefreshAnimation();
        Intent intent = new Intent(getActivity(), AlarmsSyncService.class);
        getActivity().startService(intent);
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

    private void startRefreshAnimation() {
        LayoutInflater inflater = (LayoutInflater) getSherlockActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
        Animation rotation = AnimationUtils.loadAnimation(getSherlockActivity(), R.anim.refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);
        refreshItem.setActionView(iv);
    }

    private void stopRefreshAnimation() {
        if (refreshItem != null && refreshItem.getActionView() != null) {
            refreshItem.getActionView().clearAnimation();
            refreshItem.setActionView(null);
        }
    }

}