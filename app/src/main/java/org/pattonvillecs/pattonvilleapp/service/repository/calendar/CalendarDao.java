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

package org.pattonvillecs.pattonvilleapp.service.repository.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.service.model.calendar.DataSourceMarker;
import org.pattonvillecs.pattonvilleapp.service.model.calendar.PinnedEventMarker;
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.CalendarEvent;
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.PinnableCalendarEvent;
import org.threeten.bp.Instant;

import java.util.List;

/**
 * Created by Mitchell on 10/1/2017.
 */

@SuppressWarnings("NullableProblems")
//Due to nullable annotations not being overridden in "_Impl" classes
@Dao
public abstract class CalendarDao {

    //TODO make sensible @Transaction methods to keep database consistent when deleting events that were removed from calendars

    private static final String WHERE_EVENT_IS_PINNED = "(EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1))";
    private static final String SELECT_PINNABLE_EVENTS = "events.*, " + WHERE_EVENT_IS_PINNED + " AS pinned";
    private static final String ORDER_BY_DEFAULT = "events.start_date ASC";
    private static final String WHERE_DATASOURCE_MARKER_EXISTS = "(EXISTS (SELECT * FROM datasource_markers WHERE datasource_markers.uid = events.uid AND datasource_markers.datasource IN (:dataSources) LIMIT 1))";

    @Transaction
    @Query("SELECT " + SELECT_PINNABLE_EVENTS
            + " FROM events"
            + " WHERE " + WHERE_DATASOURCE_MARKER_EXISTS
            + " ORDER BY " + ORDER_BY_DEFAULT)
    abstract LiveData<List<PinnableCalendarEvent>> getEventsByDataSources(@NonNull List<DataSource> dataSources);

    @Transaction
    @Query("SELECT " + SELECT_PINNABLE_EVENTS
            + " FROM events"
            + " WHERE uid IN (:uids) AND " + WHERE_DATASOURCE_MARKER_EXISTS
            + " ORDER BY " + ORDER_BY_DEFAULT)
    abstract LiveData<List<PinnableCalendarEvent>> getEventsByUids(@NonNull List<DataSource> dataSources, @NonNull List<String> uids);

    @Transaction
    @Query("SELECT " + SELECT_PINNABLE_EVENTS
            + " FROM events"
            + " WHERE uid IN (:uids)"
            + " ORDER BY " + ORDER_BY_DEFAULT)
    abstract LiveData<List<PinnableCalendarEvent>> getEventsByUids(@NonNull List<String> uids);

    @Transaction
    @Query("SELECT " + SELECT_PINNABLE_EVENTS
            + " FROM events"
            + " WHERE end_date <= :lastDate AND " + WHERE_DATASOURCE_MARKER_EXISTS
            + " ORDER BY " + ORDER_BY_DEFAULT)
    abstract LiveData<List<PinnableCalendarEvent>> getEventsBeforeDate(@NonNull List<DataSource> dataSources, @NonNull Instant lastDate);

    @Transaction
    @Query("SELECT " + SELECT_PINNABLE_EVENTS
            + " FROM events"
            + " WHERE start_date >= :firstDate AND " + WHERE_DATASOURCE_MARKER_EXISTS
            + " ORDER BY " + ORDER_BY_DEFAULT)
    abstract LiveData<List<PinnableCalendarEvent>> getEventsAfterDate(@NonNull List<DataSource> dataSources, @NonNull Instant firstDate);

    @Transaction
    @Query("SELECT " + SELECT_PINNABLE_EVENTS
            + " FROM events"
            + " WHERE start_date >= :firstDate AND " + WHERE_DATASOURCE_MARKER_EXISTS + " AND " + WHERE_EVENT_IS_PINNED
            + " ORDER BY " + ORDER_BY_DEFAULT)
    abstract LiveData<List<PinnableCalendarEvent>> getPinnedEventsAfterDate(@NonNull List<DataSource> dataSources, @NonNull Instant firstDate);

    @Transaction
    @Query("SELECT " + SELECT_PINNABLE_EVENTS
            + " FROM events"
            + " WHERE (start_date >= :firstDate) AND (end_date <= :lastDate) AND " + WHERE_DATASOURCE_MARKER_EXISTS
            + " ORDER BY " + ORDER_BY_DEFAULT)
    abstract LiveData<List<PinnableCalendarEvent>> getEventsBetweenDates(@NonNull List<DataSource> dataSources, @NonNull Instant firstDate, @NonNull Instant lastDate);

    @Transaction
    @Query("SELECT " + SELECT_PINNABLE_EVENTS
            + " FROM events"
            + " WHERE " + WHERE_EVENT_IS_PINNED + " AND " + WHERE_DATASOURCE_MARKER_EXISTS
            + " ORDER BY " + ORDER_BY_DEFAULT)
    abstract LiveData<List<PinnableCalendarEvent>> getPinnedEvents(@NonNull List<DataSource> dataSources);

    @Query("SELECT events.start_date"
            + " FROM events"
            + " WHERE " + WHERE_DATASOURCE_MARKER_EXISTS)
    abstract LiveData<List<Instant>> getAllInstantsByDataSource(@NonNull List<DataSource> dataSources);

    @Query("SELECT datasource"
            + " FROM datasource_markers"
            + " WHERE uid IN (:uids)")
    abstract LiveData<List<DataSource>> getDataSources(@NonNull List<String> uids);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(@NonNull CalendarEvent calendarEvent, @NonNull List<DataSourceMarker> dataSourceMarkers, @NonNull PinnedEventMarker pinnedEventMarker);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(@NonNull CalendarEvent calendarEvent, @NonNull List<DataSourceMarker> dataSourceMarkers);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertAll(@NonNull List<CalendarEvent> calendarEvents, @NonNull List<DataSourceMarker> dataSourceMarkers);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insertAllIgnore(@NonNull List<CalendarEvent> calendarEvents, @NonNull List<DataSourceMarker> dataSourceMarkers);

    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract void updateAllIgnore(@NonNull List<CalendarEvent> calendarEvents, @NonNull List<DataSourceMarker> dataSourceMarkers);

    /**
     * @see <a href="https://en.wiktionary.org/wiki/upsert">Upsert</a>
     */
    @Transaction
    void upsertAll(@NonNull List<CalendarEvent> calendarEvents, @NonNull List<DataSourceMarker> dataSourceMarkers) {
        updateAllIgnore(calendarEvents, dataSourceMarkers);
        insertAllIgnore(calendarEvents, dataSourceMarkers);
    }

    @Delete
    abstract void deleteAll(@NonNull CalendarEvent... calendarEvent);

    @Update
    abstract void updateAll(@NonNull CalendarEvent... calendarEvent);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertPin(@NonNull PinnedEventMarker pinnedEventMarker);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertPins(@NonNull List<PinnedEventMarker> pinnedEventMarkers);

    @Delete
    abstract void deletePin(@NonNull PinnedEventMarker pinnedEventMarker);
}
