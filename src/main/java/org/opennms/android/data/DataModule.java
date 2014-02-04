package org.opennms.android.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkAuthenticator;
import com.squareup.okhttp.OkHttpClient;

import org.opennms.android.data.api.ApiModule;
import org.opennms.android.settings.ConnectionSettings;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = ApiModule.class,
        complete = false,
        library = true
)
public final class DataModule {
    static final String TAG = "DataModule";
    static final int DISK_CACHE_SIZE = 10 * 1024 * 1024; // 10MB

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();

        final String user = ConnectionSettings.user(app);
        final String password = ConnectionSettings.password(app);

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

        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(app.getCacheDir(), "http");
            HttpResponseCache cache = new HttpResponseCache(cacheDir, DISK_CACHE_SIZE);
            client.setResponseCache(cache);
        } catch (IOException e) {
            Log.e(TAG, "Unable to install disk cache.", e);
        }

        return client;
    }
}
