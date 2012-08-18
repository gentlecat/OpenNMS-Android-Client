package org.opennms.gsoc.alarms;

import org.opennms.gsoc.R;
import org.opennms.gsoc.alarms.dao.AlarmsListProvider;
import org.opennms.gsoc.dao.OnmsDatabaseHelper;
import org.opennms.gsoc.model.OnmsAlarm;

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

public class AlarmsListFragment extends SherlockListFragment implements OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
	private OnAlarmsListSelectedListener alarmsListSelectedListener;
	private String currentFilter;
	private Intent intent;
	private SimpleCursorAdapter adapter;
	private ProgressBar progressBarAlarms;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		this.intent = new Intent(getActivity().getApplicationContext(), AlarmsService.class);

		setHasOptionsMenu(true);

		this.adapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, null,
				new String[] {OnmsDatabaseHelper.COL_ALARM_ID, OnmsDatabaseHelper.COL_SEVERITY},
				new int[] { android.R.id.text1, android.R.id.text2}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		getListView().setAdapter(this.adapter);
		getActivity().getSupportLoaderManager().initLoader(3, null, this);

		new Thread(new ProgressBarThread()).start();

	}

	private class ProgressBarThread implements Runnable {

		@Override
		public void run() {
			AlarmsListFragment.this.progressBarAlarms = (ProgressBar)getActivity().findViewById(R.id.progressBarAlarms);

			while(AlarmsListFragment.this.adapter.isEmpty()) {
				AlarmsListFragment.this.progressBarAlarms.setVisibility(View.VISIBLE);
			}
			//NodesListFragment.this.progressBarNodes.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String projection[] = { OnmsDatabaseHelper.COL_ALARM_ID, OnmsDatabaseHelper.COL_SEVERITY,  OnmsDatabaseHelper.COL_DESCRIPTION, OnmsDatabaseHelper.COL_LOG_MESSAGE};
		Cursor alarmsCursor = getActivity().getContentResolver().query(
				Uri.withAppendedPath(AlarmsListProvider.CONTENT_URI,
						String.valueOf(id)), projection, null, null, null);
		if (alarmsCursor.moveToFirst()) {
			Integer alarmId = alarmsCursor.getInt(0);
			String alarmSeverity = alarmsCursor.getString(1);
			String alarmDescription = alarmsCursor.getString(2);
			String alarmLogMessage = alarmsCursor.getString(3);
			OnmsAlarm onmsalarm = new OnmsAlarm(alarmId, alarmSeverity, alarmDescription, alarmLogMessage);
			this.alarmsListSelectedListener.onAlarmSelected(onmsalarm);
		}
		alarmsCursor.close();
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.alarmsListSelectedListener = new OnAlarmsListSelectedListener() {

				@Override
				public void onAlarmSelected(OnmsAlarm alarm) {
					AlarmViewerFragment viewer = (AlarmViewerFragment) getActivity().getSupportFragmentManager()
							.findFragmentById(R.id.alarmsDetails);

					if (viewer == null || !viewer.isInLayout()) {
						Intent showContent = new Intent(getActivity().getApplicationContext(),
								AlarmViewerActivity.class);
						showContent.putExtra("onmsalarm", alarm);
						startActivity(showContent);
					} else {
						viewer.updateUrl(alarm);
					}
				}
			};
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnAlarmsSelectedListener");
		}
	}

	public interface OnAlarmsListSelectedListener {
		void onAlarmSelected(OnmsAlarm alarmUrl);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.alarms_list, container, false);
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
			baseUri = Uri.withAppendedPath(Uri.withAppendedPath(AlarmsListProvider.CONTENT_URI, "severity"),
					Uri.encode(this.currentFilter));
		} else {
			baseUri = AlarmsListProvider.CONTENT_URI;
		}
		String[] projection = { OnmsDatabaseHelper.TABLE_ALARMS_ID, OnmsDatabaseHelper.COL_ALARM_ID, OnmsDatabaseHelper.COL_SEVERITY, OnmsDatabaseHelper.COL_DESCRIPTION, OnmsDatabaseHelper.COL_LOG_MESSAGE };

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