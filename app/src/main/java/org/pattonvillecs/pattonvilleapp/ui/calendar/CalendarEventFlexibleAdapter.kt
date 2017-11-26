/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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