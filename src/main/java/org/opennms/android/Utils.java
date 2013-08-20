package org.opennms.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils {

    private static final String TAG = "Utils";

    public static String parseDate(String input, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(input).toString();
        } catch (ParseException e) {
            Log.e(TAG, "Date parsing error", e);
            return input;
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static int getActionBarHeight(Context context) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(
                    typedValue.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }

}
