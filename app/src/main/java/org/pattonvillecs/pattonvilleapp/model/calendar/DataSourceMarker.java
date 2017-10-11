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

    public static DataSourceMarker dataSource(CalendarEvent calendarEvent, DataSource dataSource) {
        return new DataSourceMarker(calendarEvent.uid, dataSource);
    }
}
