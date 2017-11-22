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
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.*
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.pattonvillecs.pattonvilleapp.MainActivity
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.ui.calendar.CalendarFragmentViewModel
import javax.inject.Inject

/**
 * This Fragment manages the three calendar views and their connection to the tab bar. It also contains the common refresh menu functionality.
 *
 * @author Mitchell Skaggs
 * @since 1.0.0
 */
class CalendarFragment : DaggerFragment() {
    private var tabLayout: TabLayout? = null
    private lateinit var viewModel: CalendarFragmentViewModel

    @field:Inject
    lateinit var firebaseJobDispatcher: FirebaseJobDispatcher

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh -> {
            viewModel.refreshCalendar()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_calendar_action_bar_menu_main, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "onActivityCreated called")
        activity?.setTitle(R.string.title_fragment_calendar)

        tabLayout = (activity as MainActivity?)?.tabLayout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate called")
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this).get(CalendarFragmentViewModel::class.java)

        viewModel.firebaseJobDispatcher = firebaseJobDispatcher

        viewModel.currentPage.observe(this::getLifecycle) { page -> page?.let { pager_calendar.setCurrentItem(it, false) } }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart called")

        tabLayout?.visibility = View.VISIBLE
        tabLayout?.setupWithViewPager(pager_calendar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_calendar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager_calendar.offscreenPageLimit = 2
        pager_calendar.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            internal val fragments = listOf(
                    CalendarMonthFragment.newInstance(),
                    CalendarEventsFragment.newInstance(),
                    CalendarPinnedFragment.newInstance()
            )

            override fun getItem(position: Int): Fragment = fragments[position]

            override fun getPageTitle(position: Int): CharSequence = when (position) {
                0 -> "Month"
                1 -> "Events"
                2 -> "Pinned"
                else -> throw IllegalStateException("Invalid position!")
            }

            override fun getCount(): Int = fragments.size
        }

        pager_calendar.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentPage(position)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")

        tabLayout?.visibility = View.GONE
        tabLayout?.setupWithViewPager(null)
    }

    companion object {

        private val TAG = "CalendarFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment CalendarFragment.
         */
        @JvmStatic
        fun newInstance(): CalendarFragment = CalendarFragment()
    }
}
