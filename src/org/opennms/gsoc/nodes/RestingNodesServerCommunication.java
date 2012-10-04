package org.opennms.gsoc.nodes;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.opennms.gsoc.R;

import android.util.Base64;
import android.util.Log;

import com.google.resting.Resting;
import com.google.resting.component.EncodingTypes;
import com.google.resting.component.impl.ServiceResponse;

public class RestingNodesServerCommunication implements Callable<ServiceResponse> {

    private Context appContext;

    public RestingNodesServerCommunication(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public ServiceResponse call() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);

        String user = settings.getString("user", appContext.getResources().getString(R.string.default_user));
        String password = settings.getString("password", appContext.getResources().getString(R.string.default_password));

        String auth = new String(Base64.encode((user + ":" + password).getBytes(), Base64.URL_SAFE | Base64.NO_WRAP));
        Header httpHeader = new BasicHeader("Authorization", "Basic " + auth);
        List<Header> headers = new ArrayList<Header>();
        headers.add(httpHeader);
        ServiceResponse response = null;

        Boolean https = settings.getBoolean("https", appContext.getResources().getBoolean(R.bool.default_https));
        String host = settings.getString("host", appContext.getResources().getString(R.string.default_host));
        String path = settings.getString("path", appContext.getResources().getString(R.string.default_path));

        String base = String.format("http%s://%s%s", (https ? "s" :""), host, path);

        try {
            InetAddress.getByName(host);
            response = Resting.get(base + "/nodes", 80, null, EncodingTypes.UTF8, headers);
        } catch(Exception e) {
            Log.i("resting", e.getMessage());
        } finally {
            return response;
        }
    }
}
