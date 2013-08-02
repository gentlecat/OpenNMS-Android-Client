package org.opennms.android.ui.outages;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.provider.Contract;

public class OutageDetailsFragment extends Fragment {

    private long outageId;

    // Do not remove
    public OutageDetailsFragment() {
    }

    public OutageDetailsFragment(long outageId) {
        this.outageId = outageId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

            String serviceLostTime = cursor.getString(
                    cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_LOST_TIME));
            int serviceLostEventId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_LOST_EVENT_ID));
            TextView lostServiceEvent =
                    (TextView) getActivity().findViewById(R.id.outage_lost_service_event);
            lostServiceEvent.setText(Utils.parseDate(serviceLostTime, "yyyy-MM-dd'T'HH:mm:ssZ")
                                     + "\n#" + serviceLostEventId);

            String serviceRegainedTime = cursor.getString(
                    cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_REGAINED_TIME));
            int serviceRegainedEventId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_REGAINED_EVENT_ID));
            TextView regainedServiceEvent =
                    (TextView) getActivity().findViewById(R.id.outage_regained_service_event);
            if (serviceRegainedTime != null) {
                regainedServiceEvent.setText(Utils.parseDate(serviceRegainedTime,
                                                             "yyyy-MM-dd'T'HH:mm:ssZ") + "\n#"
                                             + serviceRegainedEventId);
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
        cursor.close();
    }

}