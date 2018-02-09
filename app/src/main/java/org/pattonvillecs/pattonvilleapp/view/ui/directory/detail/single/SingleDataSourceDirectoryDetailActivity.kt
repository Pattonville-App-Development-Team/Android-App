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

package org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.single

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import kotlinx.android.synthetic.main.activity_directory_single_datasource_detail.*
import org.jetbrains.anko.appcompat.v7.coroutines.onQueryTextListener
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.view.adapter.directory.FacultyAdapter
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.AbstractDirectoryDetailActivity
import org.pattonvillecs.pattonvilleapp.viewmodel.directory.detail.single.SingleDataSourceDirectoryDetailActivityViewModel
import org.pattonvillecs.pattonvilleapp.viewmodel.getViewModel

/**
 * Created by Mitchell Skaggs on 12/10/2017.
 */
class SingleDataSourceDirectoryDetailActivity : AbstractDirectoryDetailActivity() {

    lateinit var viewModel: SingleDataSourceDirectoryDetailActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_directory_single_datasource_detail)

        viewModel = getViewModel()

        viewModel.setDataSource(intent.getSerializableExtra("datasource") as DataSource)
        viewModel.directoryRepository = directoryRepository

        viewModel.title.observe(this::getLifecycle) {
            title = it
        }

        val facultyAdapter = FacultyAdapter(stableIds = true)
        faculty_recyclerview.adapter = facultyAdapter
        faculty_recyclerview.layoutManager = SmoothScrollLinearLayoutManager(this)
        faculty_recyclerview.addItemDecoration(
                FlexibleItemDecoration(this)
                        .withDefaultDivider()
                        .withDrawDividerOnLastItem(true))

        fast_scroller.setViewsToUse(
                R.layout.small_fast_scroller_layout,
                R.id.fast_scroller_bubble,
                R.id.fast_scroller_handle)
        facultyAdapter.fastScroller = fast_scroller

        viewModel.facultyItems.observe(this::getLifecycle) {
            Log.i(TAG, "Visible from update!")
            progress_bar.visibility = View.VISIBLE
            facultyAdapter.updateDataSet(it)
            facultyAdapter.filterItems(100L)
        }

        viewModel.searchText.observe(this::getLifecycle) {
            facultyAdapter.searchText = it
            if (facultyAdapter.hasSearchText()) {
                Log.i(TAG, "Visible from search with text $it!")
                progress_bar.visibility = View.VISIBLE
            }
            facultyAdapter.filterItems(100L)
        }

        facultyAdapter.addListener(FlexibleAdapter.OnUpdateListener {
            Log.i(TAG, "Count=$it")
            progress_bar.visibility = View.GONE
            Log.i(TAG, "Gone!")
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_icon_menu, menu)
        initSearchView(menu)
        return true
    }

    /**
     * Method to setup the search functionality of the list
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
                    viewModel.setSearchText(it)
                    true
                }
            }
        }
    }

    companion object {
        const val TAG: String = "SingleDataSourceDirecto"
        fun createIntent(context: Context, dataSource: DataSource): Intent =
                Intent(context, SingleDataSourceDirectoryDetailActivity::class.java).putExtra("datasource", dataSource)
    }
}
