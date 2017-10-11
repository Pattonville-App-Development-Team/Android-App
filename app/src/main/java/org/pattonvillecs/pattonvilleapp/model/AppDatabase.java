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
