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
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Mitchell on 10/4/2017.
 */

@Entity(tableName = "pinned_event_markers",
        foreignKeys = {
                @ForeignKey(
                        entity = CalendarEvent.class,
                        parentColumns = "uid",
                        childColumns = "uid",
                        onDelete = ForeignKey.CASCADE,
                        deferred = true)
        })
public class PinnedEventMarker {
    @PrimaryKey
    @ColumnInfo(name = "uid", index = true, collate = ColumnInfo.BINARY)
    @NonNull
    public final String uid;

    public PinnedEventMarker(@NonNull String uid) {
        this.uid = uid;
    }

    public PinnedEventMarker(@NonNull CalendarEvent calendarEvent) {
        this(calendarEvent.uid);
    }

    public static PinnedEventMarker pin(CalendarEvent calendarEvent) {
        return new PinnedEventMarker(calendarEvent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinnedEventMarker that = (PinnedEventMarker) o;

        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }
}
