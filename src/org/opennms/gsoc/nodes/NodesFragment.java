package org.opennms.gsoc.nodes;

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
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.gsoc.MainService;
import org.opennms.gsoc.R;
import org.opennms.gsoc.dao.DatabaseHelper;
import org.opennms.gsoc.model.Node;
import org.opennms.gsoc.nodes.dao.NodesListProvider;

public class NodesFragment extends SherlockFragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    MainService service;
    boolean bound = false;
    private boolean isDualPane = false;
    private String currentFilter;
    private SimpleCursorAdapter adapter;
    private ListView list;
    private FrameLayout detailsLayout;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.LocalBinder binder = (MainService.LocalBinder) service;
            NodesFragment.this.service = binder.getService();
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
        return inflater.inflate(R.layout.nodes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        detailsLayout = (FrameLayout) getSherlockActivity().findViewById(R.id.node_details_layout);
        list = (ListView) getSherlockActivity().findViewById(android.R.id.list);

        isDualPane = detailsLayout != null && detailsLayout.getVisibility() == View.VISIBLE;

        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2, null,
                new String[]{DatabaseHelper.COL_NODE_ID, DatabaseHelper.COL_LABEL},
                new int[]{android.R.id.text1, android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        list.setAdapter(this.adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                String projection[] = {
                        DatabaseHelper.COL_NODE_ID,
                        DatabaseHelper.COL_TYPE,
                        DatabaseHelper.COL_LABEL,
                        DatabaseHelper.COL_CREATED_TIME,
                        DatabaseHelper.COL_SYS_CONTACT,
                        DatabaseHelper.COL_LABEL_SOURCE
                };
                Cursor nodesCursor = getActivity().getContentResolver().query(
                        Uri.withAppendedPath(NodesListProvider.CONTENT_URI, String.valueOf(id)),
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
                        showDetails(node);
                    } else {
                        Intent showContent = new Intent(getActivity().getApplicationContext(), NodeViewerActivity.class);
                        showContent.putExtra("node", node);
                        startActivity(showContent);
                    }
                }
                nodesCursor.close();
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
        inflater.inflate(R.menu.nodes, menu);
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
            case R.id.menu_refresh_nodes:
                if (bound) {
                    service.refreshNodes();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            baseUri = Uri.withAppendedPath(Uri.withAppendedPath(
                    NodesListProvider.CONTENT_URI, "label"),
                    Uri.encode(this.currentFilter)
            );
        } else {
            baseUri = NodesListProvider.CONTENT_URI;
        }
        String[] projection = {
                DatabaseHelper.TABLE_NODES_ID,
                DatabaseHelper.COL_NODE_ID,
                DatabaseHelper.COL_TYPE,
                DatabaseHelper.COL_LABEL,
                DatabaseHelper.COL_CREATED_TIME,
                DatabaseHelper.COL_SYS_CONTACT,
                DatabaseHelper.COL_LABEL_SOURCE
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

    public void showDetails(Node node) {
        TextView details = (TextView) detailsLayout.findViewById(R.id.node_info);
        details.setText(node.toString());
    }

}
