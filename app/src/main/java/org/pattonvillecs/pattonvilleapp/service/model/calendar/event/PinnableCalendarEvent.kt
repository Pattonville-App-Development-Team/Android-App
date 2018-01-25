/*
 * Copyright (C) 2017 - 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.service.model.calendar.event

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import android.os.Parcel
import android.os.Parcelable
import com.google.errorprone.annotations.Immutable
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.service.model.calendar.DataSourceMarker

/**
 * Created by Mitchell on 10/5/2017.
 */

@Immutable
data class PinnableCalendarEvent @JvmOverloads constructor(@field:Embedded
                                                           val calendarEvent: CalendarEvent,
                                                           @field:ColumnInfo(name = "pinned")
                                                           val pinned: Boolean,
                                                           @field:Relation(parentColumn = "uid", entityColumn = "uid", entity = DataSourceMarker::class)
                                                           var dataSourceMarkers: Set<DataSourceMarker> = setOf()) : ICalendarEvent by calendarEvent, Parcelable {

    @delegate:Ignore
    val dataSources: Set<DataSource> by lazy { dataSourceMarkers.mapTo(mutableSetOf(), { it.dataSource }) }

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(CalendarEvent::class.java.classLoader),
            parcel.readByte() != 0.toByte(),
            parcel.readParcelableArray(DataSourceMarker::class.java.classLoader).mapTo(mutableSetOf(), { it as DataSourceMarker }))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PinnableCalendarEvent

        if (calendarEvent != other.calendarEvent) return false
        if (dataSourceMarkers != other.dataSourceMarkers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = calendarEvent.hashCode()
        result = 31 * result + dataSourceMarkers.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(calendarEvent, flags)
        parcel.writeByte(if (pinned) 1 else 0)
        parcel.writeParcelableArray(dataSourceMarkers.toTypedArray(), flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PinnableCalendarEvent> {
        override fun createFromParcel(parcel: Parcel): PinnableCalendarEvent {
            return PinnableCalendarEvent(parcel)
        }

        override fun newArray(size: Int): Array<PinnableCalendarEvent?> {
            return arrayOfNulls(size)
        }
    }
}
