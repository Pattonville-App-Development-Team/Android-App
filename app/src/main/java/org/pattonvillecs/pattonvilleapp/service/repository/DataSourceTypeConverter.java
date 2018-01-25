/*
 * Copyright (C) 2017 - 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pattonvillecs.pattonvilleapp.service.repository;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;

import org.pattonvillecs.pattonvilleapp.DataSource;

/**
 * Created by Mitchell on 10/9/2017.
 */

public class DataSourceTypeConverter {

    @TypeConverter
    @Nullable
    public static DataSource fromIndex(@Nullable Integer value) {
        return value == null ? null : DataSource.values()[value];
    }

    @TypeConverter
    @Nullable
    public static Integer dataSourceToIndex(@Nullable DataSource dataSource) {
        return dataSource == null ? null : dataSource.ordinal();
    }
}
