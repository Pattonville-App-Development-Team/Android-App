package org.pattonvillecs.pattonvilleapp.model.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Mitchell on 10/1/2017.
 */

@Dao
public interface CalendarEventDao {
    @Query("SELECT * FROM events WHERE uid = :uid")
    LiveData<List<CalendarEvent>> getEventByUid(String uid);

    @Query("SELECT"
            + " events.*,"
            + " EXISTS (SELECT * FROM pinned_event_markers WHERE events.uid = pinned_event_markers.uid) AS pinned"
            + " FROM events")
    LiveData<List<PinnableCalendarEvent>> getEvents();

    @Insert
    void insertAll(CalendarEvent... calendarEvents);

    @Delete
    void delete(CalendarEvent calendarEvent);

    @Update
    void update(CalendarEvent calendarEvent);

    @Insert
    void pinEvent(PinnedEventMarker pinnedEventMarker);
}
