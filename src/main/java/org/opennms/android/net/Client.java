package org.opennms.android.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.okhttp.OkAuthenticator;
import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

public class Client {

    private static final String ENCODING = "UTF-8";
    private static final int READ_TIMEOUT_MS = 10000;
    private static final int CONNECT_TIMEOUT_MS = 15000;
    private SharedPreferences settings;
    private OkHttpClient client;

    public Client(Context appContext) {
        client = new OkHttpClient();

        settings = PreferenceManager.getDefaultSharedPreferences(appContext);

        final String user = settings.getString("user", null);
        final String password = settings.getString("password", null);

        client.setAuthenticator(new OkAuthenticator() {
            @Override
            public Credential authenticate(Proxy proxy, URL url, List<Challenge> challenges)
                    throws IOException {
                return Credential.basic(user, password);
            }

            @Override
            public Credential authenticateProxy(Proxy proxy, URL url, List<Challenge> challenges)
                    throws IOException {
                return Credential.basic(user, password);
            }
        });
    }

    public Response get(String path) throws IOException {
        HttpURLConnection connection = client.open(getURL(path));
        connection.setRequestMethod("GET");
        connection.setReadTimeout(READ_TIMEOUT_MS);
        connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        InputStream in = null;
        try {
            in = connection.getInputStream();
            byte[] response = readFully(in);
            return new Response(connection.getResponseCode(), new String(response, ENCODING));
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public Response put(String path) throws IOException {
        HttpURLConnection connection = client.open(getURL(path));
        connection.setRequestMethod("PUT");
        connection.setReadTimeout(READ_TIMEOUT_MS);
        connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        InputStream in = null;
        try {
            in = connection.getInputStream();
            byte[] response = readFully(in);
            return new Response(connection.getResponseCode(), new String(response, ENCODING));
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }

    private int getPort() {
        return Integer.parseInt(settings.getString("port", null));
    }

    private URL getURL(String path) throws MalformedURLException {
        Boolean isHttps = settings.getBoolean("https", false);
        String host = settings.getString("host", null);
        String restUrl = settings.getString("rest_url", null);
        String base = String.format("http%s://%s:%d/" + restUrl,
                (isHttps ? "s" : ""), host, getPort());
        return new URL(base + path);
    }

}