package org.opennms.android.communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import com.google.resting.Resting;
import com.google.resting.component.EncodingTypes;
import com.google.resting.component.impl.ServiceResponse;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.opennms.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RestingServerCommunication implements Callable<ServiceResponse> {

    private String url;
    private Context appContext;

    public RestingServerCommunication(String url, Context appContext) {
        this.url = url;
        this.appContext = appContext;
    }

    @Override
    public ServiceResponse call() throws Exception {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);

        String user = settings.getString("user", appContext.getResources().getString(R.string.default_user));
        String password = settings.getString("password", appContext.getResources().getString(R.string.default_password));
        String auth = new String(Base64.encode((user + ":" + password).getBytes(), Base64.URL_SAFE | Base64.NO_WRAP));
        Header httpHeader = new BasicHeader("Authorization", "Basic " + auth);
        List<Header> headers = new ArrayList<Header>();
        headers.add(httpHeader);

        Boolean https = settings.getBoolean("https", appContext.getResources().getBoolean(R.bool.default_https));
        String host = settings.getString("host", appContext.getResources().getString(R.string.default_host));
        String path = settings.getString("path", appContext.getResources().getString(R.string.default_path));
        int port = Integer.parseInt(settings.getString("port", Integer.toString(appContext.getResources().getInteger(R.integer.default_port))));

        String base = String.format("http%s://%s%s/", (https ? "s" : ""), host, path);

        return Resting.get(base + url, port, null, EncodingTypes.UTF8, headers);
    }

}