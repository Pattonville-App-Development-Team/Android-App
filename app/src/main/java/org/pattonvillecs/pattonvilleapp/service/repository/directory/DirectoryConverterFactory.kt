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

package org.pattonvillecs.pattonvilleapp.service.repository.directory

import okhttp3.ResponseBody
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.service.model.directory.Directory
import org.pattonvillecs.pattonvilleapp.service.model.directory.Faculty
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.regex.Pattern

/**
 * Created by Mitchell Skaggs on 12/8/2017.
 */
object DirectoryConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, Directory>? =
            when (type) {
                Directory::class.java -> DirectoryConverter
                else -> null
            }

    object DirectoryConverter : Converter<ResponseBody, Directory> {
        override fun convert(value: ResponseBody): Directory {
            return Directory(
                    value.string()
                            .lineSequence()
                            .drop(1)
                            .filter { it.isNotEmpty() }
                            .map {
                                val columns = it.split(Pattern.compile("\\s*,"), 12)
                                Faculty(columns[0],
                                        columns[1],
                                        columns[2],
                                        columns[3],
                                        columns[4].nullIfEmpty()?.getDirectoryKeyForFaculty(),
                                        columns[5].nullIfEmpty(),
                                        columns[6].nullIfEmpty(),
                                        columns[7].nullIfEmpty(),
                                        columns[8].nullIfEmpty(),
                                        columns[9].nullIfEmpty(),
                                        columns[10].nullIfEmpty(),
                                        columns[11].nullIfEmpty())
                            }
                            .toList())
        }
    }
}

private fun String.nullIfEmpty(): String? {
    return if (this.isEmpty()) null else this
}


private fun String.getDirectoryKeyForFaculty(): DataSource? {
    return when (this) {
        "BRIDGEWAY ELEMENTARY" -> DataSource.BRIDGEWAY_ELEMENTARY
        "ROBERT DRUMMOND ELEMENTARY" -> DataSource.DRUMMOND_ELEMENTARY
        "HOLMAN MIDDLE SCHOOL" -> DataSource.HOLMAN_MIDDLE_SCHOOL
        "PATTONVILLE HEIGHTS" -> DataSource.HEIGHTS_MIDDLE_SCHOOL
        "PARKWOOD ELEMENTARY" -> DataSource.PARKWOOD_ELEMENTARY
        "ROSE ACRES ELEMENTARY" -> DataSource.ROSE_ACRES_ELEMENTARY
        "REMINGTON TRADITIONAL" -> DataSource.REMINGTON_TRADITIONAL_SCHOOL
        "PATTONVILLE HIGH SCHOOL", "POSITIVE SCHOOL" -> DataSource.HIGH_SCHOOL
        "WILLOW BROOK ELEMENTARY" -> DataSource.WILLOW_BROOK_ELEMENTARY
        "LEARNING CENTER" -> DataSource.DISTRICT
        else -> null
    }
}