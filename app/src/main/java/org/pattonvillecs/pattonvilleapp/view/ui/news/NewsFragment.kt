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

package org.pattonvillecs.pattonvilleapp.view.ui.news

import android.os.Bundle
import android.support.v7.widget.SearchView
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import dagger.android.support.DaggerFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_news.*
import org.jetbrains.anko.appcompat.v7.coroutines.onQueryTextListener
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.service.repository.news.NewsRepository
import org.pattonvillecs.pattonvilleapp.service.repository.news.NewsSyncJobService
import org.pattonvillecs.pattonvilleapp.view.ui.spotlight.showSpotlightOnMenuItem
import org.pattonvillecs.pattonvilleapp.viewmodel.getViewModel
import org.pattonvillecs.pattonvilleapp.viewmodel.news.NewsFragmentViewModel
import javax.inject.Inject

/**
 * Fragment used within MainActivity to display the News tab
 *
 * @since 1.0.0
 */
class NewsFragment : DaggerFragment() {
    @Inject
    lateinit var firebaseJobDispatcher: FirebaseJobDispatcher

    @Inject
    lateinit var newsRepository: NewsRepository

    private lateinit var adapter: FlexibleAdapter<ArticleSummaryItem>

    private lateinit var viewModel: NewsFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle(R.string.title_fragment_news)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = getViewModel()
        viewModel.newsRepository = newsRepository
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_news, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FlexibleAdapter(null)

        news_recyclerView.layoutManager = SmoothScrollLinearLayoutManager(context!!)
        news_recyclerView.adapter = adapter
        // Adds item divider between elements
        news_recyclerView.addItemDecoration(
                FlexibleItemDecoration(context!!)
                        .withDefaultDivider())

        news_refreshLayout.setOnRefreshListener(this::refreshNewsData)
        news_refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)

        viewModel.articleItems.observe(this::getLifecycle) {
            if (it != null) {
                adapter.updateDataSet(it)
                adapter.filterItems(100)
                news_refreshLayout.isRefreshing = false
            }
        }
        viewModel.searchText.observe(this::getLifecycle) {
            Log.i(TAG, "Filtering based on '$it'")
            if (it != null) {
                adapter.searchText = it
                adapter.filterItems(100)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_news_menu_main, menu)
        initSearchView(menu)

        activity?.showSpotlightOnMenuItem(
                R.id.news_menu_refresh,
                "NewsFragment_MenuButtonRefresh",
                "Tap here to manually check for new news articles. The app periodically updates news on its own.",
                "Refresh")
        activity?.showSpotlightOnMenuItem(
                R.id.news_menu_search,
                "NewsFragment_MenuButtonSearch",
                "Tap here to search news titles and sources similar to your query.",
                "Search")
    }

    private fun refreshNewsData() {
        firebaseJobDispatcher.schedule(NewsSyncJobService.getInstantNewsSyncJob(firebaseJobDispatcher))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {

            R.id.news_menu_refresh -> refreshNewsData()
        }
        return true
    }

    /**
     * Method to setup the search functionality of the list
     *
     * @param menu Menu object of current options menu
     */
    private fun initSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.news_menu_search)
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

    companion object {

        private const val TAG = "NewsFragment"

        /**
         * Method that provides a new instance of NewsFragment
         *
         * @return A new NewsFragment
         */
        @JvmStatic
        fun newInstance(): NewsFragment {
            return NewsFragment()
        }
    }
}

