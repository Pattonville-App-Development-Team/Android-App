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

package org.pattonvillecs.pattonvilleapp.service.repository.news

import android.arch.lifecycle.LiveData
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.service.model.news.ArticleSummary
import org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Mitchell Skaggs on 1/15/2018.
 *
 * @since 1.4.0
 */
@Singleton
class NewsRepository @Inject constructor(appDatabase: AppDatabase) {
    private val newsDao: NewsDao = appDatabase.newsDao()

    fun getArticlesByDataSources(dataSources: Iterable<DataSource>): LiveData<List<ArticleSummary>> =
            newsDao.getArticlesByDataSources(dataSources.toList())

    fun getArticlesByDataSources(vararg dataSources: DataSource): LiveData<List<ArticleSummary>> =
            getArticlesByDataSources(dataSources.asIterable())

    fun updateArticles(articleSummaries: List<ArticleSummary>) {
        newsDao.upsertAll(articleSummaries)
    }

    fun updateArticles(vararg articleSummaries: ArticleSummary) {
        newsDao.upsertAll(articleSummaries.asList())
    }
}