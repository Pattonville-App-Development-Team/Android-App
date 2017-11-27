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

package org.pattonvillecs.pattonvilleapp.service.model.calendar

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable
import com.google.errorprone.annotations.Immutable
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.CalendarEvent

/**
 * Created by Mitchell on 10/9/2017.
 */

@Immutable
@Entity(tableName = "datasource_markers", primaryKeys = arrayOf("uid", "datasource"))
data class DataSourceMarker(@field:ColumnInfo(name = "uid", index = true, collate = ColumnInfo.BINARY)
                            val uid: String,
                            @field:ColumnInfo(name = "datasource", index = true, collate = ColumnInfo.BINARY)
                            val dataSource: DataSource) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readSerializable() as DataSource)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeSerializable(dataSource)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataSourceMarker> {
        @JvmStatic
        fun dataSource(calendarEvent: CalendarEvent, dataSource: DataSource): DataSourceMarker = DataSourceMarker(calendarEvent.uid, dataSource)

        override fun createFromParcel(parcel: Parcel): DataSourceMarker {
            return DataSourceMarker(parcel)
        }

        override fun newArray(size: Int): Array<DataSourceMarker?> {
            return arrayOfNulls(size)
        }
    }
}
