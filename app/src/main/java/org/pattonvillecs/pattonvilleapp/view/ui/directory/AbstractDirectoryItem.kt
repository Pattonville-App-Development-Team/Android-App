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

package org.pattonvillecs.pattonvilleapp.view.ui.directory

import android.support.annotation.DrawableRes
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.jetbrains.anko.find
import org.pattonvillecs.pattonvilleapp.R

/**
 * Created by Mitchell Skaggs on 12/10/2017.
 */
abstract class AbstractDirectoryItem : AbstractFlexibleItem<AbstractDirectoryItem.DirectoryItemViewHolder>() {

    @get:DrawableRes
    abstract val mascotDrawableRes: Int

    abstract val displayName: String

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: DirectoryItemViewHolder, position: Int, payloads: MutableList<Any>?) {

        holder.name.text = displayName

        Picasso.get()
                .load(mascotDrawableRes)
                .error(mascotDrawableRes) //Needed because VectorDrawables are not loaded properly when using .load(). See square/picasso/issues/1109
                .centerInside()
                .fit()
                .into(holder.icon)
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<*>): DirectoryItemViewHolder {
        return DirectoryItemViewHolder(view, adapter)
    }

    override fun getLayoutRes(): Int {
        return R.layout.directory_recycler_view_item
    }

    class DirectoryItemViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>, stickyHeader: Boolean = false) : FlexibleViewHolder(view, adapter, stickyHeader) {
        val name = view.find<TextView>(R.id.directory_name)
        val icon = view.find<ImageView>(R.id.directory_icon)
    }
}