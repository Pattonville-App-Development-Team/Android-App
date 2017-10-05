package org.pattonvillecs.pattonvilleapp.di.calendar.pinned;

import android.util.Log;

import org.pattonvillecs.pattonvilleapp.di.database.AppDatabaseModule;
import org.pattonvillecs.pattonvilleapp.model.AppDatabase;
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarEventDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Mitchell on 10/4/2017.
 */

@Module(includes = {AppDatabaseModule.class})
public class CalendarEventsModule {
    @Provides
    @Singleton
    static CalendarEventDao provideCalendarEventDao(AppDatabase appDatabase) {
        Log.i("AppModule", "Pinned Event DAO provided!");
        return appDatabase.calendarEventDao();
    }
}
