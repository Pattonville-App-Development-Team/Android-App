package org.pattonvillecs.pattonvilleapp.fragments.calendar.fix;

import android.os.Parcel;
import android.os.Parcelable;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Created by Mitchell Skaggs on 1/6/17.
 *
 * @deprecated Will soon be unnecessary, Kryo handles nonserializable objects with ease.
 */

@Deprecated
public class SerializableCalendarDay implements Serializable, Parcelable {
    public static final Creator<SerializableCalendarDay> CREATOR = new Creator<SerializableCalendarDay>() {
        @Override
        public SerializableCalendarDay createFromParcel(Parcel in) {
            return new SerializableCalendarDay(in);
        }

        @Override
        public SerializableCalendarDay[] newArray(int size) {
            return new SerializableCalendarDay[size];
        }
    };
    private CalendarDay calendarDay;

    private SerializableCalendarDay() {
        this(CalendarDay.today());
    }

    private SerializableCalendarDay(CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
    }

    protected SerializableCalendarDay(Parcel in) {
        calendarDay = in.readParcelable(CalendarDay.class.getClassLoader());
    }

    public static SerializableCalendarDay of(CalendarDay calendarDay) {
        return new SerializableCalendarDay(calendarDay);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(calendarDay.getYear());
        out.writeInt(calendarDay.getMonth());
        out.writeInt(calendarDay.getDay());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int day = in.readInt();
        int month = in.readInt();
        int year = in.readInt();
        calendarDay = CalendarDay.from(year, month, day);
    }

    private void readObjectNoData() throws ObjectStreamException {
        calendarDay = CalendarDay.today();
    }

    public CalendarDay getCalendarDay() {
        return calendarDay;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerializableCalendarDay that = (SerializableCalendarDay) o;

        return calendarDay != null ? calendarDay.equals(that.calendarDay) : that.calendarDay == null;
    }

    @Override
    public int hashCode() {
        return calendarDay != null ? calendarDay.hashCode() : 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(calendarDay, flags);
    }
}
