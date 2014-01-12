package org.opennms.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opennms.android.R;
import org.opennms.android.ui.nodes.NodesActivity;

public class TitleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Add is_completed flag. If true, skip title screen and launch base activity.

        getSupportActionBar().hide();
        final Activity thisActivity = this;

        setContentView(R.layout.activity_title);

        Button configure = (Button) findViewById(R.id.configure_button);
        configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Fix: After settings are closed, app is not returning to base activity.
                Intent intent = new Intent(thisActivity, SettingsActivity.class);
                startActivity(intent);
            }
        });

        Button demo = (Button) findViewById(R.id.demo_button);
        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(thisActivity,
                        getString(R.string.title_activity_tip_toast),
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(thisActivity, NodesActivity.class);
                startActivity(intent);
            }
        });
    }

}
