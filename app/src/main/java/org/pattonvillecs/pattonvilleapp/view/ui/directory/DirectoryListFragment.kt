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

package org.pattonvillecs.pattonvilleapp.view.ui.directory


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_directory.*
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.viewmodel.directory.DirectoryListFragmentViewModel
import org.pattonvillecs.pattonvilleapp.viewmodel.getViewModel

/**
 * @author Keturah Gadson
 * @since 1.0.0
 */

class DirectoryListFragment : Fragment() {

    lateinit var viewModel: DirectoryListFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle(R.string.title_fragment_directory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_directory, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val directoryAdapter = FlexibleAdapter<AbstractDirectoryItem>(null, null, true)

        directory_recyclerView.adapter = directoryAdapter
        directory_recyclerView.layoutManager = SmoothScrollLinearLayoutManager(context)
        directory_recyclerView.addItemDecoration(
                FlexibleItemDecoration(context!!)
                        .withDefaultDivider()
                        .withDrawDividerOnLastItem(true))

        viewModel.directoryItems.observe(this::getLifecycle) {
            directoryAdapter.updateDataSet(it)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): DirectoryListFragment {
            return DirectoryListFragment()
        }
    }
}
