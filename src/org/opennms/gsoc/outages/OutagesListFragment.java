package org.opennms.gsoc.outages;

import org.opennms.gsoc.R;
import org.opennms.gsoc.dao.OnmsDatabaseHelper;
import org.opennms.gsoc.model.OnmsOutage;
import org.opennms.gsoc.outages.dao.OutagesListProvider;

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
import android.util.Log;
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

public class OutagesListFragment extends SherlockListFragment implements OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor>{
	private SimpleCursorAdapter adapter;
	private Intent intent;
	private String currentFilter;
	private OnOutagesListSelectedListener outagesListSelectedListener;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.intent = new Intent(getActivity().getApplicationContext(), OutagesService.class);

		setHasOptionsMenu(true);
		this.adapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, null,
				new String[] {OnmsDatabaseHelper.COL_OUTAGE_ID, OnmsDatabaseHelper.COL_IP_ADDRESS},
				new int[] { android.R.id.text1, android.R.id.text2}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		getListView().setAdapter(this.adapter);
		getActivity().getSupportLoaderManager().initLoader(1, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String projection[] = { OnmsDatabaseHelper.COL_OUTAGE_ID,  OnmsDatabaseHelper.COL_IP_ADDRESS};
		Cursor outagesCursor = getActivity().getContentResolver().query(
				Uri.withAppendedPath(OutagesListProvider.CONTENT_URI,
						String.valueOf(id)), projection, null, null, null);
		if (outagesCursor.moveToFirst()) {
			Integer outageId = outagesCursor.getInt(0);
			String outageIpAddress = outagesCursor.getString(1);
			OnmsOutage onmsoutage = new OnmsOutage(outageId, outageIpAddress);
			Log.i("Outages list fragment", onmsoutage.toString());
			this.outagesListSelectedListener.onOutageSelected(onmsoutage);
		}
		outagesCursor.close();
	}

	public interface OnOutagesListSelectedListener {
		void onOutageSelected(OnmsOutage outage);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.outagesListSelectedListener = new OnOutagesListSelectedListener() {

				@Override
				public void onOutageSelected(OnmsOutage outage) {
					OutageViewerFragment viewer = (OutageViewerFragment) getActivity().getFragmentManager()
							.findFragmentById(R.id.outagesDetails);

					if (viewer == null || !viewer.isInLayout()) {
						Intent showContent = new Intent(getActivity().getApplicationContext(),
								OutageViewerActivity.class);
						showContent.putExtra("onmsoutage", outage);
						startActivity(showContent);
					} else {
						viewer.updateUrl(outage);
					}

				}
			};
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnOutagesSelectedListener");
		}
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.outages_list, container, false);
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
		getLoaderManager().restartLoader(0, null, this);
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
			baseUri = Uri.withAppendedPath(Uri.withAppendedPath(OutagesListProvider.CONTENT_URI, "ipaddress"),
					Uri.encode(this.currentFilter));
		} else {
			baseUri = OutagesListProvider.CONTENT_URI;
		}
		String[] projection = { OnmsDatabaseHelper.TABLE_OUTAGES_ID, OnmsDatabaseHelper.COL_OUTAGE_ID, OnmsDatabaseHelper.COL_IP_ADDRESS };
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
}
