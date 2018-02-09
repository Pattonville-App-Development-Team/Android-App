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

package org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.single

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.annotation.DrawableRes
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.jetbrains.anko.browse
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.service.model.DataSource
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.details.CalendarEventDetailsActivity
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.IFacultyItem
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.single.DataSourceSummaryItem.DataSourceSummaryItemViewHolder

/**
 * Created by Mitchell Skaggs on 12/10/2017.
 */
data class DataSourceSummaryItem(val dataSource: DataSource) : AbstractFlexibleItem<DataSourceSummaryItemViewHolder>(), IFacultyItem<DataSourceSummaryItemViewHolder> {
    override val location: DataSource? get() = dataSource

    override fun getLayoutRes(): Int = R.layout.directory_datasource_suummary_item

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: DataSourceSummaryItemViewHolder, position: Int, payloads: MutableList<Any>?) {

        Picasso.get()
                .load(getImageResourceForDataSource(dataSource))
                .fit()
                .centerCrop()
                .into(holder.schoolImage)

        holder.address.text = dataSource.address
        holder.address.onClick {
            val gmmIntentUri = Uri.parse("geo:" + CalendarEventDetailsActivity.PATTONVILLE_COORDINATES + "?q=" + Uri.encode(dataSource.address))
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            it?.context?.startActivity(mapIntent)
        }

        holder.mainPhone.apply {
            text = dataSource.mainNumber
            onClick {
                it?.context?.dial(dataSource.mainNumber)
            }
        }

        dataSource.attendanceNumber.ifPresentOrElse({ number ->
            holder.attendancePhone.apply {
                text = number
                onClick {
                    it?.context?.dial(number)
                }
            }
        }, {
            holder.attendancePhone.text = "N/A"
        })

        dataSource.faxNumber.ifPresentOrElse({ number ->
            holder.fax.text = number
        }, {
            holder.fax.text = "N/A"
        })

        holder.website.onClick {
            it?.context?.browse(dataSource.websiteURL)
        }
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): DataSourceSummaryItemViewHolder =
            DataSourceSummaryItemViewHolder(view, adapter)

    class DataSourceSummaryItemViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>, stickyHeader: Boolean = false) : FlexibleViewHolder(view, adapter, stickyHeader) {
        val schoolImage = view.find<ImageView>(R.id.school_image)
        val address = view.find<TextView>(R.id.address)
        val mainPhone = view.find<TextView>(R.id.main_phone)
        val attendancePhone = view.find<TextView>(R.id.attendance_phone)
        val fax = view.find<TextView>(R.id.fax)
        val website = view.find<TextView>(R.id.website)
    }

    @DrawableRes
    private fun getImageResourceForDataSource(dataSource: DataSource): Int {
        return when (dataSource) {
            DataSource.HIGH_SCHOOL -> R.drawable.highschool_building
            DataSource.HEIGHTS_MIDDLE_SCHOOL -> R.drawable.heights_building
            DataSource.HOLMAN_MIDDLE_SCHOOL -> R.drawable.holman_building
            DataSource.REMINGTON_TRADITIONAL_SCHOOL -> R.drawable.remington_building
            DataSource.BRIDGEWAY_ELEMENTARY -> R.drawable.bridgeway_building
            DataSource.DRUMMOND_ELEMENTARY -> R.drawable.drummond_building
            DataSource.PARKWOOD_ELEMENTARY -> R.drawable.parkwood_building
            DataSource.ROSE_ACRES_ELEMENTARY -> R.drawable.roseacres_building
            DataSource.WILLOW_BROOK_ELEMENTARY -> R.drawable.willowbrook_building
            DataSource.DISTRICT -> R.drawable.learningcenter_building
            else -> R.drawable.learningcenter_building
        }
    }
}

private fun Context.dial(number: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
