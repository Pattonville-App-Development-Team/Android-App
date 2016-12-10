package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Function;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import java.util.Date;

/**
 * Created by Mitchell on 10/18/2016.
 */

public class CalendarEvent implements Parcelable {
    public static final Creator<CalendarEvent> CREATOR = new Creator<CalendarEvent>() {
        @Override
        public CalendarEvent createFromParcel(Parcel in) {
            return new CalendarEvent(in);
        }

        @Override
        public CalendarEvent[] newArray(int size) {
            return new CalendarEvent[size];
        }
    };
    private final String eventName;
    private final Date dateAndTime;
    private final String eventDetails;
    private final String eventLocation;

    public CalendarEvent(String eventName, Date dateAndTime, String eventDetails, String eventLocation) {
        this.eventName = eventName;
        this.dateAndTime = dateAndTime;
        this.eventDetails = eventDetails;
        this.eventLocation = eventLocation;
    }

    protected CalendarEvent(Parcel in) {
        eventName = in.readString();
        dateAndTime = (Date) in.readSerializable();
        eventDetails = in.readString();
        eventLocation = in.readString();
    }

    public CalendarEvent(VEvent calendarVEvent) {
        this(Optional.ofNullable(calendarVEvent.getSummary()).map(new Function<Summary, String>() {
                    @Override
                    public String apply(Summary summary) {
                        return summary.getValue();
                    }
                }).orElse(""),
                calendarVEvent.getStartDate().getDate(),
                Optional.ofNullable(calendarVEvent.getDescription()).map(new Function<Description, String>() {
                    @Override
                    public String apply(Description description) {
                        return description.getValue();
                    }
                }).orElse(""),
                Optional.ofNullable(calendarVEvent.getLocation()).map(new Function<Location, String>() {
                    @Override
                    public String apply(Location location) {
                        return location.getValue();
                    }
                }).orElse(""));
    }

    public String getEventName() {
        return eventName;
    }

    public Date getDateAndTime() {
        return dateAndTime;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeSerializable(dateAndTime);
        dest.writeString(eventDetails);
        dest.writeString(eventLocation);
    }
}
