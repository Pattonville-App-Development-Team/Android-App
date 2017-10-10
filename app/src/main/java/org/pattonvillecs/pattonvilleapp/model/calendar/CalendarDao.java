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
    @Query("SELECT"
            + " events.*, EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1) AS pinned"
            + " FROM events"
            + " WHERE (EXISTS (SELECT * FROM datasource_markers WHERE datasource_markers.uid = events.uid AND datasource_markers.datasource IN (:dataSources) LIMIT 1))")
    LiveData<List<PinnableCalendarEvent>> getEvents(@NonNull List<DataSource> dataSources);

    @Query("SELECT"
            + " events.*, EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid LIMIT 1) AS pinned"
            + " FROM events"
            + " WHERE (EXISTS (SELECT * FROM datasource_markers WHERE datasource_markers.uid = events.uid AND datasource_markers.datasource = :dataSource LIMIT 1))")
    LiveData<List<PinnableCalendarEvent>> getEvents(@NonNull DataSource dataSource);

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
    void pinEvent(@NonNull PinnedEventMarker pinnedEventMarker);

    @Delete
    void unPinEvent(@NonNull PinnedEventMarker pinnedEventMarker);
}
