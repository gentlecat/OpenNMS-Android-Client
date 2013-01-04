package org.opennms.android.outages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.android.R;

public class OutageDetailsFragment extends SherlockFragment {

    Outage outage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_outage, container, false);
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

            TextView ip = (TextView) getActivity().findViewById(R.id.outage_ip);
            ip.setText(getString(R.string.outage_details_ip) + " " + outage.getIpAddress());

            TextView ifLostService = (TextView) getActivity().findViewById(R.id.outage_if_lost_service);
            ifLostService.setText(getString(R.string.outage_details_lost) + " " + outage.getIfLostService());

            TextView ifRegainedService = (TextView) getActivity().findViewById(R.id.outage_if_regained_service);
            ifRegainedService.setText(getString(R.string.outage_details_regained) + " " + outage.getIfRegainedService());

            TextView type = (TextView) getActivity().findViewById(R.id.outage_service_type);
            type.setText(getString(R.string.outage_details_type) + " " + outage.getServiceTypeName());
        }
    }

}