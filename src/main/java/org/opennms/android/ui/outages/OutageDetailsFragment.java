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
import org.opennms.android.dao.Outage;
import org.opennms.android.provider.Contract;

public class OutageDetailsFragment extends SherlockFragment {

    private Outage outage;
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
        updateContent();
    }

    public void updateContent() {
        outage = getOutage(outageId);

        if (outage != null) {
            TextView id = (TextView) getActivity().findViewById(R.id.outage_id);
            id.setText(getString(R.string.outage_details_id) + outage.getId());

            TextView ipAddress = (TextView) getActivity().findViewById(R.id.outage_ip_address);
            ipAddress.setText(outage.getIpAddress());

            TextView ipInterfaceId = (TextView) getActivity().findViewById(R.id.outage_ip_interface_id);
            ipInterfaceId.setText(String.valueOf(outage.getIpInterfaceId()));

            TextView lostServiceEvent = (TextView) getActivity().findViewById(R.id.outage_lost_service_event);
            lostServiceEvent.setText(outage.getLostServiceTime() + "\n#" + outage.getServiceLostEventId());

            TextView regainedServiceEvent = (TextView) getActivity().findViewById(R.id.outage_regained_service_event);
            regainedServiceEvent.setText(outage.getRegainedServiceTime() + "\n#" + outage.getServiceRegainedEventId());

            TextView serviceId = (TextView) getActivity().findViewById(R.id.outage_service_id);
            serviceId.setText(String.valueOf(outage.getServiceId()));

            TextView serviceType = (TextView) getActivity().findViewById(R.id.outage_service_type);
            serviceType.setText(outage.getServiceTypeName() + " (#" + outage.getServiceTypeId() + ")");
        }
    }

    private Outage getOutage(long id) {
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(Contract.Outages.CONTENT_URI, String.valueOf(id)),
                null, null, null, null
        );
        if (cursor.moveToFirst()) {
            Outage outage = new Outage(cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages._ID)));
            outage.setIpAddress(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Outages.IP_ADDRESS)));
            outage.setIpInterfaceId(cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.IP_INTERFACE_ID)));
            outage.setServiceId(cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_ID)));
            outage.setServiceTypeId(cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_TYPE_ID)));
            outage.setServiceTypeName(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_TYPE_NAME)));
            outage.setLostServiceTime(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_LOST_TIME)));
            outage.setServiceLostEventId(cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_LOST_EVENT_ID)));
            outage.setRegainedServiceTime(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_REGAINED_TIME)));
            outage.setServiceRegainedEventId(cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Outages.SERVICE_REGAINED_EVENT_ID)));
            cursor.close();
            return outage;
        }
        cursor.close();
        return null;
    }

}