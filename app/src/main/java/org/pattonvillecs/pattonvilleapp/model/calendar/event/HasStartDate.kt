package org.pattonvillecs.pattonvilleapp.model.calendar.event

import org.threeten.bp.LocalDate

/**
 * This interface defines a property containing a local date, representing the start of something.
 *
 * @author Mitchell Skaggs
 * @since 1.2.0
 */
interface HasStartDate {
    val startDate: LocalDate
}