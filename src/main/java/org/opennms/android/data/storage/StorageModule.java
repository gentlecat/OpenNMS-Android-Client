package org.opennms.android.data.storage;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
    complete = false,
    library = true
)
public final class StorageModule {

  @Provides
  @Singleton
  SQLiteDatabase provideDatabase(Application app) {
    return new DatabaseHelper(app).getWritableDatabase();
  }
}
