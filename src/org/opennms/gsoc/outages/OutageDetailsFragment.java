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

    private View viewer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewer = inflater.inflate(R.layout.outage_view, container, false);
        viewer.setFocusableInTouchMode(true);
        viewer.requestFocus();
        return viewer;
    }

    public void updateUrl(Outage newOutage) {
        if (this.viewer != null) {
            TextView idTextView = (TextView) viewer.findViewById(R.id.outageView);
            idTextView.setText(printOutage(newOutage));
        }
    }

    private String printOutage(Outage newOutage) {
        StringBuilder builder = new StringBuilder();
        builder.append("Outage ID: " + newOutage.getId() + "\n");
        builder.append("IP address: " + newOutage.getIpAddress() + "\n");
        builder.append("If lost service: " + newOutage.getIfLostService() + "\n");
        builder.append("If regained service: " + newOutage.getIfRegainedService() + "\n");
        builder.append("Service type name: " + newOutage.getServiceTypeName() + "\n");
        return builder.toString();
    }

}
