package org.opennms.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.opennms.android.R;
import org.opennms.android.ui.nodes.NodesActivity;

public class TitleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Add is_completed flag. If true, skip title screen and launch base activity.

        setContentView(R.layout.activity_title);
        getSupportActionBar().hide();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new TitleFragment()).commit();
        }
    }

    public static class TitleFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_title, container, false);

            Button configure = (Button) rootView.findViewById(R.id.configure_button);
            configure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Fix: After settings are closed, app is not returning to base activity.
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                }
            });

            Button demo = (Button) rootView.findViewById(R.id.demo_button);
            demo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),
                            getString(R.string.title_activity_tip_toast),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), NodesActivity.class);
                    startActivity(intent);
                }
            });

            return rootView;
        }
    }

}
