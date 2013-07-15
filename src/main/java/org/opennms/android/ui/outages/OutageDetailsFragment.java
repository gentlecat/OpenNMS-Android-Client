package org.opennms.android.ui.outages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.android.R;
import org.opennms.android.dao.Outage;

public class OutageDetailsFragment extends SherlockFragment {

    Outage outage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.outage_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateContent();
    }

    public void bindOutage(Outage outage) {
        this.outage = outage;
        if (this.isVisible()) updateContent();
    }

    public void updateContent() {
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

}