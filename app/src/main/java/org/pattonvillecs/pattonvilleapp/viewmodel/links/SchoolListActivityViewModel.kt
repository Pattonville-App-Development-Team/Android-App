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

package org.pattonvillecs.pattonvilleapp.viewmodel.links

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.service.model.links.SchoolListType
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.map
import org.pattonvillecs.pattonvilleapp.view.ui.links.NutrisliceItem
import org.pattonvillecs.pattonvilleapp.view.ui.links.PeachjarItem
import org.pattonvillecs.pattonvilleapp.view.ui.links.SchoolLinkItem

/**
 * @author Mitchell Skaggs
 * @since 1.2.0
 */
class SchoolListActivityViewModel : ViewModel() {
    private val schoolListType = MutableLiveData<SchoolListType>()
    val titleStringResource: LiveData<Int?> = schoolListType.map { type: SchoolListType ->
        when (type) {
            SchoolListType.PEACHJAR -> R.string.title_activity_peachjar
            SchoolListType.NUTRISLICE -> R.string.title_activity_nutrislice
        }
    }
    val schoolLinkItems: LiveData<List<SchoolLinkItem>> = schoolListType.map { type: SchoolListType ->
        when (type) {
            SchoolListType.PEACHJAR -> DataSource.PEACHJAR
            SchoolListType.NUTRISLICE -> DataSource.NUTRISLICE
        }
                .sortedWith(DataSource.DEFAULT_ORDERING)
                .map {
                    when (type) {
                        SchoolListType.PEACHJAR -> PeachjarItem(it)
                        SchoolListType.NUTRISLICE -> NutrisliceItem(it)
                    }
                }
    }

    fun setSchoolListType(type: SchoolListType) {
        schoolListType.value = type
    }
}