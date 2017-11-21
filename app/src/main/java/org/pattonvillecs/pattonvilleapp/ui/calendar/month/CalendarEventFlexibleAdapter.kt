package org.pattonvillecs.pattonvilleapp.ui.calendar.month

import eu.davidea.flexibleadapter.FlexibleAdapter
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository

/**
 * Created by Mitchell Skaggs on 11/21/2017.
 */
class CalendarEventFlexibleAdapter(items: List<PinnableCalendarEventItem>? = null, listeners: Any? = null, stableIds: Boolean = false, val calendarRepository: CalendarRepository) : FlexibleAdapter<PinnableCalendarEventItem>(items, listeners, stableIds) {
}