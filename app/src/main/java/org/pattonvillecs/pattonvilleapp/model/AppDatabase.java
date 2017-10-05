package org.pattonvillecs.pattonvilleapp.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarEvent;
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarEventDao;
import org.pattonvillecs.pattonvilleapp.model.calendar.PinnedEventMarker;

/**
 * Created by Mitchell on 10/1/2017.
 */

@Database(entities = {CalendarEvent.class, PinnedEventMarker.class}, version = 1)
@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract CalendarEventDao calendarEventDao();
}
