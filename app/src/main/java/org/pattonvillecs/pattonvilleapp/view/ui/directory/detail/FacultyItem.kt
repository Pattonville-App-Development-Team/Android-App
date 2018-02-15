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

package org.pattonvillecs.pattonvilleapp.view.ui.directory.detail

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.apache.commons.text.WordUtils
import org.jetbrains.anko.email
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.service.model.DataSource
import org.pattonvillecs.pattonvilleapp.service.model.directory.Faculty
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.all.DataSourceHeader

/**
 * This item binds a [Faculty] to a view.
 *
 * @since 1.3.0
 * @author Mitchell Skaggs
 */

data class FacultyItem(val faculty: Faculty) : AbstractSectionableItem<FacultyItem.FacultyItemViewHolder, DataSourceHeader>(null), IFacultyItem<FacultyItem.FacultyItemViewHolder>, IFilterable<String> {
    override val location: DataSource? get() = faculty.location

    constructor(faculty: Faculty, header: DataSourceHeader? = null) : this(faculty) {
        this.header = header
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): FacultyItemViewHolder =
            FacultyItemViewHolder(view, adapter)

    override fun getLayoutRes(): Int =
            R.layout.faculty_item

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: FacultyItemViewHolder, position: Int, payloads: MutableList<Any>?) {
        val context = adapter.recyclerView.context

        holder.name.text = context.getString(R.string.faculty_name,
                WordUtils.capitalizeFully(faculty.firstName, *delimiters),
                WordUtils.capitalizeFully(faculty.lastName, *delimiters))

        holder.department.text = WordUtils.capitalizeFully(faculty.description, *delimiters)

        if (faculty.email != null) {
            holder.emailButton.visibility = View.VISIBLE
            holder.emailButton.onClick {
                context.email(faculty.email)
            }
        } else {
            holder.emailButton.visibility = View.GONE
        }

        if (faculty.phoneNumbers.firstOrNull()?.extension1 != null) {
            holder.callButton.visibility = View.VISIBLE
            holder.callButton.onClick {
                context.startActivity(CallExtensionActivity.newIntent(context, faculty.phoneNumbers.first()))
            }
        } else {
            holder.callButton.visibility = View.GONE
        }
    }

    override fun filter(constraint: String?): Boolean {
        return if (constraint == null || constraint.isEmpty() || constraint.isBlank())
            true
        else {
            FuzzySearch.weightedRatio(constraint, faculty.firstName) > 75
                    || FuzzySearch.weightedRatio(constraint, faculty.lastName) > 75
                    || FuzzySearch.weightedRatio(constraint, faculty.description) > 75
        }
    }

    class FacultyItemViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>, stickyHeader: Boolean = false) : FlexibleViewHolder(view, adapter, stickyHeader) {
        val name = view.find<TextView>(R.id.name)
        val department = view.find<TextView>(R.id.department)
        val emailButton = view.find<ImageButton>(R.id.email_button)
        val callButton = view.find<ImageButton>(R.id.call_button)
    }

    companion object {
        val delimiters = charArrayOf(' ', '-', '/', '\'')
    }
}