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

package org.pattonvillecs.pattonvilleapp.service.repository.calendar

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import okhttp3.ResponseBody
import org.pattonvillecs.pattonvilleapp.DataSource
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.StringReader
import java.lang.reflect.Type

/**
 * Created by Mitchell Skaggs on 11/20/2017.
 */
object CalendarConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, Calendar>? =
            when (type) {
                Calendar::class.java -> CalendarConverter
                else -> null
            }

    override fun stringConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, String>? =
            when (type) {
                DataSource::class.java -> DataSourceConverter
                else -> null
            }

    object CalendarConverter : Converter<ResponseBody, Calendar> {
        override fun convert(value: ResponseBody): Calendar =
                CalendarBuilder().build(StringReader(fixICalStrings(value.string())))
    }

    object DataSourceConverter : Converter<DataSource, String> {
        override fun convert(value: DataSource): String =
                value.calendarURL.orElseThrow { IllegalArgumentException("calendarURL must be present to download calendar! Did you filter by DataSources that have calendars?") }
    }

    private const val LINEBREAK_MATCHER = "(?:\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029])"

    private fun fixICalStrings(iCalString: String): String {
        return iCalString
                .replace(("X-APPLE-TRAVEL-ADVISORY-BEHAVIOR;ACKNOWLEDGED=(\\d+)T(\\d+)Z:AUTOMATIC" + LINEBREAK_MATCHER + "BEGIN:VEVENT").toRegex(), "X-APPLE-TRAVEL-ADVISORY-BEHAVIOR;ACKNOWLEDGED=$1T$2Z:AUTOMATIC" + LINEBREAK_MATCHER + "END:VEVENT" + LINEBREAK_MATCHER + "BEGIN:VEVENT")
                .replace("FREQ=;", "FREQ=YEARLY;")
                .replace("DTSTART;VALUE=DATE-TIME:", "DTSTART;TZID=US/Central:")
                .replace("DTEND;VALUE=DATE-TIME:", "DTEND;TZID=US/Central:")
    }
}