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
