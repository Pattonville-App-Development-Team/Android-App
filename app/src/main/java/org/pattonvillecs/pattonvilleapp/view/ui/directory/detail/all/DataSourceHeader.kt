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

package org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.all

import android.view.View
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.IFacultyItem
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.all.DataSourceHeader.DataSourceHeaderViewHolder

/**
 * This class is a header that shows the location of a faculty member.
 *
 * @since 1.3.0
 * @author Mitchell Skaggs
 */
data class DataSourceHeader(val dataSource: DataSource) : AbstractHeaderItem<DataSourceHeaderViewHolder>(), IFacultyItem<DataSourceHeaderViewHolder> {
    override val location: DataSource? get() = dataSource

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: DataSourceHeaderViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.name.text = dataSource.longName
    }

    override fun getLayoutRes(): Int =
            R.layout.datasource_header

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): DataSourceHeaderViewHolder =
            DataSourceHeaderViewHolder(view, adapter, true)

    class DataSourceHeaderViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>, stickyHeader: Boolean = false) : FlexibleViewHolder(view, adapter, stickyHeader) {
        val name = contentView as TextView
    }
}