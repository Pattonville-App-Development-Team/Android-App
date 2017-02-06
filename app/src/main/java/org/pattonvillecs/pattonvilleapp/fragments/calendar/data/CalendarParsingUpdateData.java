package org.pattonvillecs.pattonvilleapp.fragments.calendar.data;

import com.google.common.collect.HashMultimap;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by Mitchell Skaggs on 2/2/17.
 */
public class CalendarParsingUpdateData {
    public static final int CALENDAR_LISTENER_ID = 1203481279;

    private final ConcurrentMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> calendarData;

    public CalendarParsingUpdateData(ConcurrentMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> calendarData) {
        this.calendarData = calendarData;
    }

    public ConcurrentMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> getCalendarData() {
        return calendarData;
    }
}
