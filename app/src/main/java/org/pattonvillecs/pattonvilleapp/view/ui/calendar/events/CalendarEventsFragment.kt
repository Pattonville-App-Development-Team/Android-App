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

package org.pattonvillecs.pattonvilleapp.view.ui.calendar.events


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import dagger.android.support.DaggerFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import kotlinx.android.synthetic.main.calendar_event_tab_fast_scroller.*
import kotlinx.android.synthetic.main.calendar_event_tab_no_items_background.*
import kotlinx.android.synthetic.main.fragment_calendar_events.*
import org.jetbrains.anko.appcompat.v7.coroutines.onQueryTextListener
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.view.adapter.calendar.CalendarEventFlexibleAdapter
import org.pattonvillecs.pattonvilleapp.viewmodel.calendar.events.CalendarEventsFragmentViewModel
import org.pattonvillecs.pattonvilleapp.viewmodel.getViewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarEventsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * @author Mitchell Skaggs
 * @since 1.0.0
 */
class CalendarEventsFragment : DaggerFragment() {
    @Inject
    lateinit var calendarRepository: CalendarRepository

    private var firstInflationAfterCreation: Boolean = false

    private lateinit var viewModel: CalendarEventsFragmentViewModel

    private lateinit var eventAdapter: CalendarEventFlexibleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        firstInflationAfterCreation = savedInstanceState == null

        viewModel = getViewModel()
        viewModel.calendarRepository = calendarRepository
    }

    private fun goToCurrentDay() {
        var lastBeforeToday = -1
        val today = LocalDateTime.now().toLocalDate()
        Log.i(TAG, "Today is: ${DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(today)}")
        Log.i(TAG, "Found ${eventAdapter.itemCount} items")

        for (i in 0 until eventAdapter.itemCount) {
            val eventDate = eventAdapter.getItem(i)!!.startDate

            if (!eventDate.isBefore(today)) {
                lastBeforeToday = i - 1
                break
            }
        }

        event_recycler_view.post { event_recycler_view.scrollToPosition(lastBeforeToday.minus(1).coerceAtLeast(0)) }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_goto_today -> {
                goToCurrentDay()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_icon_menu, menu)
        inflater.inflate(R.menu.fragment_calendar_action_bar_menu_goto_today, menu)
        initSearchView(menu)
    }

    /**
     * Method to setup the search functionality of the list
     *
     *
     * Refer to the Flexible Adapter documentation, as this is a near replica implementation
     *
     * @param menu Menu object of current options menu
     */
    private fun initSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.menu_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.inputType = InputType.TYPE_TEXT_VARIATION_FILTER
            searchView.imeOptions = EditorInfo.IME_ACTION_DONE or EditorInfo.IME_FLAG_NO_FULLSCREEN
            searchView.queryHint = getString(R.string.action_search)
            searchView.onQueryTextListener {
                onQueryTextChange {
                    Log.d(TAG, "Search text: '$it'")
                    if (it != null)
                        viewModel.setSearchText(it)
                    true
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_calendar_events, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventAdapter = CalendarEventFlexibleAdapter(stableIds = true, calendarRepository = calendarRepository)
        eventAdapter.setDisplayHeadersAtStartUp(true)
        event_recycler_view.layoutManager = SmoothScrollLinearLayoutManager(context!!)
        event_recycler_view.addItemDecoration(
                FlexibleItemDecoration(context!!)
                        .withDefaultDivider()
                        .withDrawDividerOnLastItem(true))
        eventAdapter.setStickyHeaders(true)

        event_recycler_view.adapter = eventAdapter
        eventAdapter.fastScroller = fast_scroller
        fast_scroller.post { fast_scroller.bringToFront() }

        viewModel.eventItems.observe(this::getLifecycle) {
            if (it != null) {
                eventAdapter.updateDataSet(it.toList())
                eventAdapter.filterItems(100)
            }
        }
        viewModel.searchText.observe(this::getLifecycle) {
            Log.i(TAG, "Filtering based on '$it'")
            if (it != null) {
                eventAdapter.searchText = it
                eventAdapter.filterItems(100)
            }
        }
        viewModel.backgroundTextVisibility.observe(this::getLifecycle) {
            if (it != null)
                no_events_textview.visibility = it
        }

        //This is used to move to the current day ONCE, and never activate again during the life of the fragment. It must wait until the first update of data before running.
        eventAdapter.addListener(object : FlexibleAdapter.OnUpdateListener {
            internal var firstRun = true

            override fun onUpdateEmptyView(size: Int) {
                if (firstRun && size > 0 && firstInflationAfterCreation) {
                    firstRun = false
                    event_recycler_view.post { goToCurrentDay() }
                    eventAdapter.removeListener(FlexibleAdapter.OnUpdateListener::class.java)
                    Log.d(TAG, "Updated with initial data of size $size. Now removing this listener.")
                }
            }
        })
    }

    companion object {
        private const val TAG = "CalendarEventsFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment CalendarEventsFragment.
         */
        fun newInstance(): CalendarEventsFragment {
            return CalendarEventsFragment()
        }
    }
}// Required empty public constructor
