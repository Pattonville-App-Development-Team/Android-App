package org.pattonvillecs.pattonvilleapp.ui.calendar

import android.view.View
import android.widget.TextView
import com.varunest.sparkbutton.SparkButton
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHolder
import eu.davidea.viewholders.FlexibleViewHolder
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.find
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.calendar.events.EventFlexibleItem.getDataSourcesSpannableStringBuilder
import org.pattonvillecs.pattonvilleapp.model.calendar.PinnableCalendarEvent
import org.pattonvillecs.pattonvilleapp.ui.calendar.PinnableCalendarEventItem.PinnableCalendarEventItemViewHolder
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

class PinnableCalendarEventItem @JvmOverloads constructor(private val pinnableCalendarEvent: PinnableCalendarEvent, header: DateHeader? = null) : AbstractSectionableItem<PinnableCalendarEventItemViewHolder, DateHeader>(header), IHolder<PinnableCalendarEvent> {
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
            if (buttonState) bg { holder.calendarRepository.pinEvent(pinnableCalendarEvent.calendarEvent) }
            else bg { holder.calendarRepository.unpinEvent(pinnableCalendarEvent.calendarEvent) }
        })
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
    }

    class PinnableCalendarEventItemViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>, stickyHeader: Boolean = false) : FlexibleViewHolder(view, adapter, stickyHeader), HasCalendarRepository by adapter as HasCalendarRepository {
        val topText = view.find<TextView>(R.id.text_top)
        val bottomText = view.find<TextView>(R.id.text_bottom)
        val shortSchoolName = view.find<TextView>(R.id.school_short_names)
        val sparkButton = view.find<SparkButton>(R.id.pinned_button)
    }
}