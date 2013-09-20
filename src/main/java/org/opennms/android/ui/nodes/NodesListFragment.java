package org.opennms.android.ui.nodes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opennms.android.LoaderIDs;
import org.opennms.android.MainApplication;
import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.net.DataLoader;
import org.opennms.android.net.Response;
import org.opennms.android.parsing.NodesParser;
import org.opennms.android.provider.Contract;
import org.opennms.android.sync.LoadManager;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class NodesListFragment extends ListFragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor>,
        AbsListView.OnScrollListener {

    public static final String TAG = "NodesListFragment";
    public static final String STATE_ACTIVE_NODE_ID = "active_node_id";
    private static final int SCROLL_THRESHOLD = 5; // Must be more than 1
    private static final int LOAD_LIMIT = 25;
    private MainApplication app;
    private NodeAdapter adapter;
    private boolean isDualPane = false;
    private String currentFilter;
    private FrameLayout detailsContainer;
    private Menu optionsMenu;
    private SharedPreferences sharedPref;
    private Handler restoreHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            /** Restoring previously displayed node details fragment */
            long activeNodeId = sharedPref.getLong(STATE_ACTIVE_NODE_ID, -1);
            if (activeNodeId != -1) {
                showDetails(activeNodeId);
            }
        }
    };
    private boolean firstLoad = true;
    private View listFooter;
    private AsyncTask searchUpdateTask;
    private int currentBatch = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        app = (MainApplication) getActivity().getApplication();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

        getListView().setOnScrollListener(this);

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listFooter = inflater.inflate(R.layout.list_loading_footer, null);
        getListView().addFooterView(listFooter);

        adapter = new NodeAdapter(getActivity(), null,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        getListView().setAdapter(adapter);

        TextView emptyText = (TextView) getActivity().findViewById(R.id.empty_list_text);
        emptyText.setText(getString(R.string.nodes_list_empty));
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
        sharedPref.edit().putLong(STATE_ACTIVE_NODE_ID, id).commit();
        showDetails(id);
    }

    private void showDetails(long id) {
        if (isDualPane) {
            detailsContainer.removeAllViews();
            NodeDetailsFragment detailsFragment = new NodeDetailsFragment(id);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.details_fragment_container, detailsFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else {
            Intent detailsIntent = new Intent(getActivity(), NodeDetailsActivity.class);
            detailsIntent.putExtra(NodeDetailsActivity.EXTRA_NODE_ID, id);
            startActivity(detailsIntent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        optionsMenu = menu;
        inflater.inflate(R.menu.list, menu);
        inflater.inflate(R.menu.nodes, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

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
            getActivity().getContentResolver().delete(Contract.Nodes.CONTENT_URI, null, null);
            currentBatch = 1;
            if (app.serviceConnected) {
                app.loadManager.startLoading(LoadManager.LoadType.NODES, LOAD_LIMIT, 0);
                setRefreshActionButtonState(true);
            } else {
                Log.e(TAG, "LoadManager is not bound in Application. Cannot refresh list.");
            }
        } else {
            Toast.makeText(getActivity(),
                    getString(R.string.refresh_failed_offline),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String newFilter = !TextUtils.isEmpty(query) ? query : null;
        if (currentFilter == null && newFilter == null) {
            return true;
        }
        if (currentFilter != null && currentFilter.equals(newFilter)) {
            return true;
        }
        currentFilter = newFilter;
        if (searchUpdateTask != null) {
            searchUpdateTask.cancel(true);
        }
        setRefreshActionButtonState(true);
        searchUpdateTask = new SearchUpdate().execute();
        getActivity().getSupportLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        if (this.currentFilter != null) {
            baseUri = Uri.withAppendedPath(
                    Uri.withAppendedPath(Contract.Nodes.CONTENT_URI, Contract.Nodes.NAME),
                    Uri.encode(this.currentFilter));
        } else {
            baseUri = Contract.Nodes.CONTENT_URI;
        }
        String[] projection = {
                Contract.Nodes._ID,
                Contract.Nodes.NAME
        };
        return new CursorLoader(getActivity(), baseUri, projection, null, null,
                /** Sort order must be the same as in requests to server. */
                Contract.Nodes._ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        if (cursor.moveToFirst()) {
            if (isDualPane) {
                /** If list is not empty, trying to restore previously displayed details. */
                restoreHandler.sendEmptyMessage(0);
            }
        } else {
            /** If list is empty, refreshing it. */
            if (firstLoad) {
                refreshList();
            }
        }
        firstLoad = false;
        if (app.serviceConnected) {
            setRefreshActionButtonState(app.loadManager.isLoading(LoadManager.LoadType.NODES));
        }
        currentBatch = getListView().getCount() / LOAD_LIMIT;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().restartLoader(LoaderIDs.NODES, null, this);
        if (app.serviceConnected) {
            setRefreshActionButtonState(app.loadManager.isLoading(LoadManager.LoadType.NODES));
        }
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        if (refreshing) {
            listFooter.setVisibility(View.VISIBLE);
        } else {
            listFooter.setVisibility(View.GONE);
        }

        if (optionsMenu == null) {
            return;
        }
        final MenuItem refreshItem = optionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                MenuItemCompat.setActionView(refreshItem, R.layout.actionbar_indeterminate_progress);
            } else {
                MenuItemCompat.setActionView(refreshItem, null);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (app.serviceConnected && app.loadManager.isLoading(LoadManager.LoadType.NODES)) return;
        if (scrollState == SCROLL_STATE_IDLE) {
            if (getListView().getLastVisiblePosition() >= getListView().getCount() - SCROLL_THRESHOLD) {
                // TODO: Add search support
                app.loadManager.startLoading(LoadManager.LoadType.NODES, LOAD_LIMIT, LOAD_LIMIT * currentBatch);
                currentBatch++;
                setRefreshActionButtonState(true);
            }
        }
    }

    private class SearchUpdate extends AsyncTask<Void, Void, Response> {
        protected Response doInBackground(Void... voids) {
            try {
                return new DataLoader(getActivity()).nodes(LOAD_LIMIT, 0, currentFilter);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred while loading info from server", e);
                return null;
            }
        }

        protected void onPostExecute(Response response) {
            if (response != null && response.getCode() == HttpURLConnection.HTTP_OK) {
                /** Updating database records */
                ArrayList<ContentValues> values = NodesParser.parseMultiple(response.getMessage());
                getActivity().getContentResolver().bulkInsert(Contract.Nodes.CONTENT_URI,
                        values.toArray(new ContentValues[values.size()]));
            }
            setRefreshActionButtonState(false);
        }
    }

}