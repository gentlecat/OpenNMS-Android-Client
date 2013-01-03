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
        TextView details = (TextView) getSherlockActivity().findViewById(R.id.outage_details_text);
        if (details != null && outage != null) details.setText(outage.toString());
    }

}