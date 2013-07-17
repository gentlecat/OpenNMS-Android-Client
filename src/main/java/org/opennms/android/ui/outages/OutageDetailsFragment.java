package org.opennms.android.ui.outages;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.android.R;
import org.opennms.android.provider.Contract;

public class OutageDetailsFragment extends SherlockFragment {

    private long outageId;

    // Do not remove
    public OutageDetailsFragment() {
    }

    public OutageDetailsFragment(long outageId) {
        this.outageId = outageId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.outage_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateContent(outageId);
    }

    public void updateContent(long outageId) {
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(Contract.Outages.CONTENT_URI, String.valueOf(outageId)),
                null, null, null, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages._ID));
            TextView idView = (TextView) getActivity().findViewById(R.id.outage_id);
            idView.setText(getString(R.string.outage_details_id) + id);

            String ipAddress = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Outages.IP_ADDRESS));
            TextView ipAddressView = (TextView) getActivity().findViewById(R.id.outage_ip_address);
            ipAddressView.setText(ipAddress);

            int ipInterfaceId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.IP_INTERFACE_ID));
            TextView ipInterfaceIdView = (TextView) getActivity().findViewById(R.id.outage_ip_interface_id);
            ipInterfaceIdView.setText(String.valueOf(ipInterfaceId));

            String serviceLostTime = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_LOST_TIME));
            int serviceLostEventId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_LOST_EVENT_ID));
            TextView lostServiceEvent = (TextView) getActivity().findViewById(R.id.outage_lost_service_event);
            lostServiceEvent.setText(serviceLostTime + "\n#" + serviceLostEventId);

            String serviceRegainedTime = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_REGAINED_TIME));
            int serviceRegainedEventId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_REGAINED_EVENT_ID));
            TextView regainedServiceEvent = (TextView) getActivity().findViewById(R.id.outage_regained_service_event);
            regainedServiceEvent.setText(serviceRegainedTime + "\n#" + serviceRegainedEventId);

            int serviceId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_ID));
            TextView serviceIdView = (TextView) getActivity().findViewById(R.id.outage_service_id);
            serviceIdView.setText(String.valueOf(serviceId));

            int serviceTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_TYPE_ID));
            String serviceTypeName = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_TYPE_NAME));
            TextView serviceTypeView = (TextView) getActivity().findViewById(R.id.outage_service_type);
            serviceTypeView.setText(serviceTypeName + " (#" + serviceTypeId + ")");
        }
        cursor.close();
    }

}