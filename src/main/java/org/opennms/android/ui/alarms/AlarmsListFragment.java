package org.opennms.android.ui.alarms;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
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

import org.opennms.android.App;
import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.data.storage.Contract;
import org.opennms.android.data.sync.AccountService;
import org.opennms.android.data.sync.UpdateManager;
import org.opennms.android.ui.BaseActivity;

import javax.inject.Inject;

public class AlarmsListFragment extends ListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>, ActionBar.OnNavigationListener {

  public static final String STATE_ACTIVE_ALARM_ID = "active_alarm_id";
  public static final String TAG = "AlarmsListFragment";
  private static final String SELECTION_OUTSTANDING = Contract.Alarms.ACK_USER + " IS NULL";
  private static final String SELECTION_ACKED = Contract.Alarms.ACK_USER + " IS NOT NULL";
  private static final int LOADER_ID = 0;
  private static final int LOAD_LIMIT = 30;
  private AlarmAdapter adapter;
  private boolean isDualPane = false;
  private FrameLayout detailsContainer;
  private Menu optionsMenu;
  private String cursorSelection = null;
  private Fragment activeDetailsFragment;
  private Handler removeDetailsHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      if (activeDetailsFragment != null) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(activeDetailsFragment);
        fragmentTransaction.commit();
        showEmptyDetails();
      }
    }
  };
  private Handler restoreHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      /** Restoring previously displayed alarm details fragment */
      long activeAlarmId = sharedPref.getLong(STATE_ACTIVE_ALARM_ID, -1);
      if (activeAlarmId != -1) {
        showDetails(activeAlarmId);
      } else {
        showEmptyDetails();
      }
    }
  };
  private SharedPreferences sharedPref;
  private boolean firstLoad = true;
  @Inject UpdateManager updateManager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    App.get(getActivity()).inject(this);

    sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

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

    detailsContainer = (FrameLayout) getActivity().findViewById(R.id.details_fragment_container);
    isDualPane = detailsContainer != null && detailsContainer.getVisibility() == View.VISIBLE;

    adapter = new AlarmAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    getListView().setAdapter(adapter);

    TextView emptyText = (TextView) getActivity().findViewById(R.id.empty_list_text);
    emptyText.setText(getString(R.string.alarms_list_empty));

    ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    SpinnerAdapter mSpinnerAdapter =
        ArrayAdapter.createFromResource(getActivity(), R.array.alarms_action_list,
                                        android.R.layout.simple_spinner_dropdown_item);
    actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
  }

  @Override
  public void onResume() {
    super.onResume();
    setRefreshIndicationState(updateManager.isUpdating(UpdateManager.UpdateType.ALARMS));
  }

  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    switch (itemPosition) {
      case 0:
        cursorSelection = SELECTION_OUTSTANDING;
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        return true;
      case 1:
        cursorSelection = SELECTION_ACKED;
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        return true;
    }
    return false;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    optionsMenu = menu;
    inflater.inflate(R.menu.list, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    boolean isDrawerOpen = ((BaseActivity) getActivity()).isDrawerOpen();
    MenuItem refreshItem = menu.findItem(R.id.menu_refresh);
    refreshItem.setVisible(!isDrawerOpen);
    setRefreshIndicationState(updateManager.isUpdating(UpdateManager.UpdateType.ALARMS));
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

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String[] projection = {
        Contract.Alarms._ID,
        Contract.Alarms.DESCRIPTION,
        Contract.Alarms.SEVERITY
    };
    return new CursorLoader(
        getActivity(),
        Contract.Alarms.CONTENT_URI,
        projection,
        cursorSelection,
        null,
        Contract.Alarms._ID + " DESC"
    );
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    adapter.swapCursor(cursor);
    if (isDualPane) {
      if (cursor.moveToFirst()) {
        /** If list is not empty, trying to restore previously displayed details. */
        restoreHandler.sendEmptyMessage(0);
      } else {
        /** If list became empty, removing previously displayed details */
        removeDetailsHandler.sendEmptyMessage(0);
      }
    }

    /** If there is no sync going and list is empty, refreshing list. */
    Account account = AccountService.getAccount();
    if (account != null) {
      boolean syncActive = ContentResolver.isSyncActive(account, Contract.CONTENT_AUTHORITY);
      boolean syncPending = ContentResolver.isSyncPending(account, Contract.CONTENT_AUTHORITY);
      if (!cursor.moveToFirst() && firstLoad && !(syncActive || syncPending)) {
        refreshList();
      }
    }
    firstLoad = false;
    setRefreshIndicationState(updateManager.isUpdating(UpdateManager.UpdateType.ALARMS));
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    adapter.swapCursor(null);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    showDetails(position);
  }

  private void showEmptyDetails() {
    detailsContainer.removeAllViews();
    LayoutInflater inflater =
        (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    RelativeLayout emptyView = (RelativeLayout) inflater.inflate(R.layout.empty_details, null);
    detailsContainer.addView(emptyView);
  }

  private void showDetails(int position) {
    getListView().setItemChecked(position, true);
    long id = getListView().getItemIdAtPosition(position);
    sharedPref.edit().putLong(STATE_ACTIVE_ALARM_ID, id).commit();
    showDetails(id);
  }

  private void showDetails(long id) {
    if (isDualPane) {
      detailsContainer.removeAllViews();
      activeDetailsFragment = new AlarmDetailsFragment(id);
      FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
      fragmentTransaction.replace(R.id.details_fragment_container, activeDetailsFragment);
      fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
      fragmentTransaction.commit();
    } else {
      Intent detailsIntent = new Intent(getActivity(), AlarmDetailsActivity.class);
      detailsIntent.putExtra(AlarmDetailsActivity.EXTRA_ALARM_ID, id);
      startActivity(detailsIntent);
    }
  }

  private void refreshList() {
    if (Utils.isOnline(getActivity())) {
      getActivity().getContentResolver().delete(Contract.Alarms.CONTENT_URI, null, null);
      updateManager.startUpdating(UpdateManager.UpdateType.ALARMS, LOAD_LIMIT, 0);
      setRefreshIndicationState(true);
    } else {
      Toast.makeText(getActivity(), getString(R.string.refresh_failed_offline), Toast.LENGTH_LONG)
          .show();
    }
  }

  public void setRefreshIndicationState(boolean refreshing) {
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