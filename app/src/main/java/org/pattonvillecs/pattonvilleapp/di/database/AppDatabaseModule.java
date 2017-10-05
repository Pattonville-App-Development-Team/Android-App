package org.pattonvillecs.pattonvilleapp.di.database;

import android.app.Application;
import android.arch.persistence.room.Room;

import org.pattonvillecs.pattonvilleapp.model.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Mitchell on 10/4/2017.
 */

@Module
public class AppDatabaseModule {
    @Provides
    @Singleton
    static AppDatabase provideAppDatabase(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, "app_database")
                .build();
    }
}
