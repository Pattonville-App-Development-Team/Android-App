package org.pattonvillecs.pattonvilleapp.ui.calendar

import android.support.v7.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.model.calendar.event.HasEndDate
import org.pattonvillecs.pattonvilleapp.model.calendar.event.HasStartDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

/**
 * This adapter must have items that extend [IFlexible], [HasStartDate], and [HasEndDate].
 *
 * @author Mitchell Skaggs
 * @since 1.2.0
 */
class CalendarEventFlexibleAdapter(items: List<PinnableCalendarEventItem>? = null, listeners: Any? = null, stableIds: Boolean = false, override val calendarRepository: CalendarRepository) : FlexibleAdapter<IFlexibleHasStartDateHasEndDate<out RecyclerView.ViewHolder>>(items, listeners, stableIds), HasCalendarRepository {
    override fun onCreateBubbleText(position: Int): String {
        val item = getItem(position) ?: return ""
        return FORMATTER.format(item.startDate)
    }

    companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    }
}