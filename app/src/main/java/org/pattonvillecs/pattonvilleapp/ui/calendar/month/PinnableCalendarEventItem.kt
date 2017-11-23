package org.pattonvillecs.pattonvilleapp.ui.calendar.month

import android.view.View
import android.widget.TextView
import com.varunest.sparkbutton.SparkButton
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import eu.davidea.viewholders.FlexibleViewHolder
import org.jetbrains.anko.coroutines.experimental.bg
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.calendar.events.EventFlexibleItem.getDataSourcesSpannableStringBuilder
import org.pattonvillecs.pattonvilleapp.model.calendar.PinnableCalendarEvent
import org.pattonvillecs.pattonvilleapp.ui.calendar.month.PinnableCalendarEventItem.PinnableCalendarEventItemViewHolder
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.*

/**
 * Created by Mitchell Skaggs on 11/21/2017.
 */
data class PinnableCalendarEventItem(private val pinnableCalendarEvent: PinnableCalendarEvent) : AbstractFlexibleItem<PinnableCalendarEventItemViewHolder>(), IHolder<PinnableCalendarEvent> {
    override fun getModel(): PinnableCalendarEvent = pinnableCalendarEvent

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): PinnableCalendarEventItemViewHolder =
            PinnableCalendarEventItemViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.calendar_dateless_event_list_item

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: PinnableCalendarEventItemViewHolder, position: Int, payloads: MutableList<Any?>?) {


        holder.topText.text = pinnableCalendarEvent.calendarEvent.summary

        holder.bottomText.text = FORMATTER.format(pinnableCalendarEvent.calendarEvent.startDate)

        holder.shortSchoolName.text = getDataSourcesSpannableStringBuilder(pinnableCalendarEvent.dataSources.sortedWith(DataSource.DEFAULT_ORDERING), adapter.recyclerView.context)

        holder.sparkButton.isChecked = pinnableCalendarEvent.pinned
        holder.sparkButton.setEventListener({ _, buttonState ->
            bg {
                when (buttonState) {
                    true -> holder.adapter.calendarRepository.pinEvent(pinnableCalendarEvent.calendarEvent)
                    false -> holder.adapter.calendarRepository.unpinEvent(pinnableCalendarEvent.calendarEvent)
                }
            }
        })
    }

    companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())
    }

    class PinnableCalendarEventItemViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>, stickyHeader: Boolean = false) : FlexibleViewHolder(view, adapter, stickyHeader) {
        val topText = view.findViewById<TextView>(R.id.text_top)!!
        val bottomText = view.findViewById<TextView>(R.id.text_bottom)!!
        val shortSchoolName = view.findViewById<TextView>(R.id.school_short_names)!!
        val sparkButton = view.findViewById<SparkButton>(R.id.pinned_button)!!
        val adapter = adapter as CalendarEventFlexibleAdapter
    }
}