package org.opennms.android.ui.nodes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import org.opennms.android.R;
import org.opennms.android.dao.Columns;
import org.opennms.android.dao.nodes.Node;
import org.opennms.android.dao.nodes.NodesListProvider;
import org.opennms.android.service.NodesSyncService;

public class NodesListFragment extends SherlockListFragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private SimpleCursorAdapter adapter;
    private boolean isDualPane = false;
    private MenuItem refreshItem;
    private String currentFilter;

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

        FrameLayout detailsContainer = (FrameLayout) getSherlockActivity().findViewById(R.id.details_fragment_container);
        isDualPane = detailsContainer != null && detailsContainer.getVisibility() == View.VISIBLE;

        if (isDualPane) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        adapter = new SimpleCursorAdapter(
                getSherlockActivity(),
                R.layout.node_list_item,
                null,
                new String[]{Columns.NodeColumns.NAME, Columns.NodeColumns.NODE_ID},
                new int[]{R.id.node_list_item_1, R.id.node_list_item_2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        getListView().setAdapter(adapter);

        TextView emptyText = (TextView) getActivity().findViewById(R.id.empty_text);
        emptyText.setText(getString(R.string.nodes_list_empty));

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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
        showDetails(getListView().getItemIdAtPosition(position));
    }

    private void showDetails(long id) {
        Node node = getNode(id);
        if (isDualPane) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            FrameLayout detailsContainer = (FrameLayout) getSherlockActivity().findViewById(R.id.details_fragment_container);
            detailsContainer.removeAllViews();
            NodeDetailsFragment detailsFragment = new NodeDetailsFragment(node);
            fragmentTransaction.add(R.id.details_fragment_container, detailsFragment);
            fragmentTransaction.commit();
        } else {
            Intent detailsIntent = new Intent(getSherlockActivity(), NodeDetailsActivity.class);
            detailsIntent.putExtra("node", node);
            startActivity(detailsIntent);
        }
    }

    private Node getNode(long id) {
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(NodesListProvider.CONTENT_URI, String.valueOf(id)),
                null, null, null, null
        );
        if (cursor.moveToFirst()) {
            Node node = new Node((cursor.getInt(cursor.getColumnIndexOrThrow(Columns.NodeColumns.NODE_ID))));
            node.setType(cursor.getString(cursor.getColumnIndexOrThrow(Columns.NodeColumns.TYPE)));
            node.setName(cursor.getString(cursor.getColumnIndexOrThrow(Columns.NodeColumns.NAME)));
            node.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(Columns.NodeColumns.CREATED_TIME)));
            node.setSysContact(cursor.getString(cursor.getColumnIndexOrThrow(Columns.NodeColumns.SYS_CONTACT)));
            node.setLabelSource(cursor.getString(cursor.getColumnIndexOrThrow(Columns.NodeColumns.LABEL_SOURCE)));
            node.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(Columns.NodeColumns.LOCATION)));
            node.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Columns.NodeColumns.DESCRIPTION)));
            cursor.close();
            return node;
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
        Intent intent = new Intent(getActivity(), NodesSyncService.class);
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
                    Uri.withAppendedPath(NodesListProvider.CONTENT_URI, Columns.NodeColumns.NAME),
                    Uri.encode(this.currentFilter)
            );
        } else {
            baseUri = NodesListProvider.CONTENT_URI;
        }
        String[] projection = {
                Columns.NodeColumns.TABLE_ID,
                Columns.NodeColumns.NODE_ID,
                Columns.NodeColumns.NAME
        };
        return new CursorLoader(getActivity(), baseUri, projection, null, null,
                Columns.NodeColumns.NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        stopRefreshAnimation();
        adapter.swapCursor(cursor);
        if (isDualPane && !adapter.isEmpty() && isVisible()) showDetails(0);
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