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

package org.pattonvillecs.pattonvilleapp.view.adapter.directory

import eu.davidea.flexibleadapter.FlexibleAdapter
import org.apache.commons.text.WordUtils
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.FacultyItem
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.IFacultyItem

/**
 * Created by Mitchell Skaggs on 12/10/2017.
 */
class FacultyAdapter(items: MutableList<out IFacultyItem<*>>? = null, listeners: Any? = null, stableIds: Boolean = false) : FlexibleAdapter<IFacultyItem<*>>(items, listeners, stableIds) {

    override fun onCreateBubbleText(position: Int): String {
        val item = this.getItem(position)
        return (item as? FacultyItem)?.faculty?.description.let { WordUtils.capitalizeFully(it, *FacultyItem.delimiters) } ?: ""
    }
}