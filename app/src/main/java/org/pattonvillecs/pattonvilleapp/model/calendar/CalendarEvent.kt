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

package org.pattonvillecs.pattonvilleapp.model.calendar

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * Created by Mitchell on 10/1/2017.
 */

@Entity(tableName = "events")
class CalendarEvent(@field:PrimaryKey
                    @field:ColumnInfo(name = "uid", index = true, collate = ColumnInfo.BINARY)
                    val uid: String,
                    @field:ColumnInfo(name = "summary")
                    val summary: String,
                    @field:ColumnInfo(name = "location")
                    val location: String,
                    @field:ColumnInfo(name = "start_date", index = true)
                    val startDate: Instant,
                    @field:ColumnInfo(name = "end_date", index = true)
                    val endDate: Instant) {

    @delegate:Ignore
    val startDay by lazy {
        LocalDateTime.ofInstant(startDate, ZoneId.systemDefault()).toLocalDate()
    }

    @delegate:Ignore
    val endDay by lazy {
        LocalDateTime.ofInstant(endDate, ZoneId.systemDefault()).toLocalDate()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as CalendarEvent?

        return (uid == that!!.uid
                && summary == that.summary
                && location == that.location
                && startDate == that.startDate
                && endDate == that.endDate)
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + summary.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + endDate.hashCode()
        return result
    }

    override fun toString(): String {
        return "CalendarEvent{" +
                "uid='" + uid + '\'' +
                ", summary='" + summary + '\'' +
                ", location='" + location + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}'
    }
}
