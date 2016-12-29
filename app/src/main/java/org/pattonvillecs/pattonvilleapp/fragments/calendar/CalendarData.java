package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.collections4.map.MultiValueMap;
import org.pattonvillecs.pattonvilleapp.DataSource;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Mitchell on 12/24/2016.
 */

public class CalendarData {
    private Map<DataSource, MultiValueMap<CalendarDay, VEvent>> calendars;

    /**
     * Uses the provided calendars
     *
     * @param calendars the calendars to be used
     */
    public CalendarData(Map<DataSource, MultiValueMap<CalendarDay, VEvent>> calendars) {
        this.calendars = calendars;
    }

    /**
     * Initializes empty
     */
    public CalendarData() {
        this(new EnumMap<DataSource, MultiValueMap<CalendarDay, VEvent>>(DataSource.class));
    }

    public Map<DataSource, MultiValueMap<CalendarDay, VEvent>> getCalendars() {
        return calendars;
    }

    public MultiValueMap<CalendarDay, VEvent> getCalendarForDataSource(DataSource dataSource) {
        return calendars.get(dataSource);
    }
}
