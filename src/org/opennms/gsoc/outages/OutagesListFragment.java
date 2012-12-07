package org.opennms.gsoc.outages;

import android.app.Activity;
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
import org.opennms.gsoc.MainService;
import org.opennms.gsoc.R;
import org.opennms.gsoc.dao.DatabaseHelper;
import org.opennms.gsoc.model.Outage;
import org.opennms.gsoc.outages.dao.OutagesListProvider;

public class OutagesListFragment extends SherlockListFragment implements OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    MainService service;
    boolean bound = false;
    private SimpleCursorAdapter adapter;
    private String currentFilter;
    private OnOutagesListSelectedListener outagesListSelectedListener;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.LocalBinder binder = (MainService.LocalBinder) service;
            OutagesListFragment.this.service = binder.getService();
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
        Intent serviceIntent = new Intent(getActivity().getApplicationContext(), MainService.class);
        getSherlockActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        this.adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2, null,
                new String[]{DatabaseHelper.COL_OUTAGE_ID, DatabaseHelper.COL_IP_ADDRESS},
                new int[]{android.R.id.text1, android.R.id.text2}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        getListView().setAdapter(this.adapter);
        getActivity().getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String projection[] = {
                DatabaseHelper.COL_OUTAGE_ID,
                DatabaseHelper.COL_IP_ADDRESS,
                DatabaseHelper.COL_IF_REGAINED_SERVICE,
                DatabaseHelper.COL_SERVICE_TYPE_NAME,
                DatabaseHelper.COL_IF_LOST_SERVICE
        };
        Cursor outagesCursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(OutagesListProvider.CONTENT_URI, String.valueOf(id)),
                projection, null, null, null);
        if (outagesCursor.moveToFirst()) {
            Integer outageId = outagesCursor.getInt(0);
            String outageIpAddress = outagesCursor.getString(1);
            String outageIfRegainedService = outagesCursor.getString(2);
            String outageServiceTypeName = outagesCursor.getString(3);
            String outageIfLostService = outagesCursor.getString(4);
            Outage outage = new Outage(outageId, outageIpAddress, outageIfLostService, outageIfRegainedService, outageServiceTypeName);
            Log.i("Outages list fragment", outage.toString());
            this.outagesListSelectedListener.onOutageSelected(outage);
        }
        outagesCursor.close();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.outagesListSelectedListener = new OnOutagesListSelectedListener() {

                @Override
                public void onOutageSelected(Outage outage) {
                    OutageDetailsFragment viewer = (OutageDetailsFragment) getActivity().getSupportFragmentManager()
                            .findFragmentById(R.id.outage_details_fragment);

                    if (viewer == null || !viewer.isInLayout()) {
                        Intent showContent = new Intent(getActivity().getApplicationContext(),
                                OutageViewerActivity.class);
                        showContent.putExtra("outage", outage);
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
    public void onStop() {
        super.onStop();
        if (bound) {
            getSherlockActivity().unbindService(serviceConnection);
            bound = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.outages_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.outages, menu);
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
            case R.id.menu_refresh_outages:
                if (bound) {
                    service.refreshOutages();
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
            baseUri = Uri.withAppendedPath(
                    Uri.withAppendedPath(OutagesListProvider.CONTENT_URI, "ipaddress"),
                    Uri.encode(this.currentFilter)
            );
        } else {
            baseUri = OutagesListProvider.CONTENT_URI;
        }
        String[] projection = {
                DatabaseHelper.TABLE_OUTAGES_ID,
                DatabaseHelper.COL_OUTAGE_ID,
                DatabaseHelper.COL_IP_ADDRESS,
                DatabaseHelper.COL_IF_REGAINED_SERVICE,
                DatabaseHelper.COL_SERVICE_TYPE_NAME,
                DatabaseHelper.COL_IF_LOST_SERVICE
        };
        return new CursorLoader(getActivity(), baseUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.adapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
    }

    public interface OnOutagesListSelectedListener {
        void onOutageSelected(Outage outage);
    }
}
