package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import java.util.Date;

/**
 * Created by Mitchell on 10/18/2016.
 * <p>
 * This class represents a single calendar event.
 *
 * @author Mitchell Skaggs
 * @see android.os.Parcelable
 * @since 1.0
 */

public class CalendarEvent implements Parcelable {
    /**
     * Helper for parcelable serialization
     */
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
    /**
     * The name of the event. Equal to the empty string if not provided.
     */
    @NonNull
    private final String eventName;
    /**
     * The date that the event starts on. Always present.
     */
    @NonNull
    private final Date dateAndTime;
    /**
     * The details of the event. Equal to the empty string if not provided.
     */
    @NonNull
    private final String eventDetails;
    /**
     * The name of the location of the event. Equal to the empty string if not provided.
     */
    @NonNull
    private final String eventLocation;

    /**
     * A constructor that takes every part individually. Typically called by other constructors.
     *
     * @param eventName     The name of the event. May be the empty string.
     * @param dateAndTime   The date of the event. Must be valid.
     * @param eventDetails  The details of the event. May be the empty string.
     * @param eventLocation The name of the location of the event. May be the empty string.
     */
    public CalendarEvent(@NonNull String eventName, @NonNull Date dateAndTime, @NonNull String eventDetails, @NonNull String eventLocation) {
        this.eventName = eventName;
        this.dateAndTime = dateAndTime;
        this.eventDetails = eventDetails;
        this.eventLocation = eventLocation;
    }

    /**
     * A constructor that takes a parcel containing the calendar event.
     * <p>
     * Extracts in this order: name, date, details, location.
     *
     * @param in The parcel from which the new calendar event is extracted
     */
    protected CalendarEvent(@NonNull Parcel in) {
        eventName = in.readString();
        dateAndTime = (Date) in.readSerializable();
        eventDetails = in.readString();
        eventLocation = in.readString();
    }

    /**
     * A constructor that takes in a VEvent parsed from an iCal file using iCal4j.
     *
     * @param calendarVEvent The VEvent from which the new calendar event is created
     */
    public CalendarEvent(@NonNull VEvent calendarVEvent) {
        this(Optional.ofNullable(calendarVEvent.getSummary()).map(new Function<Summary, String>() { //TODO Should the name be required? Is it ever not provided?
                    @Override
                    public String apply(Summary summary) {
                        return summary.getValue();
                    }
                }).orElse(""),
                Optional.ofNullable(calendarVEvent.getStartDate()).map(new Function<DtStart, Date>() {
                    @Override
                    public Date apply(DtStart dtStart) {
                        return dtStart.getDate();
                    }
                }).orElseThrow(new Supplier<RuntimeException>() {
                    @Override
                    public RuntimeException get() {
                        return new IllegalArgumentException("Date required!");
                    }
                }),
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

    /**
     * Gets the name of the event.
     *
     * @return The name of the event. May be the empty string.
     */
    @NonNull
    public String getEventName() {
        return eventName;
    }

    /**
     * Gets the date of the event.
     *
     * @return The date and time fo the event.
     */
    @NonNull
    public Date getDateAndTime() {
        return dateAndTime;
    }

    /**
     * Gets the details of the event.
     *
     * @return The details of the event. May be the empty string.
     */
    @NonNull
    public String getEventDetails() {
        return eventDetails;
    }

    /**
     * Gets the name of the location of the event.
     *
     * @return The name of the location of the event. May be the empty string.
     */
    @NonNull
    public String getEventLocation() {
        return eventLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes in this order: name, date, details, location.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeSerializable(dateAndTime);
        dest.writeString(eventDetails);
        dest.writeString(eventLocation);
    }
}
