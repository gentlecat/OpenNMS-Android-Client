package org.opennms.android.ui.events;

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
import org.opennms.android.R;
import org.opennms.android.dao.Columns;
import org.opennms.android.dao.events.Event;
import org.opennms.android.dao.events.EventsListProvider;
import org.opennms.android.service.SyncService;

public class EventsListFragment extends SherlockListFragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 3;
    SyncService service;
    boolean bound = false;
    SimpleCursorAdapter adapter;
    boolean isDualPane = false;
    private MenuItem refreshItem;
    private String currentFilter;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SyncService.LocalBinder binder = (SyncService.LocalBinder) service;
            EventsListFragment.this.service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent refreshService = new Intent(getActivity().getApplicationContext(), SyncService.class);
        getSherlockActivity().bindService(refreshService, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FrameLayout detailsContainer = (FrameLayout) getSherlockActivity().findViewById(R.id.details_fragment_container);
        isDualPane = detailsContainer != null && detailsContainer.getVisibility() == View.VISIBLE;

        if (isDualPane) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        adapter = new SimpleCursorAdapter(
                getSherlockActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[]{Columns.EventColumns.COL_EVENT_ID, Columns.EventColumns.COL_SEVERITY},
                new int[]{android.R.id.text1, android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        getListView().setAdapter(adapter);

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRefreshAnimation();
        if (bound) {
            getSherlockActivity().unbindService(serviceConnection);
            bound = false;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    private void showDetails(int position) {
        getListView().setItemChecked(position, true);
        Event event = getEvent(getListView().getItemIdAtPosition(position));
        showDetails(event);
    }

    private void showDetails(Event event) {
        if (isDualPane) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            FrameLayout detailsContainer = (FrameLayout) getSherlockActivity().findViewById(R.id.details_fragment_container);
            detailsContainer.removeAllViews();
            EventDetailsFragment detailsFragment = new EventDetailsFragment();
            detailsFragment.bindEvent(event);
            fragmentTransaction.add(R.id.details_fragment_container, detailsFragment);
            fragmentTransaction.commit();
        } else {
            Intent detailsIntent = new Intent(getSherlockActivity(), EventDetailsActivity.class);
            detailsIntent.putExtra("event", event);
            startActivity(detailsIntent);
        }
    }

    private Event getEvent(long id) {
        String projection[] = {
                Columns.EventColumns.COL_EVENT_ID,
                Columns.EventColumns.COL_SEVERITY,
                Columns.EventColumns.COL_LOG_MESSAGE,
                Columns.EventColumns.COL_DESCRIPTION,
                Columns.EventColumns.COL_HOST,
                Columns.EventColumns.COL_IP_ADDRESS,
                Columns.EventColumns.COL_CREATE_TIME,
                Columns.EventColumns.COL_NODE_ID,
                Columns.EventColumns.COL_NODE_LABEL,
        };
        Cursor eventsCursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(EventsListProvider.CONTENT_URI, String.valueOf(id)),
                projection, null, null, null);
        if (eventsCursor.moveToFirst()) {
            Event event = new Event(eventsCursor.getInt(0));
            event.setSeverity(eventsCursor.getString(1));
            event.setLogMessage(eventsCursor.getString(2));
            event.setDescription(eventsCursor.getString(3));
            event.setHost(eventsCursor.getString(4));
            event.setIpAddress(eventsCursor.getString(5));
            event.setCreateTime(eventsCursor.getString(6));
            event.setNodeId(eventsCursor.getInt(7));
            event.setNodeLabel(eventsCursor.getString(8));
            eventsCursor.close();
            return event;
        }
        eventsCursor.close();
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
        if (bound) {
            startRefreshAnimation();
            service.refreshEvents();
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
                    Uri.withAppendedPath(EventsListProvider.CONTENT_URI, Columns.EventColumns.COL_SEVERITY),
                    Uri.encode(currentFilter)
            );
        } else {
            baseUri = EventsListProvider.CONTENT_URI;
        }
        String[] projection = {
                Columns.EventColumns.TABLE_EVENT_ID,
                Columns.EventColumns.COL_EVENT_ID,
                Columns.EventColumns.COL_SEVERITY
        };
        return new CursorLoader(getActivity(), baseUri, projection, null, null,
                Columns.EventColumns.COL_EVENT_ID + " DESC");
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