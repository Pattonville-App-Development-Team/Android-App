package org.pattonvillecs.pattonvilleapp.ui.calendar

import android.view.View
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.jetbrains.anko.find
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.ui.calendar.DateHeader.DateHeaderViewHolder
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

/**
 * Created by Mitchell Skaggs on 11/23/2017.
 */
class DateHeader(private val localDate: LocalDate) : AbstractHeaderItem<DateHeaderViewHolder>(), IFlexibleHasStartDateHasEndDate<DateHeaderViewHolder> {
    override val endDate: LocalDate get() = localDate
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