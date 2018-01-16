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

package org.pattonvillecs.pattonvilleapp.service.repository.news

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.should.shouldMatch
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.service.model.news.ArticleSummary
import org.pattonvillecs.pattonvilleapp.service.model.news.DataSourceMarker
import org.pattonvillecs.pattonvilleapp.service.model.news.SourcedArticleSummary
import org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase
import org.pattonvillecs.pattonvilleapp.service.repository.awaitValue
import org.threeten.bp.Instant

/**
 * Tests adding and removing articles from an in-memory database.
 *
 * @author Mitchell Skaggs
 * @since 1.4.0
 */
@Suppress("FunctionName")
@RunWith(AndroidJUnit4::class)
@MediumTest
class NewsRepositoryTest {
    private lateinit var appDatabase: AppDatabase
    private lateinit var newsRepository: NewsRepository


    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        appDatabase = AppDatabase.init(Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)).build()
        newsRepository = NewsRepository(appDatabase)
    }

    @After
    fun closeDb() {
        appDatabase.close()
    }

    @Test
    fun Given_SourcedArticleSummary_When_UpsertAllCalled_Then_ReturnSameSourcedArticleSummary() {
        val sourcedArticleSummary = testSourcedArticleSummary()

        newsRepository.updateSourcedArticles(sourcedArticleSummary)

        val result = newsRepository.getArticlesByDataSources(DataSource.DISTRICT).awaitValue()

        result shouldMatch hasSize(equalTo(1))
        result[0] shouldMatch equalTo(sourcedArticleSummary)
    }

    @Test
    fun Given_SourcedArticleSummaries_When_UpsertAllCalled_Then_ReturnSameSourcedArticleSummariesInCorrectOrder() {
        val sourcedArticleSummary1 = testSourcedArticleSummary(guid = "test_guid_1", pubDate = Instant.ofEpochMilli(0))
        val sourcedArticleSummary2 = testSourcedArticleSummary(guid = "test_guid_2", pubDate = Instant.ofEpochMilli(1))

        newsRepository.updateSourcedArticles(sourcedArticleSummary1, sourcedArticleSummary2)

        val result = newsRepository.getArticlesByDataSources(DataSource.DISTRICT).awaitValue()

        result shouldMatch hasSize(equalTo(2))
        result[0] shouldMatch equalTo(sourcedArticleSummary1)
        result[1] shouldMatch equalTo(sourcedArticleSummary2)
    }

    private fun testSourcedArticleSummary(title: String = "test_title",
                                          link: String = "test_link",
                                          pubDate: Instant = Instant.ofEpochMilli(0),
                                          guid: String = "test_guid",
                                          dataSources: Collection<DataSource> = setOf(DataSource.DISTRICT)): SourcedArticleSummary {
        return SourcedArticleSummary(ArticleSummary(title, link, pubDate, guid), dataSources.mapTo(mutableSetOf(), { DataSourceMarker(guid, it) }))
    }
}