package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.collections4.map.MultiValueMap;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Mitchell on 12/24/2016.
 */

public class CalendarData implements Parcelable {
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
    private EnumMap<DataSource, MultiValueMap<SerializableCalendarDay, VEvent>> calendars;

    /**
     * Uses the provided calendars
     *
     * @param calendars the calendars to be used
     */
    public CalendarData(EnumMap<DataSource, MultiValueMap<SerializableCalendarDay, VEvent>> calendars) {
        this.calendars = calendars;
    }

    /**
     * Initializes empty
     */
    public CalendarData() {
        this(new EnumMap<DataSource, MultiValueMap<SerializableCalendarDay, VEvent>>(DataSource.class));
    }

    protected CalendarData(Parcel in) {
        //noinspection unchecked
        this.calendars = (EnumMap<DataSource, MultiValueMap<SerializableCalendarDay, VEvent>>) in.readSerializable();
    }

    public Map<DataSource, MultiValueMap<SerializableCalendarDay, VEvent>> getCalendars() {
        return calendars;
    }

    public MultiValueMap<SerializableCalendarDay, VEvent> getCalendarForDataSource(DataSource dataSource) {
        return calendars.get(dataSource);
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
