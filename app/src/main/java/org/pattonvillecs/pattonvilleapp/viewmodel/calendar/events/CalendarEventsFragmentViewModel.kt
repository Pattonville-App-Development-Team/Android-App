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

package org.pattonvillecs.pattonvilleapp.viewmodel.calendar.events

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.view.View
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.PinnableCalendarEvent
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.DateHeader
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.PinnableCalendarEventItem
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.map
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.switchMap
import org.pattonvillecs.pattonvilleapp.viewmodel.app
import org.threeten.bp.LocalDate

/**
 * This class is a ViewModel for a FlexibleAdapter that derives its source from a set of DataSources.
 *
 * @since 1.2.0
 * @author Mitchell Skaggs
 */
class CalendarEventsFragmentViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var calendarRepository: CalendarRepository

    private val _searchText: MutableLiveData<String> = MutableLiveData()
    val searchText: LiveData<String> = _searchText

    private val selectedDataSources: LiveData<Set<DataSource>> = PreferenceUtils.getSelectedSchoolsLiveData(app)

    private val events: LiveData<List<PinnableCalendarEvent>> by lazy { selectedDataSources.switchMap { calendarRepository.getEventsByDataSource(it.toList()) } }

    val eventItems: LiveData<List<PinnableCalendarEventItem>> by lazy {
        events.map {
            val headerMap = mutableMapOf<LocalDate, DateHeader>()
            it.map { PinnableCalendarEventItem(it, headerMap.getOrPut(it.calendarEvent.startDate, { DateHeader(it.calendarEvent.startDate) })) }
        }
    }

    fun setSearchText(text: String) {
        _searchText.value = text
    }

    val backgroundTextVisibility: LiveData<Int> by lazy { eventItems.map { if (it.isEmpty()) View.VISIBLE else View.INVISIBLE } }
}