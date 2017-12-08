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

package org.pattonvillecs.pattonvilleapp.viewmodel.calendar.pinned

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.PinnableCalendarEvent
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.*
import org.pattonvillecs.pattonvilleapp.viewmodel.app
import org.threeten.bp.LocalDate

/**
 * This class is a ViewModel for a FlexibleAdapter that derives its source from a set of DataSources.
 *
 * @since 1.2.0
 * @author Mitchell Skaggs
 */
class CalendarPinnedFragmentViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var calendarRepository: CalendarRepository

    private val selectedDataSources: LiveData<Set<DataSource>> = PreferenceUtils.getSelectedSchoolsLiveData(app)

    private val pinnedEvents: LiveData<List<PinnableCalendarEvent>> by lazy { selectedDataSources.switchMap { calendarRepository.getPinnedEvents(it.toList()) } }

    val backgroundText: LiveData<String> by lazy {
        pinnedEvents.mapNullable {
            when {
                it == null || it.isEmpty() -> app.getString(R.string.no_pinned_events_provided_message)
                else -> ""
            }
        }
    }

    val pinnedEventItems: LiveData<List<PinnableCalendarEventItem>> by lazy {
        pinnedEvents.map {
            val headerMap = mutableMapOf<LocalDate, DateHeader>()
            it.map { PinnableCalendarEventItem(it, headerMap.getOrPut(it.calendarEvent.startDate, { DateHeader(it.calendarEvent.startDate) })) }
        }
    }
}