package org.opennms.android.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import org.opennms.android.R;
import org.opennms.android.provider.DatabaseHelper;
import org.opennms.android.sync.AccountService;
import org.opennms.android.sync.SyncUtils;
import org.opennms.android.ui.alarms.AlarmsListFragment;
import org.opennms.android.ui.nodes.NodesListFragment;
import org.opennms.android.ui.outages.OutagesListFragment;

public class SettingsActivity extends PreferenceActivity
        implements OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPref;
    private String oldHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setTitle(R.string.settings);
    }

    @Override
    protected void onResume() {
        super.onResume();

        oldHost = sharedPref.getString("host", String.valueOf(getString(R.string.default_host)));

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        updateSummaries();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_apply_settings:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummaries();
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean sync = sharedPref.getBoolean(
                "notifications_on", getResources().getBoolean(R.bool.default_notifications));
        String syncRate = sharedPref.getString(
                "sync_rate",
                String.valueOf(getResources().getInteger(R.integer.default_sync_rate_minutes)));
        int frequency = Integer.parseInt(syncRate) * 60;
        SyncUtils.setSyncAlarmsPeriodically(sync, AccountService.getAccount(), frequency);

        String newHost = sharedPref.getString(
                "host", String.valueOf(getString(R.string.default_host)));
        if (!newHost.equals(oldHost)) {
            new DatabaseHelper(getApplicationContext()).wipe();

            /** Resetting information about active fragments with details */
            sharedPref.edit().putLong(NodesListFragment.STATE_ACTIVE_NODE_ID, -1).commit();
            sharedPref.edit().putLong(AlarmsListFragment.STATE_ACTIVE_ALARM_ID, -1).commit();
            sharedPref.edit().putLong(OutagesListFragment.STATE_ACTIVE_OUTAGE_ID, -1).commit();
        }
    }

    private void updateSummaries() {
        // Authentication
        findPreference("user").setSummary(
                sharedPref.getString("user", getResources().getString(R.string.default_user)));

        // Server
        findPreference("host").setSummary(
                sharedPref.getString("host", getResources().getString(R.string.default_host)));
        findPreference("port").setSummary(sharedPref.getString(
                "port", Integer.toString(getResources().getInteger(R.integer.default_port))));
        if (sharedPref.getBoolean("https", getResources().getBoolean(R.bool.default_https))) {
            findPreference("https")
                    .setSummary(getResources().getString(R.string.settings_https_on));
        } else {
            findPreference("https")
                    .setSummary(getResources().getString(R.string.settings_https_off));
        }
        findPreference("rest_url").setSummary(sharedPref.getString("rest_url", getResources()
                .getString(R.string.default_rest_url)));

        // Notifications
        boolean notificationsOn = sharedPref.getBoolean(
                "notifications_on", getResources().getBoolean(R.bool.default_notifications));
        setNotificationPrefsEnabled(notificationsOn);
        if (notificationsOn) {
            findPreference("notifications_on").setSummary(
                    getResources().getString(R.string.settings_notifications_enabled_true));
        } else {
            findPreference("notifications_on").setSummary(
                    getResources().getString(R.string.settings_notifications_enabled_false));
        }

        String minimalSeverity = sharedPref.getString("minimal_severity",
                                                      getString(R.string.default_minimal_severity));
        ListPreference minimalSeverityPreference =
                (ListPreference) findPreference("minimal_severity");
        int index = minimalSeverityPreference.findIndexOfValue(minimalSeverity);
        minimalSeverityPreference.setSummary(minimalSeverityPreference.getEntries()[index]);

        String syncRate = sharedPref.getString(
                "sync_rate",
                String.valueOf(getResources().getInteger(R.integer.default_sync_rate_minutes)));
        int refreshRateVal = Integer.parseInt(syncRate);
        String syncRateSummary = syncRate + " ";
        if (refreshRateVal == 1) {
            syncRateSummary += getString(R.string.settings_sync_rate_minutes_singular);
        } else {
            syncRateSummary += getString(R.string.settings_sync_rate_minutes_plural);
        }
        findPreference("sync_rate").setSummary(syncRateSummary);
    }

    void setNotificationPrefsEnabled(Boolean enabled) {
        findPreference("wifi_only").setEnabled(enabled);
        findPreference("minimal_severity").setEnabled(enabled);
        findPreference("sync_rate").setEnabled(enabled);
    }

}
