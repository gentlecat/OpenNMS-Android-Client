package org.opennms.android.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.opennms.android.R;
import org.opennms.android.service.AlarmReceiver;

import java.util.Calendar;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_settings:
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

    @Override
    protected void onStop() {
        super.onStop();
        Context context = getApplicationContext();
        Intent intent = new Intent(this, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (sharedPref.getBoolean("notifications_on", getResources().getBoolean(R.bool.default_notifications))) {
            setRecurringAlarm(context, alarmManager, intent);
        } else {
            cancelRecurringAlarm(context, alarmManager, intent);
        }
    }

    private void updateSummaries() {
        // Authentication
        findPreference("user").setSummary(sharedPref.getString("user", getResources().getString(R.string.default_user)));

        // Server
        findPreference("host").setSummary(sharedPref.getString("host", getResources().getString(R.string.default_host)));
        findPreference("port").setSummary(sharedPref.getString("port",
                Integer.toString(getResources().getInteger(R.integer.default_port))));
        findPreference("path").setSummary(sharedPref.getString("path", getResources().getString(R.string.default_path)));
        if (sharedPref.getBoolean("https", getResources().getBoolean(R.bool.default_https))) {
            findPreference("https").setSummary(getResources().getString(R.string.settings_https_on));
        } else {
            findPreference("https").setSummary(getResources().getString(R.string.settings_https_off));
        }

        // Notifications
        boolean notificationsOn = sharedPref.getBoolean("notifications_on",
                getResources().getBoolean(R.bool.default_notifications));
        setNotificationPrefsEnabled(notificationsOn);
        if (notificationsOn) {
            findPreference("notifications_on").setSummary(getResources().getString(R.string.settings_notifications_enabled_true));
        } else {
            findPreference("notifications_on").setSummary(getResources().getString(R.string.settings_notifications_enabled_false));
        }

        String minimalSeverity = sharedPref.getString("minimal_severity", getString(R.string.default_minimal_severity));
        ListPreference minimalSeverityPreference = (ListPreference) findPreference("minimal_severity");
        int index = minimalSeverityPreference.findIndexOfValue(minimalSeverity);
        minimalSeverityPreference.setSummary(minimalSeverityPreference.getEntries()[index]);

        String refreshRate = sharedPref.getString("refresh_rate",
                String.valueOf(getResources().getInteger(R.integer.default_refresh_rate)));
        int refreshRateVal = Integer.parseInt(refreshRate);
        String refreshRateSummary = refreshRate + " ";
        if (refreshRateVal == 1) {
            refreshRateSummary += getString(R.string.settings_refresh_rate_minutes_singular);
        } else {
            refreshRateSummary += getString(R.string.settings_refresh_rate_minutes_plural);
        }
        findPreference("refresh_rate").setSummary(refreshRateSummary);
    }

    void setNotificationPrefsEnabled(Boolean enabled) {
        findPreference("wifi_only").setEnabled(enabled);
        findPreference("minimal_severity").setEnabled(enabled);
        findPreference("refresh_rate").setEnabled(enabled);
    }

    private void setRecurringAlarm(Context context, AlarmManager alarmManager, Intent alarmRecieverIntent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmRecieverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        int interval = Integer.parseInt(sharedPref.getString("refresh_rate",
                String.valueOf(getResources().getInteger(R.integer.default_refresh_rate))));
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis(),
                interval * DateUtils.MINUTE_IN_MILLIS,
                pendingIntent
        );
    }

    private void cancelRecurringAlarm(Context context, AlarmManager alarmManager, Intent alarmRecieverIntent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmRecieverIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

}
