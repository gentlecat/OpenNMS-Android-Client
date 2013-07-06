package org.opennms.android.ui.outages;

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
import android.support.v4.widget.SimpleCursorAdapter;
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
import org.opennms.android.dao.outages.Outage;
import org.opennms.android.dao.outages.OutagesListProvider;
import org.opennms.android.service.OutagesSyncService;

public class OutagesListFragment extends SherlockListFragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 2;
    private static final String STATE_ACTIVE_OUTAGE_ID = "active_outage_id";
    private SimpleCursorAdapter adapter;
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

        adapter = new SimpleCursorAdapter(
                getSherlockActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[]{Columns.OutageColumns.OUTAGE_ID, Columns.OutageColumns.SERVICE_TYPE_NAME},
                new int[]{android.R.id.text1, android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        getListView().setAdapter(adapter);

        TextView emptyText = (TextView) getActivity().findViewById(R.id.empty_list_text);
        emptyText.setText(getString(R.string.outages_list_empty));

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isDualPane) {
            long activeOutageId = sharedPref.getLong(STATE_ACTIVE_OUTAGE_ID, -1);
            if (activeOutageId != -1) {
                Outage outage = getOutage(activeOutageId);
                showDetails(outage);
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
        sharedPref.edit().putLong(STATE_ACTIVE_OUTAGE_ID, id).commit();
        Outage outage = getOutage(id);
        showDetails(outage);
    }

    private void showDetails(Outage outage) {
        if (isDualPane) {
            detailsContainer.removeAllViews();
            OutageDetailsFragment detailsFragment = new OutageDetailsFragment();
            detailsFragment.bindOutage(outage);
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.details_fragment_container, detailsFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else {
            Intent detailsIntent = new Intent(getSherlockActivity(), OutageDetailsActivity.class);
            detailsIntent.putExtra("outage", outage);
            startActivity(detailsIntent);
        }
    }

    private Outage getOutage(long id) {
        String projection[] = {
                Columns.OutageColumns.OUTAGE_ID,
                Columns.OutageColumns.IP_ADDRESS,
                Columns.OutageColumns.IF_REGAINED_SERVICE,
                Columns.OutageColumns.SERVICE_TYPE_NAME,
                Columns.OutageColumns.IF_LOST_SERVICE
        };
        Cursor outagesCursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(OutagesListProvider.CONTENT_URI, String.valueOf(id)),
                projection, null, null, null);
        if (outagesCursor.moveToFirst()) {
            Outage outage = new Outage(outagesCursor.getInt(0));
            outage.setIpAddress(outagesCursor.getString(1));
            outage.setIfRegainedService(outagesCursor.getString(2));
            outage.setServiceTypeName(outagesCursor.getString(3));
            outage.setIfLostService(outagesCursor.getString(4));
            outagesCursor.close();
            return outage;
        }
        outagesCursor.close();
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
        Intent intent = new Intent(getActivity(), OutagesSyncService.class);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        if (this.currentFilter != null) {
            baseUri = Uri.withAppendedPath(
                    Uri.withAppendedPath(OutagesListProvider.CONTENT_URI, Columns.OutageColumns.OUTAGE_ID),
                    Uri.encode(this.currentFilter)
            );
        } else {
            baseUri = OutagesListProvider.CONTENT_URI;
        }
        String[] projection = {
                Columns.OutageColumns.TABLE_ID,
                Columns.OutageColumns.OUTAGE_ID,
                Columns.OutageColumns.SERVICE_TYPE_NAME
        };
        return new CursorLoader(getActivity(), baseUri, projection, null, null,
                Columns.OutageColumns.OUTAGE_ID + " DESC");
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