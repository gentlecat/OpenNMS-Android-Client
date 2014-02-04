package org.opennms.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import org.opennms.android.App;
import org.opennms.android.R;
import org.opennms.android.data.api.ServerInterface;

import javax.inject.Inject;

public abstract class DetailsFragment extends Fragment {
    @Inject
    protected ServerInterface server;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = App.get(getActivity());
        app.inject(this);
    }

    protected void showErrorMessage() {
        if (!isAdded()) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                RelativeLayout detailsContainer =
                        (RelativeLayout) getActivity().findViewById(R.id.details_container);
                if (detailsContainer == null) {
                    return;
                }
                detailsContainer.removeAllViews();
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.details_error, detailsContainer);
            }
        });
    }

}