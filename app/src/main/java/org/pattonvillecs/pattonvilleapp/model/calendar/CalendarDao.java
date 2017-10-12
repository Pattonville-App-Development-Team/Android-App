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
    LiveData<List<PinnableCalendarEvent>> getEventsByDataSource(@NonNull List<DataSource> dataSources);

    @Query("SELECT events.*, EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1) AS pinned"
            + " FROM events"
            + " WHERE (EXISTS (SELECT * FROM datasource_markers WHERE datasource_markers.uid = events.uid AND datasource_markers.datasource = :dataSource LIMIT 1))")
    LiveData<List<PinnableCalendarEvent>> getEventsByDataSource(@NonNull DataSource dataSource);

    @Query("SELECT events.*, EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1) AS pinned"
            + " FROM events"
            + " WHERE uid IN (:uids)")
    LiveData<List<PinnableCalendarEvent>> getEventsByUid(@NonNull List<String> uids);

    @Query("SELECT events.*, EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1) AS pinned"
            + " FROM events"
            + " WHERE uid = :uid")
    LiveData<List<PinnableCalendarEvent>> getEventsByUid(@NonNull String uid);

    @Query("SELECT datasource"
            + " FROM datasource_markers"
            + " WHERE uid IN (:uids)")
    LiveData<List<DataSource>> getDataSources(@NonNull List<String> uids);

    @Query("SELECT datasource"
            + " FROM datasource_markers"
            + " WHERE uid = :uid")
    LiveData<List<DataSource>> getDataSources(@NonNull String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull CalendarEvent calendarEvent, @NonNull List<DataSourceMarker> dataSourceMarkers, @NonNull PinnedEventMarker pinnedEventMarker);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull CalendarEvent calendarEvent, @NonNull DataSourceMarker dataSourceMarker, @NonNull PinnedEventMarker pinnedEventMarker);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull CalendarEvent calendarEvent, @NonNull List<DataSourceMarker> dataSourceMarkers);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull CalendarEvent calendarEvent, @NonNull DataSourceMarker dataSourceMarker);

    @Delete
    void deleteAll(@NonNull CalendarEvent... calendarEvent);

    @Update
    void updateAll(@NonNull CalendarEvent... calendarEvent);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPin(@NonNull PinnedEventMarker pinnedEventMarker);

    @Delete
    void deletePin(@NonNull PinnedEventMarker pinnedEventMarker);
}
