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
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by Mitchell on 10/1/2017.
 */

@Entity(tableName = "events")
public class CalendarEvent {
    @PrimaryKey
    @ColumnInfo(name = "uid", index = true, collate = ColumnInfo.BINARY)
    @NonNull
    public final String uid;

    @ColumnInfo(name = "summary")
    @NonNull
    public final String summary;

    @ColumnInfo(name = "location")
    @NonNull
    public final String location;

    @ColumnInfo(name = "start_date")
    @NonNull
    private final Date startDate;

    @ColumnInfo(name = "end_date")
    @NonNull
    private final Date endDate;

    public CalendarEvent(@NonNull String uid, @NonNull String summary, @NonNull String location, @NonNull Date startDate, @NonNull Date endDate) {
        this.uid = uid;
        this.summary = summary;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @NonNull
    public Date getStartDate() {
        return (Date) startDate.clone();
    }

    @NonNull
    public Date getEndDate() {
        return (Date) endDate.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarEvent that = (CalendarEvent) o;

        return uid.equals(that.uid)
                && summary.equals(that.summary)
                && location.equals(that.location)
                && startDate.equals(that.startDate)
                && endDate.equals(that.endDate);
    }

    @Override
    public int hashCode() {
        int result = uid.hashCode();
        result = 31 * result + summary.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CalendarEvent{" +
                "uid='" + uid + '\'' +
                ", summary='" + summary + '\'' +
                ", location='" + location + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
