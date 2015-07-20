package org.opennms.android.settings;

import android.content.Context;
import android.preference.PreferenceManager;

import org.opennms.android.R;

/**
 * ConnectionSettings provides an interface to settings for connection to OpenNMS server.
 */
public class NotificationSettings {

    public static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String KEY_MINIMAL_SEVERITY = "minimal_notification_severity";
    public static final String KEY_SYNC_RATE_MINUTES = "sync_rate";
    public static final String KEY_SYNC_WIFI_ONLY = "sync_wifi_only";

    public static boolean enabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                KEY_NOTIFICATIONS_ENABLED, context.getResources().getBoolean(R.bool.default_notifications));
    }

    public static String minSeverity(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_MINIMAL_SEVERITY,
                        context.getResources().getString(R.string.default_minimal_severity));
    }

    public static int syncRateMinutes(Context context) {
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(
                KEY_SYNC_RATE_MINUTES,
                String.valueOf(context.getResources().getInteger(R.integer.default_sync_rate_minutes))));
    }

    public static boolean wifiOnly(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_SYNC_WIFI_ONLY, context.getResources().getBoolean(R.bool.wifi_only));
    }
}
