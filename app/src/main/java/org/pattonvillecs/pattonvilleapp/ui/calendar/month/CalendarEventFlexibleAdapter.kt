package org.pattonvillecs.pattonvilleapp.ui.calendar.month

import eu.davidea.flexibleadapter.FlexibleAdapter
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.ui.calendar.HasCalendarRepository
import org.pattonvillecs.pattonvilleapp.ui.calendar.PinnableCalendarEventItem

/**
 * Created by Mitchell Skaggs on 11/21/2017.
 */
class CalendarEventFlexibleAdapter(items: List<PinnableCalendarEventItem>? = null, listeners: Any? = null, stableIds: Boolean = false, override val calendarRepository: CalendarRepository) : FlexibleAdapter<PinnableCalendarEventItem>(items, listeners, stableIds), HasCalendarRepository