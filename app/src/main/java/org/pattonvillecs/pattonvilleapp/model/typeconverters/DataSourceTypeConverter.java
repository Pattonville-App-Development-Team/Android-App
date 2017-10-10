package org.pattonvillecs.pattonvilleapp.model.typeconverters;

import android.arch.persistence.room.TypeConverter;

import org.pattonvillecs.pattonvilleapp.DataSource;

/**
 * Created by Mitchell on 10/9/2017.
 */

public class DataSourceTypeConverter {
    @TypeConverter
    public static DataSource fromTimestamp(Integer value) {
        return value == null ? null : DataSource.values()[value];
    }

    @TypeConverter
    public static Integer dateToTimestamp(DataSource dataSource) {
        return dataSource == null ? null : dataSource.ordinal();
    }
}
