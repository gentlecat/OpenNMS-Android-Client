package org.opennms.android;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

public class App extends Application {

  private ObjectGraph objectGraph;

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);

    // Initializing objectGraph here because we'll need it early in ContentProvider.
    createObjectGraph();
  }

  public void inject(Object o) {
    objectGraph.inject(o);
  }

  public static App get(Context context) {
    return (App) context.getApplicationContext();
  }

  public void createObjectGraph() {
    // Initializing objectGraph here because we'll need it early in ContentProvider.
    objectGraph = ObjectGraph.create(Modules.list(this));
    objectGraph.inject(this);
  }

}
