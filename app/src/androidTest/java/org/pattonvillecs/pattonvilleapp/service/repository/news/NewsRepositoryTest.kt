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

@file:Suppress("TestFunctionName")

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
import org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase
import org.pattonvillecs.pattonvilleapp.service.repository.awaitValue
import org.threeten.bp.Instant

/**
 * Tests adding and removing articles from an in-memory database.
 *
 * @author Mitchell Skaggs
 * @since 1.4.0
 */
@Suppress("TestFunctionName")
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

        newsRepository.updateArticles(sourcedArticleSummary)

        val result = newsRepository.getArticlesByDataSources(DataSource.DISTRICT).awaitValue()

        result shouldMatch hasSize(equalTo(1))
        result[0] shouldMatch equalTo(sourcedArticleSummary)
    }

    @Test
    fun Given_SourcedArticleSummaries_When_UpsertAllCalled_Then_ReturnSameSourcedArticleSummariesInCorrectOrder() {
        val sourcedArticleSummaryOlder = testSourcedArticleSummary(guid = "test_guid_old", pubDate = Instant.ofEpochMilli(0))
        val sourcedArticleSummaryNewer = testSourcedArticleSummary(guid = "test_guid_new", pubDate = Instant.ofEpochMilli(1))

        newsRepository.updateArticles(sourcedArticleSummaryOlder, sourcedArticleSummaryNewer)

        val result = newsRepository.getArticlesByDataSources(DataSource.DISTRICT).awaitValue()

        result shouldMatch hasSize(equalTo(2))
        result shouldMatch equalTo(listOf(sourcedArticleSummaryNewer, sourcedArticleSummaryOlder))
    }

    private fun testSourcedArticleSummary(title: String = "test_title",
                                          link: String = "test_link",
                                          pubDate: Instant = Instant.ofEpochMilli(0),
                                          guid: String = "test_guid",
                                          dataSource: DataSource = DataSource.DISTRICT): ArticleSummary {
        return ArticleSummary(title, link, pubDate, guid, dataSource)
    }
}