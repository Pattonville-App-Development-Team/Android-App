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

package org.pattonvillecs.pattonvilleapp.ui.calendar.month

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import com.google.common.collect.Multiset
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils
import org.threeten.bp.LocalDate

/**
 * Created by Mitchell Skaggs on 11/13/2017.
 */
class CalendarMonthFragmentViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var calendarRepository: CalendarRepository

    fun getDateMultiset(context: Context): LiveData<Multiset<LocalDate>> {
        return Transformations.switchMap(
                PreferenceUtils.getSelectedSchoolsLiveData(context),
                { calendarRepository.getCountOnDays(it.toList()) })
    }
}