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

package org.pattonvillecs.pattonvilleapp.calendar


import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.*
import com.google.common.collect.Multiset
import com.google.firebase.crash.FirebaseCrash
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import dagger.android.support.AndroidSupportInjection
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_calendar_month.*
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.SpotlightHelper
import org.pattonvillecs.pattonvilleapp.SpotlightUtils
import org.pattonvillecs.pattonvilleapp.calendar.CalendarMonthFragment.Companion.TAG
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils
import org.pattonvillecs.pattonvilleapp.ui.calendar.month.CalendarEventFlexibleAdapter
import org.pattonvillecs.pattonvilleapp.ui.calendar.month.CalendarMonthFragmentViewModel
import org.pattonvillecs.pattonvilleapp.ui.calendar.month.PinnableCalendarEventItem
import org.pattonvillecs.pattonvilleapp.ui.calendar.month.SingleDayEventFlexibleViewModel
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.chrono.IsoChronology
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [CalendarMonthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarMonthFragment : Fragment() {

    @Inject
    lateinit var calendarRepository: CalendarRepository

    private var nestedScrollView: NestedScrollView? = null

    private lateinit var viewModel: CalendarMonthFragmentViewModel
    private lateinit var adapterViewModel: SingleDayEventFlexibleViewModel

    private lateinit var eventAdapter: FlexibleAdapter<PinnableCalendarEventItem>

    //Minimum radius of 5
    private val dotRadius: Float
        get() {
            return if (calendarView != null) {
                if (calendarView.width == 0 || calendarView.height == 0)
                    FirebaseCrash.logcat(Log.WARN, TAG, "Width and height are ${calendarView.width}, ${calendarView.height}! This method was called before the final layout pass!")

                (Math.min(calendarView.width, calendarView.height) / 125f).coerceAtLeast(5f)
            } else {
                FirebaseCrash.logcat(Log.WARN, TAG, "calendarView is null!")
                10f
            }
        }
    @delegate:ColorInt
    private val dotColor: Int by lazy { ContextCompat.getColor(context!!, R.color.colorPrimary) }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(CalendarMonthFragmentViewModel::class.java)
        adapterViewModel = ViewModelProviders.of(this).get(SingleDayEventFlexibleViewModel::class.java)

        viewModel.calendarRepository = calendarRepository
        adapterViewModel.calendarRepository = calendarRepository
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_calendar_action_bar_menu_goto_today, menu)

        SpotlightUtils.showSpotlightOnMenuItem(activity!!,
                R.id.action_goto_today,
                "CalendarMonthFragment_MenuButtonGoToCurrentDay",
                "Tap here to return to the current day.",
                "Today")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_goto_today -> true
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_calendar_month, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showSpotlight()

        eventAdapter = CalendarEventFlexibleAdapter(stableIds = true, calendarRepository = calendarRepository)
        event_recycler_view.layoutManager = SmoothScrollLinearLayoutManager(context!!)
        event_recycler_view.addItemDecoration(
                FlexibleItemDecoration(context!!)
                        .withDefaultDivider(R.layout.calendar_dateless_event_list_item)
                        .withDrawDividerOnLastItem(true))
        event_recycler_view.adapter = eventAdapter

        calendarView.selectedDate = adapterViewModel.getDate().toCalendarDay()
        calendarView.addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Log.d(TAG, "MCV New layout: $left $top $right $bottom; Old layout: $oldLeft $oldTop $oldRight $oldBottom")
            Log.d(TAG, "MCV tile height: ${calendarView.tileHeight}")

            if (left != oldLeft
                    || top != oldTop
                    || right != oldRight
                    || bottom != oldBottom)
                calendarView.post { calendarView.invalidateDecorators() }
        }
        calendarView.addDecorator(
                object : DayViewDecorator {

                    override fun shouldDecorate(day: CalendarDay): Boolean = when (day.day) {
                        1, 2 -> true
                        else -> false
                    }

                    override fun decorate(view: DayViewFacade) {
                        val triple = EnhancedDotSpan.createTripleWithPlus(dotRadius, dotColor, dotColor, dotColor)
                        view.addSpan(triple.first)
                        view.addSpan(triple.second)
                        view.addSpan(triple.third)
                    }
                })
        calendarView.setOnDateChangedListener { _, date, _ ->
            adapterViewModel.setDate(date.toLocalDate())
        }

        PreferenceUtils.getSelectedSchoolsLiveData(context!!).observe(this::getLifecycle) {
            if (it != null)
                adapterViewModel.setDataSources(it.toList())
        }

        viewModel.getDateMultiset(PreferenceUtils.getSharedPreferences(context!!)).observe(this::getLifecycle) { dates ->
            if (dates != null) {
                calendarView.removeDecorators()
                calendarView.addDecorators(
                        DotDayDecorator(dates, dotRadius, dotColor, EnhancedDotSpan.DotType.SINGLE),
                        DotDayDecorator(dates, dotRadius, dotColor, EnhancedDotSpan.DotType.DOUBLE),
                        DotDayDecorator(dates, dotRadius, dotColor, EnhancedDotSpan.DotType.TRIPLE),
                        DotDayDecorator(dates, dotRadius, dotColor, EnhancedDotSpan.DotType.TRIPLE_PLUS))
            }
        }

        adapterViewModel.liveItems.observe(this::getLifecycle) {
            if (it != null)
                eventAdapter.updateDataSet(it, true)
        }
    }

    private fun showSpotlight() {
        val spotlightPadding: Int
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                spotlightPadding = 20
                nestedScrollView = view as NestedScrollView?
                nestedScrollView!!.isSmoothScrollingEnabled = true
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                spotlightPadding = -250
                nestedScrollView = null
            }
            else -> throw IllegalStateException("Why would this ever happen?")
        }
        SpotlightHelper.showSpotlight(activity,
                view,
                spotlightPadding,
                "CalendarMonthFragment_SelectedDayEventList",
                "Events occurring on the selected day are shown here.",
                "Events")
    }

    companion object {

        val TAG = "CalendarMonthFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment CalendarMonthFragment.
         */
        @JvmStatic
        fun newInstance(): CalendarMonthFragment {
            Log.i(TAG, "New instance created...")
            return CalendarMonthFragment()
        }
    }
}

class DotDayDecorator(private val dates: Multiset<LocalDate>,
                      private val dotRadius: Float,
                      private val dotColor: Int,
                      private val dotType: EnhancedDotSpan.DotType,
                      private val testFunction: (Int) -> Boolean) : DayViewDecorator {

    constructor(dates: Multiset<LocalDate>,
                dotRadius: Float,
                dotColor: Int,
                dotType: EnhancedDotSpan.DotType) : this(dates, dotRadius, dotColor, dotType, dotType.defaultTestFunction)

    override fun shouldDecorate(day: CalendarDay): Boolean = testFunction(dates.count(day.toLocalDate()))

    override fun decorate(view: DayViewFacade) {
        val dotSpans = when (dotType) {
            EnhancedDotSpan.DotType.SINGLE -> listOf(EnhancedDotSpan.createSingle(dotRadius, dotColor))
            EnhancedDotSpan.DotType.DOUBLE -> EnhancedDotSpan.createDouble(dotRadius, dotColor, dotColor).toList()
            EnhancedDotSpan.DotType.TRIPLE -> EnhancedDotSpan.createTriple(dotRadius, dotColor, dotColor, dotColor).toList()
            EnhancedDotSpan.DotType.TRIPLE_PLUS -> EnhancedDotSpan.createTripleWithPlus(dotRadius, dotColor, dotColor, dotColor).toList()
        }
        dotSpans.forEach { view.addSpan(it) }
    }

}

private fun CalendarDay.toLocalDate(): LocalDate {
    var month = this.month + 1
    return try {
        LocalDate.of(year, month, day)
    } catch (exception: DateTimeException) {
        FirebaseCrash.logcat(Log.WARN, TAG, "Date parsing of $this failed!")
        FirebaseCrash.report(exception)

        month = month.coerceIn(1, 12)
        val day = day.coerceIn(1, Month.of(month).length(IsoChronology.INSTANCE.isLeapYear(year.toLong())))

        LocalDate.of(year, month, day)
    }
}

private fun LocalDate.toCalendarDay(): CalendarDay =
        CalendarDay.from(this.year, this.monthValue - 1, this.dayOfMonth)
