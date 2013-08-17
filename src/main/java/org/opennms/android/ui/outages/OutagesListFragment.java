package org.opennms.android.ui.outages;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.opennms.android.Loaders;
import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.provider.Contract;
import org.opennms.android.sync.AccountService;
import org.opennms.android.sync.SyncAdapter;
import org.opennms.android.sync.SyncUtils;

public class OutagesListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ActionBar.OnNavigationListener {

    private static final String SELECTION_CURRENT =
            Contract.Outages.SERVICE_REGAINED_TIME + " IS NULL";
    private static final String SELECTION_RESOLVED =
            Contract.Outages.SERVICE_REGAINED_TIME + " IS NOT NULL";
    private static final String SELECTION_ALL = null;
    private SimpleCursorAdapter adapter;
    private boolean isDualPane = false;
    private FrameLayout detailsContainer;
    private Object syncObserverHandle;
    private Menu optionsMenu;
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        @Override
        public void onStatusChanged(int which) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Account account = AccountService.getAccount();
                    if (account == null) {
                        setRefreshActionButtonState(false);
                        return;
                    }
                    boolean syncActive = ContentResolver
                            .isSyncActive(account, Contract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver
                            .isSyncPending(account, Contract.CONTENT_AUTHORITY);
                    setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };
    private String cursorSelection = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        detailsContainer =
                (FrameLayout) getActivity().findViewById(R.id.details_fragment_container);
        isDualPane = detailsContainer != null && detailsContainer.getVisibility() == View.VISIBLE;

        adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[]{Contract.Outages._ID, Contract.Outages.SERVICE_TYPE_NAME},
                new int[]{android.R.id.text1, android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        getListView().setAdapter(adapter);

        TextView emptyText = (TextView) getActivity().findViewById(R.id.empty_list_text);
        emptyText.setText(getString(R.string.outages_list_empty));

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        SpinnerAdapter mSpinnerAdapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.outages_action_list,
                                                android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        switch (itemPosition) {
            case 0:
                cursorSelection = SELECTION_CURRENT;
                getActivity().getSupportLoaderManager().restartLoader(Loaders.OUTAGES, null, this);
                return true;
            case 1:
                cursorSelection = SELECTION_RESOLVED;
                getActivity().getSupportLoaderManager().restartLoader(Loaders.OUTAGES, null, this);
                return true;
            case 2:
                cursorSelection = SELECTION_ALL;
                getActivity().getSupportLoaderManager().restartLoader(Loaders.OUTAGES, null, this);
                return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isDualPane) {
            detailsContainer.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout emptyView =
                    (RelativeLayout) inflater.inflate(R.layout.empty_details, null);
            detailsContainer.addView(emptyView);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    private void showDetails(int position) {
        getListView().setItemChecked(position, true);
        long id = getListView().getItemIdAtPosition(position);
        showDetails(id);
    }

    private void showDetails(long id) {
        if (isDualPane) {
            detailsContainer.removeAllViews();
            OutageDetailsFragment detailsFragment = new OutageDetailsFragment(id);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.details_fragment_container, detailsFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else {
            Intent detailsIntent = new Intent(getActivity(), OutageDetailsActivity.class);
            detailsIntent.putExtra(OutageDetailsActivity.EXTRA_OUTAGE_ID, id);
            startActivity(detailsIntent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        optionsMenu = menu;
        inflater.inflate(R.menu.list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshList() {
        if (Utils.isOnline(getActivity())) {
            SyncUtils.triggerRefresh(SyncAdapter.SYNC_TYPE_OUTAGES);
        } else {
            Toast.makeText(getActivity(),
                           getString(R.string.refresh_failed_offline),
                           Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                Contract.Outages._ID,
                Contract.Outages.SERVICE_TYPE_NAME
        };
        return new CursorLoader(
                getActivity(),
                Contract.Outages.CONTENT_URI,
                projection,
                cursorSelection,
                null,
                Contract.Outages._ID + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING
                         | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        syncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (syncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(syncObserverHandle);
            syncObserverHandle = null;
        }
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        if (optionsMenu == null) {
            return;
        }
        final MenuItem refreshItem = optionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                MenuItemCompat.setActionView(
                        refreshItem, R.layout.actionbar_indeterminate_progress);
            } else {
                MenuItemCompat.setActionView(refreshItem, null);
            }
        }
    }

}