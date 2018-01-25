/*
 * Copyright (C) 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.viewmodel.news

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils
import org.pattonvillecs.pattonvilleapp.service.model.news.ArticleSummary
import org.pattonvillecs.pattonvilleapp.service.repository.news.NewsRepository
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.map
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.switchMap
import org.pattonvillecs.pattonvilleapp.view.ui.news.ArticleSummaryItem
import org.pattonvillecs.pattonvilleapp.viewmodel.app

/**
 * Created by Mitchell Skaggs on 1/16/2018.
 *
 * @since 1.4.0
 */
class NewsFragmentViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var newsRepository: NewsRepository

    private val selectedDataSources: LiveData<Set<DataSource>> = PreferenceUtils.getSelectedSchoolsLiveData(app)

    private val _searchText: MutableLiveData<String> = MutableLiveData()
    val searchText: LiveData<String> = _searchText

    val articles: LiveData<List<ArticleSummary>> by lazy {
        selectedDataSources.switchMap { newsRepository.getArticlesByDataSources(it) }
    }
    val articleItems: LiveData<List<ArticleSummaryItem>> by lazy {
        articles.map {
            it.map(::ArticleSummaryItem)
        }
    }

    fun setSearchText(text: String) {
        _searchText.value = text
    }
}