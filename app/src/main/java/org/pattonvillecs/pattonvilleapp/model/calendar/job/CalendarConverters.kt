/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.model.calendar.job

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import okhttp3.ResponseBody
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.calendar.data.RetrieveCalendarDataAsyncTask.fixICalStrings
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.StringReader
import java.lang.reflect.Type

/**
 * Created by Mitchell Skaggs on 11/20/2017.
 */
object CalendarConverters {

    class CalendarConverter : Converter<ResponseBody, Calendar> {
        override fun convert(value: ResponseBody): Calendar =
                CalendarBuilder().build(StringReader(fixICalStrings(value.string())))
    }

    class DataSourceConverter : Converter<DataSource, String> {
        override fun convert(value: DataSource): String =
                value.calendarURL.orElseThrow { IllegalStateException("calendarURL must be present to download calendar! Did you filter by DataSources that have calendars?") }
    }

    @JvmField
    val FACTORY: Converter.Factory = object : Converter.Factory() {
        override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, Calendar>? =
                when (type) {
                    Calendar::class.java -> CalendarConverter()
                    else -> null
                }

        override fun stringConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, String>? =
                when (type) {
                    DataSource::class.java -> DataSourceConverter()
                    else -> null
                }
    }
}