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

    public CalendarData() {
        calendars = new EnumMap<>(DataSource.class);
    }
}
