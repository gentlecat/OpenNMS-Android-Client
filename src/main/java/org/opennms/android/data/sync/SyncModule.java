package org.opennms.android.data.sync;

import android.app.Application;

import org.opennms.android.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                Updater.class,
                UpdateManager.class,
                AlarmsSyncAdapter.class,
        },
        complete = false,
        library = true
)
public final class SyncModule {

    @Provides
    @Singleton
    Updater provideUpdater(Application app) {
        return new Updater(App.get(app));
    }

    @Provides
    @Singleton
    UpdateManager provideUpdateManager(Application app) {
        return new UpdateManager(App.get(app));
    }
}
