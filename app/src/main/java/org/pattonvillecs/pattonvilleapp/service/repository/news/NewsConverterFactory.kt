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

package org.pattonvillecs.pattonvilleapp.service.repository.news

import com.github.magneticflux.rss.createRssPersister
import com.github.magneticflux.rss.namespaces.standard.elements.Rss
import okhttp3.ResponseBody
import org.pattonvillecs.pattonvilleapp.DataSource
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by Mitchell Skaggs on 12/14/2017.
 *
 * @since 1.4.0
 */
object NewsConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        return when (type) {
            Rss::class.java -> RssConverter
            else -> super.responseBodyConverter(type, annotations, retrofit)
        }
    }

    override fun stringConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<*, String>? {
        return when (type) {
            DataSource::class.java -> DataSourceConverter
            else -> super.stringConverter(type, annotations, retrofit)
        }
    }

    private val persister = createRssPersister()

    object RssConverter : Converter<ResponseBody, Rss> {
        override fun convert(value: ResponseBody): Rss {
            return value.use {
                persister.read(Rss::class.java, it.charStream())
            }
        }
    }

    object DataSourceConverter : Converter<DataSource, String> {
        override fun convert(value: DataSource): String {
            return when (value) {
                DataSource.DISTRICT -> "District"
                DataSource.HIGH_SCHOOL -> "HighSchool"
                DataSource.HEIGHTS_MIDDLE_SCHOOL -> "Heights"
                DataSource.HOLMAN_MIDDLE_SCHOOL -> "Holman"
                DataSource.REMINGTON_TRADITIONAL_SCHOOL -> "Remington"
                DataSource.BRIDGEWAY_ELEMENTARY -> "Bridgeway"
                DataSource.DRUMMOND_ELEMENTARY -> "Drummond"
                DataSource.ROSE_ACRES_ELEMENTARY -> "RoseAcres"
                DataSource.PARKWOOD_ELEMENTARY -> "Parkwood"
                DataSource.WILLOW_BROOK_ELEMENTARY -> "WillowBrook"
                else -> throw IllegalArgumentException("newsURL must be present to download news feed! Did you filter by DataSources that have newsURLs?")
            }
        }
    }
}