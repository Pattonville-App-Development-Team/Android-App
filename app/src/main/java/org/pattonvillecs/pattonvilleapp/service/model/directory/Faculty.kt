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

package org.pattonvillecs.pattonvilleapp.service.model.directory

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import org.apache.commons.collections4.comparators.ComparatorChain
import org.pattonvillecs.pattonvilleapp.service.model.DataSource

/**
 * This class represents a faculty member.
 *
 * @since 1.3.0
 * @author Mitchell Skaggs
 */
@Entity(tableName = "faculty", primaryKeys = ["first_name", "last_name", "pcn"])
data class Faculty(
        @ColumnInfo(name = "first_name")
        val firstName: String,
        @ColumnInfo(name = "last_name")
        val lastName: String,
        @ColumnInfo(name = "pcn")
        val pcn: String,
        @ColumnInfo(name = "description")
        val description: String,
        @ColumnInfo(name = "location", index = true, collate = ColumnInfo.BINARY)
        val location: DataSource?,
        @ColumnInfo(name = "email")
        val email: String?,
        @ColumnInfo(name = "office_number_1")
        val officeNumber1: String? = null,
        @ColumnInfo(name = "extension_1")
        val extension1: String? = null,
        @ColumnInfo(name = "office_number_2")
        val officeNumber2: String? = null,
        @ColumnInfo(name = "extension_2")
        val extension2: String? = null,
        @ColumnInfo(name = "office_number_3")
        val officeNumber3: String? = null,
        @ColumnInfo(name = "extension_3")
        val extension3: String? = null) : Comparable<Faculty> {


    @delegate:Ignore
    val phoneNumbers: List<PhoneNumber> by lazy {
        val list = mutableListOf<PhoneNumber>()

        if (officeNumber1 != null)
            list += PhoneNumber(officeNumber1, extension1)

        if (officeNumber2 != null)
            list += PhoneNumber(officeNumber2, extension2)
        else if (extension2 != null && officeNumber1 != null)
            list += PhoneNumber(officeNumber1, extension2)

        if (officeNumber3 != null)
            list += PhoneNumber(officeNumber3, extension3)
        else if (extension3 != null && officeNumber2 != null)
            list += PhoneNumber(officeNumber2, extension3)
        else if (extension3 != null && officeNumber1 != null)
            list += PhoneNumber(officeNumber1, extension3)

        return@lazy list
    }

    @delegate:Ignore
    val rank: Int by lazy {
        when {
            pcn.contains("LCSPT") -> -2
            pcn.contains("LCASTSUP") -> -1
            pcn.contains("ADMSPR") or pcn.contains("ADMPRN") or pcn.contains("LCCFO") -> 0
            pcn.contains("ASCPRN") or pcn.contains("LCDR") -> 1
            pcn.contains("ADMAPR") or pcn.contains("DIR") -> 2
            pcn.contains("TCH") -> 3
            pcn.contains("EXSEC1") -> 4
            pcn.contains("EXSEC2") -> 5
            pcn.contains("GUISEC") -> 6
            pcn.contains("CONSEC") -> 7
            pcn.contains("SECRTY") -> 8
            else -> 9
        }
    }

    override fun compareTo(other: Faculty): Int {
        return NATURAL_ORDERING.compare(this, other)
    }

    companion object {
        val NATURAL_ORDERING = ComparatorChain<Faculty>(listOf(
                compareBy(nullsLast(DataSource.DEFAULT_ORDERING)) { it.location },
                compareBy { it.rank },
                compareBy { it.description },
                compareBy { it.lastName },
                compareBy { it.firstName }
        ))
    }
}