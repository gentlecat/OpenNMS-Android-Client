package org.opennms.android.settings;

import android.content.Context;
import android.preference.PreferenceManager;

import org.opennms.android.R;

/**
 * ConnectionSettings provides an interface to settings for connection to OpenNMS server.
 */
public class ConnectionSettings {
    // Server
    public static final String KEY_HOST = "connection_host";
    public static final String KEY_PORT = "connection_port";
    public static final String KEY_HTTPS = "connection_https";
    public static final String KEY_REST_URL = "connection_rest_url";
    // Auth
    public static final String KEY_USER = "connection_user";
    public static final String KEY_PASSWORD = "connection_password";

    public static String host(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_HOST,
                context.getResources().getString(R.string.default_host));
    }

    public static int port(Context context) {
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_PORT,
                String.valueOf(context.getResources().getInteger(R.integer.default_port))));
    }

    public static boolean isHttps(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_HTTPS,
                context.getResources().getBoolean(R.bool.default_https));
    }

    public static String restUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_REST_URL,
                context.getResources().getString(R.string.default_rest_url));
    }

    public static String user(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_USER,
                context.getResources().getString(R.string.default_user));
    }

    public static String password(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_PASSWORD,
                context.getResources().getString(R.string.default_password));
    }
}
