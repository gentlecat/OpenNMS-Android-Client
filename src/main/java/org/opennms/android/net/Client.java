package org.opennms.android.net;

import android.content.Context;

import com.squareup.okhttp.OkAuthenticator;
import com.squareup.okhttp.OkHttpClient;

import org.opennms.android.settings.ConnectionSettings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

/**
 * HTTP client that works with OpenNMS server specified in {@link android.content.SharedPreferences}.
 */
public class Client {

    private static final String ENCODING = "UTF-8";
    private static final int READ_TIMEOUT_MS = 10000;
    private static final int CONNECT_TIMEOUT_MS = 15000;
    private Context context;
    private OkHttpClient client;

    /**
     * @param context Application context used to get connection information from settings.
     */
    public Client(Context context) {
        this.context = context;
        client = new OkHttpClient();

        final String user = ConnectionSettings.user(this.context);
        final String password = ConnectionSettings.password(this.context);

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

    /**
     * Make GET request to OpenNMS server.
     *
     * @param path REST API path.
     * @return {@link org.opennms.android.net.Response} object.
     * @throws IOException
     */
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

    /**
     * Make PUT request to OpenNMS server.
     *
     * @param path REST API path.
     * @return {@link org.opennms.android.net.Response} object.
     * @throws IOException
     */
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

    /**
     * @return port that is used to connect to OpenNMS server.
     */
    private int getPort() {
        return ConnectionSettings.port(context);
    }

    /**
     * Creates complete URL that is used in requests by appending path to base URL for REST calls
     * specified in settings.
     *
     * @param path Path after base URL.
     * @return {@link java.net.URL}
     * @throws MalformedURLException
     */
    private URL getURL(String path) throws MalformedURLException {
        boolean isHttps = ConnectionSettings.isHttps(context);
        String host = ConnectionSettings.host(context);
        String restUrl = ConnectionSettings.restUrl(context);
        String base = String.format("http%s://%s:%d/" + restUrl,
                (isHttps ? "s" : ""), host, getPort());
        return new URL(base + path);
    }

}