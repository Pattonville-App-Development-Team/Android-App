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

package org.pattonvillecs.pattonvilleapp.model.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.threeten.bp.Instant;

import java.util.List;

/**
 * Created by Mitchell on 10/1/2017.
 */

@SuppressWarnings("NullableProblems")
//Due to nullable annotations not being overridden in "_Impl" classes
@Dao
public interface CalendarDao {

    @Query("SELECT events.*, EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1) AS pinned"
            + " FROM events"
            + " WHERE (EXISTS (SELECT * FROM datasource_markers WHERE datasource_markers.uid = events.uid AND datasource_markers.datasource IN (:dataSources) LIMIT 1))")
    LiveData<List<PinnableCalendarEvent>> getEventsByDataSources(@NonNull List<DataSource> dataSources);

    @Query("SELECT events.*, EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1) AS pinned"
            + " FROM events"
            + " WHERE uid IN (:uids) AND (EXISTS (SELECT * FROM datasource_markers WHERE datasource_markers.uid = events.uid AND datasource_markers.datasource IN (:dataSources) LIMIT 1))")
    LiveData<List<PinnableCalendarEvent>> getEventsByUids(@NonNull List<DataSource> dataSources, @NonNull List<String> uids);

    @Query("SELECT events.*, EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1) AS pinned"
            + " FROM events"
            + " WHERE end_date <= :lastDate AND (EXISTS (SELECT * FROM datasource_markers WHERE datasource_markers.uid = events.uid AND datasource_markers.datasource IN (:dataSources) LIMIT 1))")
    LiveData<List<PinnableCalendarEvent>> getEventsBeforeDate(@NonNull List<DataSource> dataSources, @NonNull Instant lastDate);

    @Query("SELECT events.*, EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1) AS pinned"
            + " FROM events"
            + " WHERE start_date >= :firstDate AND (EXISTS (SELECT * FROM datasource_markers WHERE datasource_markers.uid = events.uid AND datasource_markers.datasource IN (:dataSources) LIMIT 1))")
    LiveData<List<PinnableCalendarEvent>> getEventsAfterDate(@NonNull List<DataSource> dataSources, @NonNull Instant firstDate);

    @Query("SELECT events.*, EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1) AS pinned"
            + " FROM events"
            + " WHERE start_date >= :firstDate AND end_date <= :lastDate AND (EXISTS (SELECT * FROM datasource_markers WHERE datasource_markers.uid = events.uid AND datasource_markers.datasource IN (:dataSources) LIMIT 1))")
    LiveData<List<PinnableCalendarEvent>> getEventsBetweenDates(@NonNull List<DataSource> dataSources, @NonNull Instant firstDate, @NonNull Instant lastDate);

    @Query("SELECT events.start_date"
            + " FROM events"
            + " WHERE (EXISTS (SELECT * FROM datasource_markers WHERE datasource_markers.uid = events.uid AND datasource_markers.datasource IN (:dataSources) LIMIT 1))")
    LiveData<List<Instant>> getAllInstantsByDataSource(@NonNull List<DataSource> dataSources);

    @Query("SELECT datasource"
            + " FROM datasource_markers"
            + " WHERE uid IN (:uids)")
    LiveData<List<DataSource>> getDataSources(@NonNull List<String> uids);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull CalendarEvent calendarEvent, @NonNull List<DataSourceMarker> dataSourceMarkers, @NonNull PinnedEventMarker pinnedEventMarker);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull CalendarEvent calendarEvent, @NonNull List<DataSourceMarker> dataSourceMarkers);

    @Delete
    void deleteAll(@NonNull CalendarEvent... calendarEvent);

    @Update
    void updateAll(@NonNull CalendarEvent... calendarEvent);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPin(@NonNull PinnedEventMarker pinnedEventMarker);

    @Delete
    void deletePin(@NonNull PinnedEventMarker pinnedEventMarker);
}
