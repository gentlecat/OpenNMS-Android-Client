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

import org.opennms.android.App;
import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.data.api.ServerInterface;
import org.opennms.android.data.api.model.User;
import org.opennms.android.data.storage.DatabaseHelper;
import org.opennms.android.data.sync.AccountService;
import org.opennms.android.data.sync.SyncUtils;
import org.opennms.android.settings.ConnectionSettings;
import org.opennms.android.settings.NotificationSettings;
import org.opennms.android.ui.alarms.AlarmsListFragment;
import org.opennms.android.ui.nodes.NodesActivity;
import org.opennms.android.ui.nodes.NodesListFragment;
import org.opennms.android.ui.outages.OutagesListFragment;

import javax.inject.Inject;

public class SettingsActivity extends PreferenceActivity
    implements OnSharedPreferenceChangeListener {

  public static final String TAG = "SettingsActivity";
  private SharedPreferences sharedPref;
  private ServerCheckTask checkTask;
  private Context context;
  private Settings oldSettings;
  @Inject ServerInterface server;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    App.get(context).inject(this);
    addPreferencesFromResource(R.xml.settings);
    sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

    setTitle(R.string.settings);

    oldSettings = (Settings) getLastNonConfigurationInstance();
    if (oldSettings == null) {
      oldSettings = getCurrentSettings();
    }
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
  protected void onStop() {
    super.onStop();
    if (checkTask != null) {
      checkTask.cancel(true);
    }
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    return oldSettings;
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
    if (checkTask != null && checkTask.getStatus() == AsyncTask.Status.RUNNING) {
      return;
    }
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
    findPreference(ConnectionSettings.KEY_PORT)
        .setSummary(String.valueOf(ConnectionSettings.port(context)));
    if (ConnectionSettings.isHttps(context)) {
      findPreference(ConnectionSettings.KEY_HTTPS)
          .setSummary(getString(R.string.settings_https_on));
    } else {
      findPreference(ConnectionSettings.KEY_HTTPS)
          .setSummary(getString(R.string.settings_https_off));
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
    ListPreference minimalSeverityPreference =
        (ListPreference) findPreference(NotificationSettings.KEY_MINIMAL_SEVERITY);
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
    builder.setPositiveButton(
        getString(R.string.settings_dialog_apply),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            sharedPref.edit().putBoolean(TitleActivity.STATE_TITLE_PASSED, true).commit();

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
            finish();
          }
        });
    builder.setNegativeButton(
        getString(R.string.settings_dialog_cancel),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });

    builder.create().show();
  }

  void showDiscardDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.settings_discard_message));
    builder.setPositiveButton(
        getString(R.string.settings_dialog_discard),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            restoreSettings(oldSettings, sharedPref);
            finish();
          }
        });
    builder.setNegativeButton(
        getString(R.string.settings_dialog_cancel),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });
    builder.create().show();
  }

  /**
   * Settings class is used to store settings to allow restoration in case user decided to reverse
   * changes.
   */
  public class Settings {

    // Connection
    public String host;
    public int port;
    public boolean isHttps;
    public String restUrl;
    public String user;
    public String password;

    // Notifications and sync
    public boolean notificationsOn;
    public String minSeverity;
    public int syncRate;
    public boolean isWifiOnly;
  }

  Settings getCurrentSettings() {
    Settings config = new Settings();

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

  void restoreSettings(Settings settings, SharedPreferences sharedPref) {
    SharedPreferences.Editor editor = sharedPref.edit();

    // Connection
    editor.putString(ConnectionSettings.KEY_HOST, settings.host);
    editor.putString(ConnectionSettings.KEY_PORT, String.valueOf(settings.port));
    editor.putBoolean(ConnectionSettings.KEY_HTTPS, settings.isHttps);
    editor.putString(ConnectionSettings.KEY_REST_URL, settings.restUrl);
    editor.putString(ConnectionSettings.KEY_USER, settings.user);
    editor.putString(ConnectionSettings.KEY_PASSWORD, settings.password);

    // Notifications and sync
    editor.putBoolean(NotificationSettings.KEY_NOTIFICATIONS_ENABLED, settings.notificationsOn);
    editor.putString(NotificationSettings.KEY_MINIMAL_SEVERITY, settings.minSeverity);
    editor.putString(NotificationSettings.KEY_SYNC_RATE_MINUTES, String.valueOf(settings.syncRate));
    editor.putBoolean(NotificationSettings.KEY_SYNC_WIFI_ONLY, settings.isWifiOnly);

    editor.commit();
  }

  private class ServerCheckTask extends AsyncTask<Void, Void, User> {

    protected User doInBackground(Void... voids) {
      String user = ConnectionSettings.user(context);
      try {
        return server.user(user);
      } catch (Exception e) {
        // TODO: Provide more information to user
        Log.e(TAG, "Error occurred while testing connection to server!", e);
        return null;
      }
    }

    protected void onPostExecute(User user) {
      // TODO: Replace previous toast if it is still displayed
      if (user != null) {
        Toast.makeText(getApplicationContext(),
                       R.string.server_check_ok, Toast.LENGTH_LONG).show();
      } else {
        Toast.makeText(getApplicationContext(),
                       R.string.server_check_not_ok, Toast.LENGTH_LONG).show();
      }
    }
  }

}
