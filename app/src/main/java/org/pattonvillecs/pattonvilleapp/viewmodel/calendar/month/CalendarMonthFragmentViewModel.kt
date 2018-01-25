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

package org.pattonvillecs.pattonvilleapp.viewmodel.calendar.month

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.common.collect.Multiset
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.PinnableCalendarEventItem
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.map
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.switchMap
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.zipTo
import org.pattonvillecs.pattonvilleapp.viewmodel.app
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Period
import org.threeten.bp.ZoneId

/**
 * This class is a ViewModel for a FlexibleAdapter that derives its source from a date and a set of DataSources.
 *
 * @since 1.2.0
 * @author Mitchell Skaggs
 */
class CalendarMonthFragmentViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var calendarRepository: CalendarRepository

    private val _selectedDate: MutableLiveData<LocalDate> = MutableLiveData<LocalDate>().apply { LocalDateTime.now().toLocalDate() }
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val selectedDataSources: LiveData<Set<DataSource>> by lazy { PreferenceUtils.getSelectedSchoolsLiveData(app) }

    val dateMultiset: LiveData<Multiset<LocalDate>> by lazy {
        selectedDataSources.switchMap {
            calendarRepository.getCountOnDays(it.toList())
        }
    }

    val currentDateEventItems: LiveData<List<PinnableCalendarEventItem>> by lazy {
        selectedDate.zipTo(selectedDataSources)
                .switchMap {
                    val startOfDay = it.first.atStartOfDay(ZoneId.systemDefault()).toInstant()
                    val endOfDay = startOfDay + Period.ofDays(1)

                    calendarRepository.getEventsBetweenDates(
                            it.second.toList(),
                            startOfDay,
                            endOfDay)
                }
                .map {
                    it.map { PinnableCalendarEventItem(it) }
                }
    }

    fun setDate(localDate: LocalDate) {
        _selectedDate.value = localDate
    }

    /*
    private val factory = PinnableCalendarEventItemFactory()


    init {
        identifier.value = SingleDayIdentifier(LocalDateTime.now().toLocalDate(), setOf())
    }

    override fun isSourceValid(source: List<PinnableCalendarEvent>?): Boolean = source != null

    override fun map(source: List<PinnableCalendarEvent>): MutableList<PinnableCalendarEventItem> {
        return FlexibleItemProvider
                .with(factory)
                .from(source)
    }

    override fun getSource(identifier: SingleDayIdentifier): LiveData<List<PinnableCalendarEvent>> =
            calendarRepository.getEventsBetweenDates(identifier.dataSources.toList(),
                    identifier.localDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    identifier.localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())


    class PinnableCalendarEventItemFactory : FlexibleItemProvider.Factory<PinnableCalendarEvent, PinnableCalendarEventItem> {
        override fun create(model: PinnableCalendarEvent): PinnableCalendarEventItem =
                FlexibleFactory.create(PinnableCalendarEventItem::class.java, model)
    }

    data class SingleDayIdentifier(val localDate: LocalDate, val dataSources: Set<DataSource>)

    fun setDate(localDate: LocalDate) {
        if (identifier.value?.localDate != localDate)
            identifier.value = identifier.value?.copy(localDate = localDate)
    }

    fun setDataSources(dataSources: Set<DataSource>) {
        if (identifier.value?.dataSources != dataSources)
            identifier.value = identifier.value?.copy(dataSources = dataSources)
    }

    fun getDate(): LocalDate = identifier.value!!.localDate

    fun getDateMultiset(context: Context): LiveData<Multiset<LocalDate>> {
        return PreferenceUtils.getSelectedSchoolsLiveData(context).switchMap { calendarRepository.getCountOnDays(it.toList()) }
    }*/
}
