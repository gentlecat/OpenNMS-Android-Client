package org.opennms.android.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opennms.android.R;
import org.opennms.android.ui.nodes.NodesActivity;

public class TitleActivity extends ActionBarActivity {
    private FinishReceiver finishReceiver;
    public static final String ACTION_FINISH = "org.opennms.android.ui.ACTION_FINISH";
    public static final String STATE_TITLE_PASSED = "title_passed";
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean(STATE_TITLE_PASSED, false)) {
            Intent intent = new Intent(this, NodesActivity.class);
            startActivity(intent);
            finish();
        }

        getSupportActionBar().hide();
        final Activity thisActivity = this;

        setContentView(R.layout.activity_title);

        finishReceiver = new FinishReceiver();
        registerReceiver(finishReceiver, new IntentFilter(ACTION_FINISH));

        Button configure = (Button) findViewById(R.id.configure_button);
        configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, SettingsActivity.class);
                startActivity(intent);
            }
        });

        Button demo = (Button) findViewById(R.id.demo_button);
        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().putBoolean(STATE_TITLE_PASSED, true).commit();
                Toast.makeText(thisActivity,
                        getString(R.string.title_activity_tip_toast),
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(thisActivity, NodesActivity.class);
                startActivity(intent);

                thisActivity.finish();
                thisActivity.overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishReceiver);
    }

    private final class FinishReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(ACTION_FINISH))
                finish();
        }
    }

}
