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

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.jetbrains.anko.find
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.service.model.news.ArticleSummary
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

/**
 * Created by Mitchell Skaggs on 1/16/2018.
 *
 * @since 1.4.0
 */
data class ArticleSummaryItem(private val articleSummary: ArticleSummary) : AbstractFlexibleItem<ArticleSummaryItem.ArticleSummaryItemViewHolder>(), IFilterable {
    override fun filter(constraint: String?): Boolean {
        if (constraint == null || constraint.isBlank())
            return true

        val titleRatio = FuzzySearch.weightedRatio(constraint, articleSummary.title.toLowerCase())
        val dataSourceRatio = FuzzySearch.weightedRatio(constraint, articleSummary.dataSource.toString().toLowerCase())
        return titleRatio > 70 || dataSourceRatio > 70
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ArticleSummaryItemViewHolder {
        return ArticleSummaryItemViewHolder(view, adapter)
    }

    override fun getLayoutRes(): Int = R.layout.news_recyclerview_item

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: ArticleSummaryItemViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.circle.setColorFilter(articleSummary.dataSource.calendarColor)
        holder.abbreviation.text = articleSummary.dataSource.initialsName
        holder.title.text = articleSummary.title
        holder.date.text = FORMATTER.format(articleSummary.pubDate)
    }

    class ArticleSummaryItemViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>, stickyHeader: Boolean = false) : FlexibleViewHolder(view, adapter, stickyHeader) {
        val circle = view.find<ImageView>(R.id.news_front_imageview)
        val abbreviation = view.find<TextView>(R.id.news_circle_school_id)
        val title = view.find<TextView>(R.id.home_news_listview_item_textView)
        val date = view.find<TextView>(R.id.news_list_article_date_textview)
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withZone(ZoneId.systemDefault())
    }
}