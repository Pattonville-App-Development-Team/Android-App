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

package org.pattonvillecs.pattonvilleapp.view.ui.links

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import kotlinx.android.synthetic.main.activity_school_links_list.*
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.service.model.links.SchoolListType
import org.pattonvillecs.pattonvilleapp.viewmodel.getViewModel
import org.pattonvillecs.pattonvilleapp.viewmodel.links.SchoolListActivityViewModel

/**
 * Activity to handle links for Peachjar and Nutrislice services
 *
 * @author Nathan Skelton
 * @since 1.0.0
 */
class SchoolListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_links_list)

        school_links_recyclerview.layoutManager = SmoothScrollLinearLayoutManager(this)
        school_links_recyclerview.addItemDecoration(FlexibleItemDecoration(this)
                .withDrawDividerOnLastItem(true)
                .withDefaultDivider())

        val schoolLinksAdapter = FlexibleAdapter<SchoolLinkItem>(null)
        school_links_recyclerview.adapter = schoolLinksAdapter

        val viewModel = getViewModel<SchoolListActivityViewModel>()

        viewModel.setSchoolListType(intent.getSerializableExtra(SCHOOL_LIST_TYPE) as SchoolListType)

        viewModel.titleStringResource.observe(this::getLifecycle) {
            if (it != null) setTitle(it)
        }

        viewModel.schoolLinkItems.observe(this::getLifecycle) {
            schoolLinksAdapter.updateDataSet(it.orEmpty(), true)
        }
    }

    companion object {
        private const val SCHOOL_LIST_TYPE = "school_list_type"

        @JvmStatic
        fun newInstance(context: Context, schoolListType: SchoolListType): Intent {
            return Intent(context, SchoolListActivity::class.java)
                    .putExtra(SCHOOL_LIST_TYPE, schoolListType)
        }
    }
}

