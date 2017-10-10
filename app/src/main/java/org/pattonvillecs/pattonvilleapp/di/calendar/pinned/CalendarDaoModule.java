package org.pattonvillecs.pattonvilleapp.di.calendar.pinned;

import android.util.Log;

import org.pattonvillecs.pattonvilleapp.model.AppDatabase;
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Mitchell on 10/4/2017.
 */

@Module
@Deprecated
public class CalendarDaoModule {
    @Provides
    @Singleton
    static CalendarDao provideCalendarEventDao(AppDatabase appDatabase) {
        Log.i("AppModule", "Pinned Event DAO provided!");
        return appDatabase.calendarDao();
    }
}
