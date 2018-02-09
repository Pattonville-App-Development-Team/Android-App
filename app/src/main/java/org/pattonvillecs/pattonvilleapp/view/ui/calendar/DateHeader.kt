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

package org.pattonvillecs.pattonvilleapp.view.ui.calendar

import android.view.View
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.jetbrains.anko.find
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.DateHeader.DateHeaderViewHolder
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

/**
 * Created by Mitchell Skaggs on 11/23/2017.
 */
class DateHeader(private val localDate: LocalDate) : AbstractHeaderItem<DateHeaderViewHolder>(), IFlexibleHasStartDate<DateHeaderViewHolder> {
    override val startDate: LocalDate get() = localDate

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): DateHeaderViewHolder =
            DateHeaderViewHolder(view, adapter, true)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: DateHeaderViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.dateText.text = FORMATTER.format(localDate)
    }

    override fun getLayoutRes(): Int = R.layout.calendar_event_date_header
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateHeader

        if (localDate != other.localDate) return false

        return true
    }

    override fun hashCode(): Int = localDate.hashCode()

    companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    }

    class DateHeaderViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>, stickyHeader: Boolean = false) : FlexibleViewHolder(view, adapter, stickyHeader) {
        val dateText = view.find<TextView>(R.id.calendar_event_header_text)
    }
}