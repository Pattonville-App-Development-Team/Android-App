/*
 * Copyright (C) 2017 - 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.view.ui.home

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.service.repository.news.NewsRepository
import org.pattonvillecs.pattonvilleapp.view.adapter.calendar.CalendarEventFlexibleAdapter
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.CalendarFragment
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.pinned.CalendarPinnedFragment
import org.pattonvillecs.pattonvilleapp.view.ui.news.ArticleSummaryItem
import org.pattonvillecs.pattonvilleapp.view.ui.news.NewsFragment
import org.pattonvillecs.pattonvilleapp.viewmodel.getViewModel
import org.pattonvillecs.pattonvilleapp.viewmodel.home.HomeFragmentViewModel
import javax.inject.Inject


class HomeFragment : DaggerFragment() {
    @Inject
    lateinit var picasso: Picasso

    @Inject
    lateinit var calendarRepository: CalendarRepository

    @Inject
    lateinit var newsRepository: NewsRepository

    private lateinit var viewModel: HomeFragmentViewModel

    private lateinit var allEventsAdapter: CalendarEventFlexibleAdapter
    private lateinit var pinnedEventsAdapter: CalendarEventFlexibleAdapter
    private lateinit var newsArticlesAdapter: FlexibleAdapter<ArticleSummaryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()

        viewModel.calendarRepository = calendarRepository
        viewModel.newsRepository = newsRepository
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle(R.string.title_fragment_home)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.carouselVisiblePreference.observe(this::getLifecycle) {
            if (it == true)
                carousel.visibility = View.VISIBLE
            else if (it == false)
                carousel.visibility = View.GONE
        }
        carousel.setImageListener { position, imageView ->
            picasso.load("http://moodle.psdr3.org/psdlogin/backgrounds/bg$position.jpg")
                    .fit()
                    .centerCrop()
                    .into(imageView)
        }
        carousel.pageCount = 8

        viewModel.homeNewsAmountPreference.observe(this::getLifecycle) {
            it?.let {
                when {
                    it > 0 -> {
                        news_header.visibility = View.VISIBLE
                        news_see_more.visibility = View.VISIBLE
                    }
                    else -> {
                        news_header.visibility = View.GONE
                        news_see_more.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.homeCalendarAmountPreference.observe(this::getLifecycle) {
            it?.let {
                when {
                    it > 0 -> {
                        all_events_header.visibility = View.VISIBLE
                        all_events_see_more.visibility = View.VISIBLE
                    }
                    else -> {
                        all_events_header.visibility = View.GONE
                        all_events_see_more.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.homePinnedAmountPreference.observe(this::getLifecycle) {
            it?.let {
                when {
                    it > 0 -> {
                        pinned_events_header.visibility = View.VISIBLE
                        pinned_events_see_more.visibility = View.VISIBLE
                    }
                    else -> {
                        pinned_events_header.visibility = View.GONE
                        pinned_events_see_more.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.recentCalendarEvents.observe(this::getLifecycle) {
            it?.let {
                allEventsAdapter.updateDataSet(it, true)
            }
        }

        viewModel.recentPinnedCalendarEvents.observe(this::getLifecycle) {
            it?.let {
                pinnedEventsAdapter.updateDataSet(it, true)
            }
        }

        viewModel.newsArticles.observe(this::getLifecycle) {
            it?.let {
                newsArticlesAdapter.updateDataSet(it, true)
            }
        }

        newsArticlesAdapter = FlexibleAdapter(null, null, true)
        news_recyclerview.layoutManager = SmoothScrollLinearLayoutManager(context!!)
        news_recyclerview.addItemDecoration(
                FlexibleItemDecoration(context!!)
                        .withDefaultDivider()
                        .withDrawDividerOnLastItem(true))
        news_recyclerview.isNestedScrollingEnabled = false
        news_recyclerview.adapter = newsArticlesAdapter

        allEventsAdapter = CalendarEventFlexibleAdapter(stableIds = true, calendarRepository = calendarRepository)
        all_events_recyclerview.layoutManager = SmoothScrollLinearLayoutManager(context!!)
        all_events_recyclerview.addItemDecoration(
                FlexibleItemDecoration(context!!)
                        .withDefaultDivider()
                        .withDrawDividerOnLastItem(true))
        all_events_recyclerview.isNestedScrollingEnabled = false
        all_events_recyclerview.adapter = allEventsAdapter

        pinnedEventsAdapter = CalendarEventFlexibleAdapter(stableIds = true, calendarRepository = calendarRepository)
        pinned_events_recyclerview.layoutManager = SmoothScrollLinearLayoutManager(context!!)
        pinned_events_recyclerview.addItemDecoration(
                FlexibleItemDecoration(context!!)
                        .withDefaultDivider()
                        .withDrawDividerOnLastItem(true))
        pinned_events_recyclerview.isNestedScrollingEnabled = false
        pinned_events_recyclerview.adapter = pinnedEventsAdapter

        val listener: (View) -> Unit = { v: View ->
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.content_default, when (v.id) {
                        R.id.news_see_more -> NewsFragment.newInstance()
                        R.id.all_events_see_more -> CalendarFragment.newInstance()
                        R.id.pinned_events_see_more -> CalendarPinnedFragment.newInstance()
                        else -> throw IllegalStateException("Invalid view ID clicked! ID: ${v.id}, View: $v")
                    })
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    ?.commit()
        }

        news_see_more.setOnClickListener(listener)
        all_events_see_more.setOnClickListener(listener)
        pinned_events_see_more.setOnClickListener(listener)
    }

    companion object {

        @JvmStatic
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
