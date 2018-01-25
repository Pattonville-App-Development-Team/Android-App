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

package org.pattonvillecs.pattonvilleapp.viewmodel.home

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication
import org.pattonvillecs.pattonvilleapp.news.NewsParsingUpdateData
import org.pattonvillecs.pattonvilleapp.news.articles.NewsArticle
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.PinnableCalendarEventItem
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.map
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.switchMap
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.zipTo
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

/**
 * The ViewModel for the HomeFragment. This handles loading news, calendar, and pinned events.
 *
 * @since 1.2.0
 * @author Mitchell Skaggs
 */
class HomeFragmentViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var calendarRepository: CalendarRepository

    val carouselVisiblePreference: LiveData<Boolean> by lazy { PreferenceUtils.getCarouselVisibleLiveData(getApplication()) }
    val homeNewsAmountPreference: LiveData<Int> by lazy { PreferenceUtils.getHomeNewsLiveData(getApplication()) }
    val homeCalendarAmountPreference: LiveData<Int> by lazy { PreferenceUtils.getHomeEventsLiveData(getApplication()) }
    val homePinnedAmountPreference: LiveData<Int> by lazy { PreferenceUtils.getHomePinnedLiveData(getApplication()) }
    private val selectedDataSources: LiveData<Set<DataSource>> by lazy { PreferenceUtils.getSelectedSchoolsLiveData(getApplication()) }

    val newsArticles: LiveData<List<NewsArticle>> by lazy {
        homeNewsAmountPreference.switchMap { amount ->
            PauseableListenerLiveData<NewsParsingUpdateData>(NewsParsingUpdateData.NEWS_LISTENER_ID, getApplication<PattonvilleApplication>()).map { articleList ->
                articleList.newsData.values.flatMap { it }.sortedByDescending { it.publishDate }.take<NewsArticle>(amount)
            }
        }

    }

    val recentCalendarEvents: LiveData<List<PinnableCalendarEventItem>> by lazy {
        homeCalendarAmountPreference.zipTo(selectedDataSources).switchMap { pair ->
            calendarRepository.getEventsAfterDate(pair.second.toList(), LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .map { eventList ->
                        eventList.take(pair.first).map { PinnableCalendarEventItem(it) }
                    }
        }
    }

    val recentPinnedCalendarEvents: LiveData<List<PinnableCalendarEventItem>> by lazy {
        homeCalendarAmountPreference.zipTo(selectedDataSources).switchMap { pair ->
            calendarRepository.getPinnedEventsAfterDate(pair.second.toList(), LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                    .map { pinnedEventList ->
                        pinnedEventList.take(pair.first).map { PinnableCalendarEventItem(it) }
                    }
        }
    }
}