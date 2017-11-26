package org.pattonvillecs.pattonvilleapp.ui.calendar.events

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import eu.davidea.flexibleadapter.livedata.FlexibleViewModel
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.model.calendar.event.PinnableCalendarEvent
import org.pattonvillecs.pattonvilleapp.ui.calendar.DateHeader
import org.pattonvillecs.pattonvilleapp.ui.calendar.PinnableCalendarEventItem
import org.pattonvillecs.pattonvilleapp.ui.calendar.zipLiveData
import org.threeten.bp.LocalDate

/**
 * Created by Mitchell Skaggs on 11/25/2017.
 */
class CalendarEventsFragmentViewModel : FlexibleViewModel<List<PinnableCalendarEvent>, PinnableCalendarEventItem, Set<DataSource>>() {
    lateinit var calendarRepository: CalendarRepository

    private val searchText: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }

    fun setSearchText(text: String) {
        searchText.value = text
    }

    fun getSearchTextString(): String? = searchText.value

    fun getEventsAndSearch() = zipLiveData(searchText, liveItems)

    override fun getSource(identifier: Set<DataSource>): LiveData<List<PinnableCalendarEvent>> {
        return calendarRepository.getEventsByDataSource(identifier.toList())
    }

    override fun isSourceValid(source: List<PinnableCalendarEvent>?): Boolean {
        return source != null
    }

    override fun map(source: List<PinnableCalendarEvent>): MutableList<PinnableCalendarEventItem> {
        val headerMap = mutableMapOf<LocalDate, DateHeader>()

        return source.map { PinnableCalendarEventItem(it, headerMap.getOrPut(it.calendarEvent.startDate, { DateHeader(it.calendarEvent.startDate) })) }.toMutableList()
    }

    fun getSearchText(): LiveData<String> = searchText
}