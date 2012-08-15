package org.opennms.gsoc.nodes;

import org.opennms.gsoc.R;
import org.opennms.gsoc.dao.OnmsDatabaseHelper;
import org.opennms.gsoc.model.OnmsNode;
import org.opennms.gsoc.nodes.dao.NodesListProvider;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NodesListFragment extends SherlockListFragment implements OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
	private OnNodesListSelectedListener nodesListSelectedListener;
	private String currentFilter;
	private Intent intent;
	private SimpleCursorAdapter adapter;
	private ProgressBar progressBarNodes;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		this.intent = new Intent(getActivity().getApplicationContext(), NodesService.class);

		setHasOptionsMenu(true);

		this.adapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, null,
				new String[] {OnmsDatabaseHelper.COL_NODE_ID, OnmsDatabaseHelper.COL_LABEL},
				new int[] { android.R.id.text1, android.R.id.text2}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		getListView().setAdapter(this.adapter);
		getActivity().getSupportLoaderManager().initLoader(0, null, this);

		new Thread(new ProgressBarThread()).start();

	}

	private class ProgressBarThread implements Runnable {

		@Override
		public void run() {
			NodesListFragment.this.progressBarNodes = (ProgressBar)getActivity().findViewById(R.id.progressBarNodes);
			while(NodesListFragment.this.adapter.isEmpty()) {
				NodesListFragment.this.progressBarNodes.setVisibility(View.VISIBLE);
			}
			NodesListFragment.this.progressBarNodes.setVisibility(View.GONE);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String projection[] = { OnmsDatabaseHelper.COL_NODE_ID, OnmsDatabaseHelper.COL_TYPE,  OnmsDatabaseHelper.COL_LABEL};
		Cursor nodesCursor = getActivity().getContentResolver().query(
				Uri.withAppendedPath(NodesListProvider.CONTENT_URI,
						String.valueOf(id)), projection, null, null, null);
		if (nodesCursor.moveToFirst()) {
			Integer nodeId = nodesCursor.getInt(0);
			String nodeType = nodesCursor.getString(1);
			String nodeLabel = nodesCursor.getString(2);
			OnmsNode onmsnode = new OnmsNode(nodeId, nodeLabel, nodeType);
			this.nodesListSelectedListener.onNodeSelected(onmsnode);
		}
		nodesCursor.close();
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.nodesListSelectedListener = new OnNodesListSelectedListener() {

				@Override
				public void onNodeSelected(OnmsNode node) {
					NodeViewerFragment viewer = (NodeViewerFragment) getActivity().getSupportFragmentManager()
							.findFragmentById(R.id.details);

					if (viewer == null || !viewer.isInLayout()) {
						Intent showContent = new Intent(getActivity().getApplicationContext(),
								NodeViewerActivity.class);
						showContent.putExtra("onmsnode", node);
						startActivity(showContent);
					} else {
						viewer.updateUrl(node);
					}
				}
			};
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnNodesSelectedListener");
		}
	}

	public interface OnNodesListSelectedListener {
		void onNodeSelected(OnmsNode nodeUrl);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.nodes_list, container, false);
	}

	@Override 
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item = menu.add("Search");
		item.setIcon(android.R.drawable.ic_menu_search);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		SearchView sv = new SearchView(getActivity());
		sv.setOnQueryTextListener(this);
		item.setActionView(sv);
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
			baseUri = Uri.withAppendedPath(Uri.withAppendedPath(NodesListProvider.CONTENT_URI, "label"),
					Uri.encode(this.currentFilter));
		} else {
			baseUri = NodesListProvider.CONTENT_URI;
		}
		String[] projection = { OnmsDatabaseHelper.TABLE_NODES_ID, OnmsDatabaseHelper.COL_NODE_ID, OnmsDatabaseHelper.COL_TYPE, OnmsDatabaseHelper.COL_LABEL };

		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				baseUri, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		this.adapter.swapCursor(cursor);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		this.adapter.swapCursor(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getApplicationContext().startService(this.intent);

	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().getApplicationContext().stopService(this.intent);
	}
}