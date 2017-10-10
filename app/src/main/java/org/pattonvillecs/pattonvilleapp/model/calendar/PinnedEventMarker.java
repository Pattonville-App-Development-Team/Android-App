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

    public static PinnedEventMarker pinned(CalendarEvent calendarEvent) {
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
