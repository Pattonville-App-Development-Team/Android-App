package org.pattonvillecs.pattonvilleapp.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarDao;
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarEvent;
import org.pattonvillecs.pattonvilleapp.model.calendar.DataSourceMarker;
import org.pattonvillecs.pattonvilleapp.model.calendar.PinnedEventMarker;
import org.pattonvillecs.pattonvilleapp.model.typeconverters.DataSourceTypeConverter;
import org.pattonvillecs.pattonvilleapp.model.typeconverters.DateTypeConverter;

/**
 * Created by Mitchell on 10/1/2017.
 */

@Database(entities = {CalendarEvent.class, PinnedEventMarker.class, DataSourceMarker.class}, version = 1)
@TypeConverters({
        DateTypeConverter.class,
        DataSourceTypeConverter.class
})
public abstract class AppDatabase extends RoomDatabase {
    @NonNull
    public static RoomDatabase.Builder<AppDatabase> init(@NonNull RoomDatabase.Builder<AppDatabase> builder) {
        return builder;
    }

    public abstract CalendarDao calendarDao();
}
