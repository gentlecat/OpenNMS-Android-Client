package org.opennms.android.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.android.R;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.settings));
        actionBar.setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.settings);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        updateSummaries();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummaries();
    }

    private void updateSummaries() {
        // Authentication
        findPreference("user").setSummary(sharedPref.getString("user", getResources().getString(R.string.default_user)));

        // Server
        findPreference("host").setSummary(sharedPref.getString("host", getResources().getString(R.string.default_host)));
        findPreference("port").setSummary(sharedPref.getString("port", Integer.toString(getResources().getInteger(R.integer.default_port))));
        findPreference("path").setSummary(sharedPref.getString("path", getResources().getString(R.string.default_path)));
        if (sharedPref.getBoolean("https", getResources().getBoolean(R.bool.default_https))) {
            findPreference("https").setSummary(getResources().getString(R.string.settings_https_on));
        } else {
            findPreference("https").setSummary(getResources().getString(R.string.settings_https_off));
        }

        // Notifications
        Boolean notificationsOn = sharedPref.getBoolean("notifications_on", getResources().getBoolean(R.bool.default_notifications));
        setNotificationPrefsEnabled(notificationsOn);
        if (notificationsOn) {
            findPreference("notifications_on").setSummary(getResources().getString(R.string.settings_notifications_enabled_true));
        } else {
            findPreference("notifications_on").setSummary(getResources().getString(R.string.settings_notifications_enabled_false));
        }
        String refreshRateSummary = sharedPref.getString("refresh_rate", getResources().getString(R.string.default_refresh_rate))
                + " " + getResources().getString(R.string.settings_refresh_rate_minutes);
        findPreference("refresh_rate").setSummary(refreshRateSummary);
    }

    void setNotificationPrefsEnabled(Boolean enabled) {
        findPreference("wifi_only").setEnabled(enabled);
        findPreference("refresh_rate").setEnabled(enabled);
    }

}
