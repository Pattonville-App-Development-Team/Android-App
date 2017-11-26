package org.pattonvillecs.pattonvilleapp.model.calendar.event

import org.threeten.bp.LocalDate

/**
 * This interface defines a property containing a local date, representing the end of something.
 *
 * @author Mitchell Skaggs
 * @since 1.2.0
 */
interface HasEndDate {
    val endDate: LocalDate
}