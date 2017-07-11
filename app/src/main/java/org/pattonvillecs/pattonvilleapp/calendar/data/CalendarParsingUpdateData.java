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

package org.pattonvillecs.pattonvilleapp.calendar.data;

import org.pattonvillecs.pattonvilleapp.calendar.events.EventFlexibleItem;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Mitchell Skaggs on 2/2/17.
 */
public class CalendarParsingUpdateData {
    public static final int CALENDAR_LISTENER_ID = 1203481279;

    private final TreeSet<EventFlexibleItem> calendarData;
    private final Set<RetrieveCalendarDataAsyncTask> runningCalendarAsyncTasks;

    public CalendarParsingUpdateData(TreeSet<EventFlexibleItem> calendarData, Set<RetrieveCalendarDataAsyncTask> runningCalendarAsyncTasks) {
        this.calendarData = calendarData;
        this.runningCalendarAsyncTasks = runningCalendarAsyncTasks;
    }

    public TreeSet<EventFlexibleItem> getCalendarData() {
        return calendarData;
    }

    public Set<RetrieveCalendarDataAsyncTask> getRunningCalendarAsyncTasks() {
        return runningCalendarAsyncTasks;
    }
}
