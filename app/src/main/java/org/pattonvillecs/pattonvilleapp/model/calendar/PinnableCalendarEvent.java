package org.pattonvillecs.pattonvilleapp.model.calendar;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.support.annotation.NonNull;

/**
 * Created by Mitchell on 10/5/2017.
 */

public class PinnableCalendarEvent {
    @Embedded
    @NonNull
    public final CalendarEvent calendarEvent;

    @ColumnInfo(name = "pinned")
    public final boolean pinned;

    public PinnableCalendarEvent(@NonNull CalendarEvent calendarEvent, boolean pinned) {
        this.calendarEvent = calendarEvent;
        this.pinned = pinned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinnableCalendarEvent that = (PinnableCalendarEvent) o;

        return pinned == that.pinned
                && calendarEvent.equals(that.calendarEvent);
    }

    @Override
    public int hashCode() {
        int result = calendarEvent.hashCode();
        result = 31 * result + (pinned ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PinnableCalendarEvent{" +
                "calendarEvent=" + calendarEvent +
                ", pinned=" + pinned +
                '}';
    }
}
