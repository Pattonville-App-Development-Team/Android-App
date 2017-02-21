package org.pattonvillecs.pattonvilleapp.fragments.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.HashMultimap;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventFlexibleItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mitchell Skaggs on 12/24/2016.
 */

@Deprecated
public class CalendarData implements Parcelable, Serializable {
    public static final Creator<CalendarData> CREATOR = new Creator<CalendarData>() {
        @Override
        public CalendarData createFromParcel(Parcel in) {
            return new CalendarData(in);
        }

        @Override
        public CalendarData[] newArray(int size) {
            return new CalendarData[size];
        }
    };
    private EnumMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendars;

    /**
     * Uses the provided calendars
     *
     * @param calendars the calendars to be used
     */
    public CalendarData(EnumMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendars) {
        this.calendars = calendars;
    }

    /**
     * Initializes empty
     */
    public CalendarData() {
        this(new EnumMap<DataSource, HashMultimap<CalendarDay, VEvent>>(DataSource.class));
    }

    protected CalendarData(Parcel in) {
        //noinspection unchecked
        this.calendars = (EnumMap<DataSource, HashMultimap<CalendarDay, VEvent>>) in.readSerializable();
    }

    public Map<DataSource, HashMultimap<CalendarDay, VEvent>> getCalendars() {
        return calendars;
    }

    public HashMultimap<CalendarDay, VEvent> getCalendarForDataSource(DataSource dataSource) {
        return calendars.get(dataSource);
    }

    private List<VEvent> getEventsForDay(CalendarDay day) {
        List<VEvent> events = new ArrayList<>();
        for (Map.Entry<DataSource, HashMultimap<CalendarDay, VEvent>> entry : calendars.entrySet()) {
            events.addAll(entry.getValue().get(day));
        }
        return events;
    }

    private List<EventFlexibleItem> getItemsForDay(CalendarDay day) {
        List<EventFlexibleItem> events = new ArrayList<>();
        for (Map.Entry<DataSource, HashMultimap<CalendarDay, VEvent>> entry : calendars.entrySet()) {
            if (entry.getValue().containsKey(day))
                for (VEvent vEvent : entry.getValue().get(day)) {
                    events.add(new EventFlexibleItem(entry.getKey(), vEvent));
                }
        }
        return events;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(calendars);
    }
}
