package org.pattonvillecs.pattonvilleapp.ui.calendar.pinned

import android.arch.lifecycle.LiveData
import eu.davidea.flexibleadapter.livedata.FlexibleViewModel
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.model.calendar.PinnableCalendarEvent
import org.pattonvillecs.pattonvilleapp.ui.calendar.DateHeader
import org.pattonvillecs.pattonvilleapp.ui.calendar.PinnableCalendarEventItem
import org.threeten.bp.LocalDate

/**
 * This class is a ViewModel for a FlexibleAdapter that derives its source from a set of DataSources.
 *
 * @since 1.2.0
 * @author Mitchell Skaggs
 */
class CalendarPinnedFragmentViewModel : FlexibleViewModel<List<PinnableCalendarEvent>, PinnableCalendarEventItem, Set<DataSource>>() {

    lateinit var calendarRepository: CalendarRepository

    override fun getSource(identifier: Set<DataSource>): LiveData<List<PinnableCalendarEvent>> {
        return calendarRepository.getPinnedEvents(identifier.toList())
    }

    override fun isSourceValid(source: List<PinnableCalendarEvent>?): Boolean {
        return source != null
    }

    override fun map(source: List<PinnableCalendarEvent>): MutableList<PinnableCalendarEventItem> {
        val headerMap = mutableMapOf<LocalDate, DateHeader>()

        return source.map { PinnableCalendarEventItem(it, headerMap.getOrPut(it.calendarEvent.startDay, { DateHeader(it.calendarEvent.startDay) })) }.toMutableList()
    }
}