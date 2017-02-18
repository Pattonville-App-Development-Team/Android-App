package org.pattonvillecs.pattonvilleapp.fragments.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.HashMultimap;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventFlexibleItem;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;

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
    private EnumMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> calendars;

    /**
     * Uses the provided calendars
     *
     * @param calendars the calendars to be used
     */
    public CalendarData(EnumMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> calendars) {
        this.calendars = calendars;
    }

    /**
     * Initializes empty
     */
    public CalendarData() {
        this(new EnumMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>>(DataSource.class));
    }

    protected CalendarData(Parcel in) {
        //noinspection unchecked
        this.calendars = (EnumMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>>) in.readSerializable();
    }

    public Map<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> getCalendars() {
        return calendars;
    }

    public HashMultimap<SerializableCalendarDay, VEvent> getCalendarForDataSource(DataSource dataSource) {
        return calendars.get(dataSource);
    }

    public List<VEvent> getEventsForDay(CalendarDay day) {
        return getEventsForDay(SerializableCalendarDay.of(day));
    }

    private List<VEvent> getEventsForDay(SerializableCalendarDay day) {
        List<VEvent> events = new ArrayList<>();
        for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendars.entrySet()) {
            events.addAll(entry.getValue().get(day));
        }
        return events;
    }

    public List<EventFlexibleItem> getItemsForDay(CalendarDay day) {
        return getItemsForDay(SerializableCalendarDay.of(day));
    }

    private List<EventFlexibleItem> getItemsForDay(SerializableCalendarDay day) {
        List<EventFlexibleItem> events = new ArrayList<>();
        for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendars.entrySet()) {
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
