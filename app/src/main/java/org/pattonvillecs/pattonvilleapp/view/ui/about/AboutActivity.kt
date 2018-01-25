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

package org.pattonvillecs.pattonvilleapp.view.ui.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemAnimator
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.activity_about.*
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.viewmodel.about.AboutActivityViewModel
import org.pattonvillecs.pattonvilleapp.viewmodel.getViewModel

/**
 * This Activity displays a grid of the developers who worked on the project, organized by team. The blank space at the end of the list may be long-pressed to reveal an easter egg.
 *
 * @author Mitchell Skaggs
 * @since 1.0.0
 */

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "About Us"
        setContentView(R.layout.activity_about)

        val viewModel = getViewModel<AboutActivityViewModel>()

        val aboutAdapter = FlexibleAdapter<IFlexible<*>>(null)
        aboutAdapter.setDisplayHeadersAtStartUp(true)
                .setAnimationEntryStep(false)
                .setAnimationOnScrolling(true)
                .setAnimationOnReverseScrolling(true)

        val manager = SmoothScrollGridLayoutManager(this, 2)
        manager.spanSizeLookup = FunctionalSpanSizeLookup {
            if (aboutAdapter.isHeader(aboutAdapter.getItem(it))) 2 else 1
        }
        about_us_recyclerview.layoutManager = manager
        about_us_recyclerview.itemAnimator = FlexibleItemAnimator()
        about_us_recyclerview.adapter = aboutAdapter

        viewModel.developers.observe(this::getLifecycle) {
            it?.let {
                aboutAdapter.updateDataSet(it, true)
            }
        }
    }

    private class FunctionalSpanSizeLookup(val lookup: (position: Int) -> Int) : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int = lookup(position)
    }
}
