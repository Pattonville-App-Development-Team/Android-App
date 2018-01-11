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

package org.pattonvillecs.pattonvilleapp.service.repository.news

import android.app.Application
import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import com.github.magneticflux.rss.namespaces.standard.elements.Item
import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.should.shouldMatch
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.di.network.NewsRetrofitServiceModule.provideNewsRetrofitService
import org.pattonvillecs.pattonvilleapp.di.network.OkHttpClientModule.provideOkHttpClient

/**
 * @author Mitchell Skaggs
 * @since 1.4.0
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class NewsRetrofitTest {
    private lateinit var newsRetrofitService: NewsRetrofitService

    @Before
    fun createService() {
        val application = InstrumentationRegistry.getTargetContext().applicationContext as Application
        newsRetrofitService = provideNewsRetrofitService(provideOkHttpClient(application))
    }

    @Test
    fun Given_AllNewsDataSources_When_GetRssCalled_Then_ReturnValidXML() {
        val feeds = newsRetrofitService.getRss(DataSource.HIGH_SCHOOL).get()

        feeds.channel.title shouldMatch equalTo("News")
        feeds.channel.items shouldMatch anyElement(has(Item::title, equalTo("PHS Announcements")))
        feeds.channel.items shouldMatch allElements(has(Item::author, present(anything)))
        feeds.channel.items shouldMatch allElements(Matcher("has matching authors", { it.author == it.iTunesAuthor }))
    }
}
