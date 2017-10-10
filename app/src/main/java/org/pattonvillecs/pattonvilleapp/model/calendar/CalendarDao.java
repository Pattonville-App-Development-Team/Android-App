package org.pattonvillecs.pattonvilleapp.model.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

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
            + " FROM events")
    LiveData<List<PinnableCalendarEvent>> getEvents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull CalendarEvent calendarEvent, @NonNull PinnedEventMarker pinnedEventMarker, @NonNull DataSourceMarker... dataSourceMarkers);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull CalendarEvent calendarEvent, @NonNull DataSourceMarker... dataSourceMarkers);

    @Delete
    void deleteAll(@NonNull CalendarEvent... calendarEvent);

    @Update
    void updateAll(@NonNull CalendarEvent... calendarEvent);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void pinEvent(@NonNull PinnedEventMarker pinnedEventMarker);

    @Delete
    void unPinEvent(@NonNull PinnedEventMarker pinnedEventMarker);
}
