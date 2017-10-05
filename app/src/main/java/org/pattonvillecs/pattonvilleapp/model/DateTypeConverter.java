package org.pattonvillecs.pattonvilleapp.model;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;


/**
 * Created by Mitchell on 10/4/2017.
 */

public class DateTypeConverter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
