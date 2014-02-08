package org.opennms.android.ui.outages;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.opennms.android.R;
import org.opennms.android.data.ContentValuesGenerator;
import org.opennms.android.data.api.model.Outage;
import org.opennms.android.data.storage.Contract;
import org.opennms.android.ui.ActivityUtils;
import org.opennms.android.ui.DetailsFragment;

public class OutageDetailsFragment extends DetailsFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "OutageDetailsFragment";
    private static final int LOADER_ID = 0x3;
    private long outageId;
    private LoaderManager loaderManager;

    // Do not remove
    public OutageDetailsFragment() {
    }

    public OutageDetailsFragment(long outageId) {
        this.outageId = outageId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        return new CursorLoader(getActivity(),
                Uri.withAppendedPath(Contract.Outages.CONTENT_URI,
                        String.valueOf(outageId)),
                null, null, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!isAdded()) {
            return;
        }

        /** Checking if data has been loaded from the DB */
        if (cursor != null && cursor.moveToFirst()) {
            updateContent(cursor);
        } else {
            /** If not, trying to get information from the server */
            new GetDetailsFromServer().execute();
        }

        cursor.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        loaderManager = getLoaderManager();
        loaderManager.restartLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_loading, container, false);
    }

    public void updateContent(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }

        RelativeLayout detailsContainer =
                (RelativeLayout) getActivity().findViewById(R.id.details_container);
        if (detailsContainer == null) {
            return;
        }
        detailsContainer.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.outage_details, detailsContainer);

        LinearLayout detailsLayout =
                (LinearLayout) getActivity().findViewById(R.id.outage_details);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages._ID));
        TextView idView = (TextView) getActivity().findViewById(R.id.outage_id);
        idView.setText(getString(R.string.outage_details_id) + id);

        String ipAddress = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Outages.IP_ADDRESS));
        TextView ipAddressView = (TextView) getActivity().findViewById(R.id.outage_ip_address);
        ipAddressView.setText(ipAddress);

        int ipInterfaceId = cursor.getInt(
                cursor.getColumnIndexOrThrow(Contract.Outages.IP_INTERFACE_ID));
        TextView ipInterfaceIdView =
                (TextView) getActivity().findViewById(R.id.outage_ip_interface_id);
        ipInterfaceIdView.setText(String.valueOf(ipInterfaceId));

        final int nodeId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.NODE_ID));
        String nodeLabel = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Outages.NODE_LABEL));
        TextView nodeView = (TextView) getActivity().findViewById(R.id.outage_node);
        nodeView.setText(nodeLabel + " (#" + nodeId + ")");
        nodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.showNodeDetails(getActivity(), nodeId);
            }
        });

        String serviceLostTime = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_LOST_TIME));
        final int serviceLostEventId = cursor.getInt(
                cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_LOST_EVENT_ID));
        TextView lostServiceEvent =
                (TextView) getActivity().findViewById(R.id.outage_lost_service_event);
        lostServiceEvent.setText(serviceLostTime + "\n#" + serviceLostEventId);
        lostServiceEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.showEventDetails(getActivity(), serviceLostEventId);
            }
        });

        String serviceRegainedTime = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_REGAINED_TIME));
        final int serviceRegainedEventId = cursor.getInt(
                cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_REGAINED_EVENT_ID));
        TextView regainedServiceEvent =
                (TextView) getActivity().findViewById(R.id.outage_regained_service_event);
        if (serviceRegainedTime != null) {
            regainedServiceEvent.setText(serviceRegainedTime + "\n#" + serviceRegainedEventId);
            regainedServiceEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.showEventDetails(getActivity(), serviceLostEventId);
                }
            });
        } else {
            detailsLayout.removeView(regainedServiceEvent);
            TextView title = (TextView) getActivity()
                    .findViewById(R.id.outage_regained_service_event_title);
            detailsLayout.removeView(title);
        }

        int serviceId = cursor.getInt(
                cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_ID));
        TextView serviceIdView = (TextView) getActivity().findViewById(R.id.outage_service_id);
        serviceIdView.setText(String.valueOf(serviceId));

        int serviceTypeId = cursor.getInt(
                cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_TYPE_ID));
        String serviceTypeName = cursor.getString(
                cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_TYPE_NAME));
        TextView serviceTypeView =
                (TextView) getActivity().findViewById(R.id.outage_service_type);
        serviceTypeView.setText(serviceTypeName + " (#" + serviceTypeId + ")");
    }

    private class GetDetailsFromServer extends AsyncTask<Void, Void, Outage> {

        protected Outage doInBackground(Void... voids) {
            try {
                return server.outage(outageId);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred while loading info about outage from server", e);
                return null;
            }
        }

        protected void onPostExecute(Outage outage) {
            /** If information is available, updating DB */
            if (outage != null) {
                ContentValues[] values = new ContentValues[1];
                values[0] = ContentValuesGenerator.generate(outage);
                ContentResolver contentResolver = getActivity().getContentResolver();
                contentResolver.bulkInsert(Contract.Outages.CONTENT_URI, values);

                Cursor newCursor = getActivity().getContentResolver().query(
                        Uri.withAppendedPath(Contract.Outages.CONTENT_URI, String.valueOf(outageId)),
                        null, null, null, null);
                updateContent(newCursor);
                newCursor.close();
            } else {
                showErrorMessage();
            }
        }
    }

}