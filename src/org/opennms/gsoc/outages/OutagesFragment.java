package org.opennms.gsoc.outages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import org.opennms.gsoc.R;

public class OutagesFragment extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // TODO: Fix: Crashes here.
        return inflater.inflate(R.layout.outages, container, false);
    }

}
