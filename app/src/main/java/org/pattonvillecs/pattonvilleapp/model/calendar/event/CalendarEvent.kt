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

package org.pattonvillecs.pattonvilleapp.model.calendar.event

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.google.errorprone.annotations.Immutable
import net.fortuna.ical4j.model.component.VEvent
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * Created by Mitchell on 10/1/2017.
 */

@Immutable
@Entity(tableName = "events")
data class CalendarEvent(@field:PrimaryKey
                         @field:ColumnInfo(name = "uid", index = true, collate = ColumnInfo.BINARY)
                         val uid: String,
                         @field:ColumnInfo(name = "summary")
                         val summary: String,
                         @field:ColumnInfo(name = "location")
                         val location: String,
                         @field:ColumnInfo(name = "start_date", index = true)
                         val startDateTime: Instant,
                         @field:ColumnInfo(name = "end_date", index = true)
                         val endDateTime: Instant) : HasStartDate, HasEndDate, Parcelable {
    constructor(vEvent: VEvent) : this(
            vEvent.uid.value,
            vEvent.summary.value,
            vEvent.location.value,
            Instant.ofEpochMilli(vEvent.startDate.date.time),
            Instant.ofEpochMilli(vEvent.endDate.date.time))

    @delegate:Ignore
    override val startDate: LocalDate by lazy { LocalDateTime.ofInstant(startDateTime, ZoneId.systemDefault()).toLocalDate() }

    @delegate:Ignore
    override val endDate: LocalDate by lazy { LocalDateTime.ofInstant(endDateTime, ZoneId.systemDefault()).toLocalDate() }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readSerializable() as Instant,
            parcel.readSerializable() as Instant)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(summary)
        parcel.writeString(location)
        parcel.writeSerializable(startDateTime)
        parcel.writeSerializable(endDateTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CalendarEvent> {
        override fun createFromParcel(parcel: Parcel): CalendarEvent {
            return CalendarEvent(parcel)
        }

        override fun newArray(size: Int): Array<CalendarEvent?> {
            return arrayOfNulls(size)
        }
    }
}
