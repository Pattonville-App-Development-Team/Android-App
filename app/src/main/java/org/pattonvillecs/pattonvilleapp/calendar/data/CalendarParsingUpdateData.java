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
