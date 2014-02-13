package org.opennms.android.data.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Application;

import com.squareup.okhttp.OkHttpClient;

import org.joda.time.DateTime;
import org.opennms.android.settings.ConnectionSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.Server;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module(
    complete = false,
    library = true
)
public final class ApiModule {

  @Provides
  @Singleton
  Server provideServer(Application app) {
    boolean isHttps = ConnectionSettings.isHttps(app);
    String host = ConnectionSettings.host(app);
    String restUrl = ConnectionSettings.restUrl(app);
    String url = String.format("http%s://%s:%d/" + restUrl,
                               (isHttps ? "s" : ""), host, ConnectionSettings.port(app));
    return new Server(url);
  }

  @Provides
  @Singleton
  Client provideClient(OkHttpClient client) {
    return new OkClient(client);
  }

  @Provides
  @Singleton
  RestAdapter provideRestAdapter(Server server, Client client, ApiHeaders headers) {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
        .create();

    return new RestAdapter.Builder()
        .setClient(client)
        .setServer(server)
        .setRequestInterceptor(headers)
        .setConverter(new GsonConverter(gson))
        .build();
  }

  @Provides
  @Singleton
  ServerInterface provideServerInterface(RestAdapter restAdapter) {
    return restAdapter.create(ServerInterface.class);
  }
}
