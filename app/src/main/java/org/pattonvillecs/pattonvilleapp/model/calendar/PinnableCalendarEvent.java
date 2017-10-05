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

    @ColumnInfo()
    public final boolean pinned;

    public PinnableCalendarEvent(@NonNull CalendarEvent calendarEvent, boolean pinned) {
        this.calendarEvent = calendarEvent;
        this.pinned = pinned;
    }
}
