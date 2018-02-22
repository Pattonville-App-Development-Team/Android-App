/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pattonvillecs.pattonvilleapp.service.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import org.pattonvillecs.pattonvilleapp.service.model.calendar.DataSourceMarker;
import org.pattonvillecs.pattonvilleapp.service.model.calendar.PinnedEventMarker;
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.CalendarEvent;
import org.pattonvillecs.pattonvilleapp.service.model.directory.Faculty;
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarDao;
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.typeconverters.InstantTypeConverter;
import org.pattonvillecs.pattonvilleapp.service.repository.directory.DirectoryDao;

/**
 * Created by Mitchell on 10/1/2017.
 */

@Database(entities = {
        CalendarEvent.class,
        PinnedEventMarker.class,
        DataSourceMarker.class,
        Faculty.class
},
        version = 2)
@TypeConverters({
        DataSourceTypeConverter.class,
        InstantTypeConverter.class
})
public abstract class AppDatabase extends RoomDatabase {
    @NonNull
    public static RoomDatabase.Builder<AppDatabase> init(@NonNull RoomDatabase.Builder<AppDatabase> builder) {
        return builder.addMigrations(
                MIGRATION_1_2.INSTANCE
        );
    }

    public abstract CalendarDao calendarDao();

    public abstract DirectoryDao directoryDao();
}
