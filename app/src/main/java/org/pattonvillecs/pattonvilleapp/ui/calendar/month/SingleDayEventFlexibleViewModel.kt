package org.pattonvillecs.pattonvilleapp.ui.calendar.month

import android.arch.lifecycle.LiveData
import eu.davidea.flexibleadapter.livedata.FlexibleFactory
import eu.davidea.flexibleadapter.livedata.FlexibleItemProvider
import eu.davidea.flexibleadapter.livedata.FlexibleViewModel
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.model.calendar.PinnableCalendarEvent
import org.pattonvillecs.pattonvilleapp.ui.calendar.month.SingleDayEventFlexibleViewModel.SingleDayIdentifier
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * Created by Mitchell Skaggs on 11/21/2017.
 */
class SingleDayEventFlexibleViewModel : FlexibleViewModel<List<PinnableCalendarEvent>, PinnableCalendarEventItem, SingleDayIdentifier>() {
    lateinit var calendarRepository: CalendarRepository
    private val factory = PinnableCalendarEventItemFactory()

    init {
        identifier.value = SingleDayIdentifier(LocalDateTime.now().toLocalDate(), listOf())
    }

    override fun isSourceValid(source: List<PinnableCalendarEvent>?): Boolean = source != null

    override fun map(source: List<PinnableCalendarEvent>): MutableList<PinnableCalendarEventItem> {
        return FlexibleItemProvider
                .with(factory)
                .from(source)
    }

    override fun getSource(identifier: SingleDayIdentifier): LiveData<List<PinnableCalendarEvent>> =
            calendarRepository.getEventsBetweenDates(identifier.dataSources,
                    identifier.localDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    identifier.localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

    class PinnableCalendarEventItemFactory : FlexibleItemProvider.Factory<PinnableCalendarEvent, PinnableCalendarEventItem> {
        override fun create(model: PinnableCalendarEvent): PinnableCalendarEventItem =
                FlexibleFactory.create(PinnableCalendarEventItem::class.java, model)
    }

    data class SingleDayIdentifier(val localDate: LocalDate, val dataSources: List<DataSource>)

    fun setDate(localDate: LocalDate) {
        if (identifier.value?.localDate != localDate)
            identifier.value = identifier.value?.copy(localDate = localDate)
    }

    fun setDataSources(dataSources: List<DataSource>) {
        if (identifier.value?.dataSources != dataSources)
            identifier.value = identifier.value?.copy(dataSources = dataSources)
    }
}
