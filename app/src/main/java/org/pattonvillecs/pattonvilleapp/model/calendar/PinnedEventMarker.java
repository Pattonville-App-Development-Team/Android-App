package org.pattonvillecs.pattonvilleapp.model.calendar;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Mitchell on 10/4/2017.
 */

@Entity(tableName = "pinned_event_markers")
public class PinnedEventMarker {
    @PrimaryKey
    @ColumnInfo(name = "uid", index = true, collate = ColumnInfo.BINARY)
    @NonNull
    public final String uid;

    public PinnedEventMarker(@NonNull String uid) {
        this.uid = uid;
    }
}
