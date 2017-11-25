package org.pattonvillecs.pattonvilleapp.ui.calendar

import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository

/**
 *
 * @author Mitchell Skaggs
 * @since 1.2.0
 */
interface HasCalendarRepository {
    val calendarRepository: CalendarRepository
}