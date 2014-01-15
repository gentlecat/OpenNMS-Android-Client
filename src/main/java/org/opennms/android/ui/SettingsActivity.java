package org.opennms.android.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.net.DataLoader;
import org.opennms.android.net.Response;
import org.opennms.android.provider.DatabaseHelper;
import org.opennms.android.settings.Configuration;
import org.opennms.android.settings.ConnectionSettings;
import org.opennms.android.settings.NotificationSettings;
import org.opennms.android.sync.AccountService;
import org.opennms.android.sync.SyncUtils;
import org.opennms.android.ui.alarms.AlarmsListFragment;
import org.opennms.android.ui.nodes.NodesActivity;
import org.opennms.android.ui.nodes.NodesListFragment;
import org.opennms.android.ui.outages.OutagesListFragment;

import java.net.HttpURLConnection;

public class SettingsActivity extends PreferenceActivity
        implements OnSharedPreferenceChangeListener {

    public static final String TAG = "SettingsActivity";
    public static final String STATE_OLD_SETTINGS = "old_settings";
    private SharedPreferences sharedPref;
    private ServerCheckTask checkTask;
    private Context context;
    private Configuration oldSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        context = this;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (savedInstanceState != null) {
            oldSettings = savedInstanceState.getParcelable(STATE_OLD_SETTINGS);
        } else {
            oldSettings = getCurrentConfiguration();
        }

        setTitle(R.string.settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        updateSummaries();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_OLD_SETTINGS, oldSettings);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (checkTask != null) checkTask.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check_settings:
                checkServer();
                return true;
            case R.id.menu_apply_settings:
                showApplyDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        showDiscardDialog();
    }

    private void checkServer() {
        if (checkTask != null && checkTask.getStatus() == AsyncTask.Status.RUNNING) return;
        if (Utils.isOnline(context)) {
            Toast.makeText(context, R.string.server_check_wait, Toast.LENGTH_LONG).show();
            checkTask = new ServerCheckTask();
            checkTask.execute();
        } else {
            Toast.makeText(context, R.string.server_check_offline, Toast.LENGTH_LONG).show();
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummaries();
    }


    private void updateSummaries() {
        // Server
        findPreference(ConnectionSettings.KEY_HOST).setSummary(ConnectionSettings.host(context));
        findPreference(ConnectionSettings.KEY_PORT).setSummary(String.valueOf(ConnectionSettings.port(context)));
        if (ConnectionSettings.isHttps(context)) {
            findPreference(ConnectionSettings.KEY_HTTPS).setSummary(getString(R.string.settings_https_on));
        } else {
            findPreference(ConnectionSettings.KEY_HTTPS).setSummary(getString(R.string.settings_https_off));
        }
        findPreference(ConnectionSettings.KEY_REST_URL).setSummary(ConnectionSettings.restUrl(context));

        // Authentication
        findPreference(ConnectionSettings.KEY_USER).setSummary(ConnectionSettings.user(context));

        // Notifications
        boolean notificationsOn = NotificationSettings.enabled(context);
        setNotificationPrefsEnabled(notificationsOn);
        if (notificationsOn) {
            findPreference(NotificationSettings.KEY_NOTIFICATIONS_ENABLED)
                    .setSummary(getString(R.string.settings_notifications_enabled_true));
        } else {
            findPreference(NotificationSettings.KEY_NOTIFICATIONS_ENABLED)
                    .setSummary(getString(R.string.settings_notifications_enabled_false));
        }

        String minimalSeverity = NotificationSettings.minSeverity(context);
        ListPreference minimalSeverityPreference = (ListPreference) findPreference(NotificationSettings.KEY_MINIMAL_SEVERITY);
        int index = minimalSeverityPreference.findIndexOfValue(minimalSeverity);
        minimalSeverityPreference.setSummary(minimalSeverityPreference.getEntries()[index]);

        String syncRate = String.valueOf(NotificationSettings.syncRateMinutes(context));
        int refreshRateVal = Integer.parseInt(syncRate);
        String syncRateSummary = syncRate + " ";
        if (refreshRateVal == 1) {
            syncRateSummary += getString(R.string.settings_sync_rate_minutes_singular);
        } else {
            syncRateSummary += getString(R.string.settings_sync_rate_minutes_plural);
        }
        findPreference(NotificationSettings.KEY_SYNC_RATE_MINUTES).setSummary(syncRateSummary);
    }

    void setNotificationPrefsEnabled(Boolean enabled) {
        findPreference(NotificationSettings.KEY_SYNC_WIFI_ONLY).setEnabled(enabled);
        findPreference(NotificationSettings.KEY_MINIMAL_SEVERITY).setEnabled(enabled);
        findPreference(NotificationSettings.KEY_SYNC_RATE_MINUTES).setEnabled(enabled);
    }

    void showApplyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.settings_apply_message));
        builder.setPositiveButton(getString(R.string.settings_dialog_apply), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // TODO: Mark as configured (TitleActivity will not be shown anymore)

                boolean sync = NotificationSettings.enabled(context);
                int frequency = NotificationSettings.syncRateMinutes(context) * 60;
                SyncUtils.setSyncAlarmsPeriodically(sync, AccountService.getAccount(), frequency);

                new DatabaseHelper(getApplicationContext()).wipe();
                /** Resetting information about active fragments with details */
                sharedPref.edit().putLong(NodesListFragment.STATE_ACTIVE_NODE_ID, -1).commit();
                sharedPref.edit().putLong(AlarmsListFragment.STATE_ACTIVE_ALARM_ID, -1).commit();
                sharedPref.edit().putLong(OutagesListFragment.STATE_ACTIVE_OUTAGE_ID, -1).commit();

                sendBroadcast(new Intent(TitleActivity.ACTION_FINISH));
                Intent intent = new Intent(context, NodesActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getString(R.string.settings_dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    void showDiscardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.settings_discard_message));
        builder.setPositiveButton(getString(R.string.settings_dialog_discard), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                restoreConfiguration(oldSettings, sharedPref);
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.settings_dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    Configuration getCurrentConfiguration() {
        Configuration config = new Configuration();

        // Connection
        config.host = ConnectionSettings.host(context);
        config.port = ConnectionSettings.port(context);
        config.isHttps = ConnectionSettings.isHttps(context);
        config.restUrl = ConnectionSettings.restUrl(context);
        config.user = ConnectionSettings.user(context);
        config.password = ConnectionSettings.password(context);

        // Notifications and sync
        config.notificationsOn = NotificationSettings.enabled(context);
        config.minSeverity = NotificationSettings.minSeverity(context);
        config.syncRate = NotificationSettings.syncRateMinutes(context);
        config.isWifiOnly = NotificationSettings.wifiOnly(context);

        return config;
    }

    void restoreConfiguration(Configuration config, SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();

        // Connection
        editor.putString(ConnectionSettings.KEY_HOST, config.host);
        editor.putString(ConnectionSettings.KEY_PORT, String.valueOf(config.port));
        editor.putBoolean(ConnectionSettings.KEY_HTTPS, config.isHttps);
        editor.putString(ConnectionSettings.KEY_REST_URL, config.restUrl);
        editor.putString(ConnectionSettings.KEY_USER, config.user);
        editor.putString(ConnectionSettings.KEY_PASSWORD, config.password);

        // Notifications and sync
        editor.putBoolean(NotificationSettings.KEY_NOTIFICATIONS_ENABLED, config.notificationsOn);
        editor.putString(NotificationSettings.KEY_MINIMAL_SEVERITY, config.minSeverity);
        editor.putString(NotificationSettings.KEY_SYNC_RATE_MINUTES, String.valueOf(config.syncRate));
        editor.putBoolean(NotificationSettings.KEY_SYNC_WIFI_ONLY, config.isWifiOnly);

        editor.commit();
    }

    private class ServerCheckTask extends AsyncTask<Void, Void, Response> {

        protected Response doInBackground(Void... voids) {
            String user = ConnectionSettings.user(context);
            try {
                return new DataLoader(getApplicationContext()).user(user);
            } catch (Exception e) {
                // TODO: Provide more information to user
                Log.e(TAG, "Error occurred while testing connection to server!", e);
                return null;
            }
        }

        protected void onPostExecute(Response response) {
            // TODO: Replace previous toast if it is still displayed
            if (response != null) {
                if (response.getCode() == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(getApplicationContext(), R.string.server_check_ok, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.server_check_not_ok, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.server_check_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

}
