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

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.support.v4.content.res.ResourcesCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.TextView
import com.annimon.stream.Collector
import com.annimon.stream.Stream
import com.annimon.stream.function.BiConsumer
import com.annimon.stream.function.Function
import com.annimon.stream.function.Supplier
import com.varunest.sparkbutton.SparkButton
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import eu.davidea.viewholders.FlexibleViewHolder
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.ICalendarEvent
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.PinnableCalendarEvent
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.PinnableCalendarEventItem.PinnableCalendarEventItemViewHolder
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.details.CalendarEventDetailsActivity
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.fix.CustomTypefaceSpan
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.*

/**
 * This class is an [IFlexible] that contains a pinnable calendar event. It must be put in an adapter that implements [HasCalendarRepository].
 *
 * @author Mitchell Skaggs
 * @since 1.2.0
 */

class PinnableCalendarEventItem @JvmOverloads constructor(private val pinnableCalendarEvent: PinnableCalendarEvent, header: DateHeader? = null) : AbstractSectionableItem<PinnableCalendarEventItemViewHolder, DateHeader>(header), IHolder<PinnableCalendarEvent>, IFilterable, ICalendarEvent by pinnableCalendarEvent, IFlexibleHasStartDateHasEndDate<PinnableCalendarEventItemViewHolder> {
    override fun filter(constraint: String?): Boolean {
        if (constraint == null || constraint.isBlank())
            return true

        val lowerCaseConstraint = constraint.toLowerCase()
        val summaryRatio = FuzzySearch.partialRatio(lowerCaseConstraint, pinnableCalendarEvent.calendarEvent.summary.toLowerCase())
        val dataSourceRatio = FuzzySearch.partialRatio(lowerCaseConstraint, pinnableCalendarEvent.dataSources.toString().toLowerCase())
        return summaryRatio > 80 || dataSourceRatio > 80
    }

    override fun getModel(): PinnableCalendarEvent = pinnableCalendarEvent

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): PinnableCalendarEventItemViewHolder =
            PinnableCalendarEventItemViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.calendar_dateless_event_list_item

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: PinnableCalendarEventItemViewHolder, position: Int, payloads: MutableList<Any?>?) {

        holder.topText.text = pinnableCalendarEvent.calendarEvent.summary

        holder.bottomText.text = FORMATTER.format(pinnableCalendarEvent.calendarEvent.startDateTime)

        holder.shortSchoolName.text = getDataSourcesSpannableStringBuilder(pinnableCalendarEvent.dataSources.sortedWith(DataSource.DEFAULT_ORDERING), adapter.recyclerView.context)

        holder.sparkButton.isChecked = pinnableCalendarEvent.pinned
        holder.sparkButton.setEventListener({ _, buttonState ->
            if (buttonState) bg { holder.calendarRepository.pinEvent(pinnableCalendarEvent.calendarEvent) }
            else bg { holder.calendarRepository.unpinEvent(pinnableCalendarEvent.calendarEvent) }
        })

        holder.view.onClick {
            it?.let {
                val activity = getActivity(it)
                activity.startActivity(CalendarEventDetailsActivity.createIntent(activity, uid))
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PinnableCalendarEventItem

        if (pinnableCalendarEvent != other.pinnableCalendarEvent) return false

        return true
    }

    override fun hashCode(): Int = pinnableCalendarEvent.hashCode()

    companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())

        @JvmStatic
        fun getActivity(view: View): Activity {
            return getActivity(view.context)
        }

        @JvmStatic
        fun getActivity(context: Context): Activity {
            var c = context
            while (c is ContextWrapper) {
                if (c is Activity) {
                    return c
                }
                c = c.baseContext
            }
            throw IllegalStateException("Activity not found!")
        }

        @JvmStatic
        fun getDataSourcesSpannableStringBuilder(dataSources: Collection<DataSource>, context: Context): SpannableStringBuilder {
            return Stream.of(dataSources).map { dataSource ->
                // \uf111 is a circle in Font Awesome. Supported on all devices
                // \u00A0 is a non-breaking space, which prevents word wrap between the circle and name
                val spannableString = SpannableString(("\uf111 " + dataSource.shortName).replace(' ', '\u00A0'))

                spannableString.apply {
                    setSpan(ForegroundColorSpan(dataSource.calendarColor), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(RelativeSizeSpan(1.25f), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.fontawesome_webfont)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }.collect(object : Collector<SpannableString, SpannableStringBuilder, SpannableStringBuilder> {
                override fun supplier(): Supplier<SpannableStringBuilder> {
                    return Supplier { SpannableStringBuilder() }
                }

                override fun accumulator(): BiConsumer<SpannableStringBuilder, SpannableString> {
                    return BiConsumer { value1, value2 -> value1.append(value2).append(", ") }
                }

                override fun finisher(): Function<SpannableStringBuilder, SpannableStringBuilder> {
                    return Function { spannableStringBuilder -> spannableStringBuilder.delete(spannableStringBuilder.length - 2, spannableStringBuilder.length) }
                }
            })
        }
    }

    class PinnableCalendarEventItemViewHolder(val view: View, adapter: FlexibleAdapter<out IFlexible<*>>, stickyHeader: Boolean = false) : FlexibleViewHolder(view, adapter, stickyHeader), HasCalendarRepository by adapter as HasCalendarRepository {
        val topText = view.find<TextView>(R.id.text_top)
        val bottomText = view.find<TextView>(R.id.text_bottom)
        val shortSchoolName = view.find<TextView>(R.id.school_short_names)
        val sparkButton = view.find<SparkButton>(R.id.pinned_button)
    }
}