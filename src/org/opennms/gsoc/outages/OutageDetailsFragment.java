package org.opennms.gsoc.outages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.gsoc.R;
import org.opennms.gsoc.model.Outage;

public class OutageDetailsFragment extends SherlockFragment {

    private View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.outage_details, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        return view;
    }

    public void show(Outage newOutage) {
        if (view != null) {
            TextView idTextView = (TextView) view.findViewById(R.id.outage_info);
            idTextView.setText(printOutageInfo(newOutage));
        }
    }

    private String printOutageInfo(Outage newOutage) {
        StringBuilder builder = new StringBuilder();
        builder.append("Outage ID: " + newOutage.getId() + "\n");
        builder.append("IP address: " + newOutage.getIpAddress() + "\n");
        builder.append("If lost service: " + newOutage.getIfLostService() + "\n");
        builder.append("If regained service: " + newOutage.getIfRegainedService() + "\n");
        builder.append("Service type name: " + newOutage.getServiceTypeName() + "\n");
        return builder.toString();
    }

}
