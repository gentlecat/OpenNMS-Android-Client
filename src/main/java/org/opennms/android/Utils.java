package org.opennms.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static final String TAG = "Utils";

    public static Date parseDate(String input) {
        // Example: "2013-07-22T04:03:04-04:00"
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = new Date();
        try {
            date = format.parse(input);
        } catch (ParseException e) {
            Log.e(TAG, "Date parsing error", e);
        }
        return date;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
