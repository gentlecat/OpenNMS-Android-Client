package org.opennms.android;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils {

    private static final String TAG = "Utils";

    /**
     * Makes input date a bit prettier.
     *
     * @param input   Original date.
     * @param pattern Pattern of input.
     * @return String with prettier date or original, if error occurred during reformatting process.
     */
    public static String reformatDate(String input, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(input).toString();
        } catch (ParseException e) {
            Log.e(TAG, "Date parsing error", e);
            return input;
        }
    }

    /**
     * Check if device is connected to network.
     *
     * @param context Application context.
     * @return {@code true} if connected to network, {@code false} if not.
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
