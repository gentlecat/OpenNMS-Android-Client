package org.opennms.android;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

    private PreferenceManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.settings));
        actionBar.setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.settings);
        prefManager = getPreferenceManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        findPreference("host").setSummary(sharedPref.getString("host", getResources().getString(R.string.default_host)));
        findPreference("port").setSummary(sharedPref.getString("port", Integer.toString(getResources().getInteger(R.integer.default_port))));
        findPreference("path").setSummary(sharedPref.getString("path", getResources().getString(R.string.default_path)));
        findPreference("user").setSummary(sharedPref.getString("user", getResources().getString(R.string.default_user)));
        if (sharedPref.getBoolean("https", getResources().getBoolean(R.bool.default_https))) {
            findPreference("https").setSummary(getResources().getString(R.string.settings_https_on));
        } else {
            findPreference("https").setSummary(getResources().getString(R.string.settings_https_off));
        }

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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
        if (key.equals("host") || key.equals("path") || key.equals("user")) { // String cases
            prefManager.findPreference(key).setSummary(sharedPreferences.getString(key, null));
        } else if (key.equals("port")) { // Integer case
            prefManager.findPreference(key).setSummary(sharedPreferences.getString(key, null));
        } else if (key.equals("https")) { // Boolean case
            if (sharedPreferences.getBoolean(key, false)) {
                prefManager.findPreference(key).setSummary(getResources().getString(R.string.settings_https_on));
            } else {
                prefManager.findPreference(key).setSummary(getResources().getString(R.string.settings_https_off));
            }
        }
    }

}
