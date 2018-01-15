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

package org.pattonvillecs.pattonvilleapp.service.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

import org.pattonvillecs.pattonvilleapp.service.model.calendar.DataSourceMarker
import org.pattonvillecs.pattonvilleapp.service.model.calendar.PinnedEventMarker
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.CalendarEvent
import org.pattonvillecs.pattonvilleapp.service.model.directory.Faculty
import org.pattonvillecs.pattonvilleapp.service.model.news.ArticleSummary
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarDao
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.typeconverters.InstantTypeConverter
import org.pattonvillecs.pattonvilleapp.service.repository.directory.DirectoryDao
import org.pattonvillecs.pattonvilleapp.service.repository.news.NewsDao

/**
 * Created by Mitchell on 10/1/2017.
 */

@Database(
        entities = [
            CalendarEvent::class,
            PinnedEventMarker::class,
            org.pattonvillecs.pattonvilleapp.service.model.calendar.DataSourceMarker::class,
            Faculty::class,
            ArticleSummary::class,
            org.pattonvillecs.pattonvilleapp.service.model.news.DataSourceMarker::class
        ],
        version = 3)
@TypeConverters(
        DataSourceTypeConverter::class,
        InstantTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun calendarDao(): CalendarDao

    abstract fun directoryDao(): DirectoryDao

    abstract fun newsDao(): NewsDao

    companion object {
        @JvmStatic
        fun init(builder: RoomDatabase.Builder<AppDatabase>): RoomDatabase.Builder<AppDatabase> {
            return builder.addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3
            )
        }
    }
}
