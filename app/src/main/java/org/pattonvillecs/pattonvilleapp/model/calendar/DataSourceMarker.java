package org.pattonvillecs.pattonvilleapp.model.calendar;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import org.pattonvillecs.pattonvilleapp.DataSource;

/**
 * Created by Mitchell on 10/9/2017.
 */

@Entity(tableName = "datasource_markers",
        primaryKeys = {"uid", "datasource"})
public class DataSourceMarker {
    @NonNull
    @ColumnInfo(name = "uid", index = true, collate = ColumnInfo.BINARY)
    public final String uid;

    @NonNull
    @ColumnInfo(name = "datasource", index = true, collate = ColumnInfo.BINARY)
    public final DataSource dataSource;

    public DataSourceMarker(@NonNull String uid, @NonNull DataSource dataSource) {
        this.uid = uid;
        this.dataSource = dataSource;
    }
}
