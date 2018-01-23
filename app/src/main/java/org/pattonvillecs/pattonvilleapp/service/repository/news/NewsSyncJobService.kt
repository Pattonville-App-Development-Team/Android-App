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

import android.os.AsyncTask
import android.util.Log
import com.firebase.jobdispatcher.*
import com.github.magneticflux.rss.namespaces.standard.elements.Rss
import com.google.common.base.Function
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.Futures.transform
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.crash.FirebaseCrash
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.service.model.news.ArticleSummary
import org.pattonvillecs.pattonvilleapp.service.repository.DaggerJobService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

typealias SourcedRss = Pair<Rss, DataSource>

/**
 * Created by Mitchell Skaggs on 1/15/2018.
 *
 * @since 1.4.0
 */
class NewsSyncJobService : DaggerJobService() {
    @field:Inject
    lateinit var newsRepository: NewsRepository

    @field:Inject
    lateinit var newsRetrofitService: NewsRetrofitService

    override fun onStartJob(job: JobParameters): Boolean {
        Log.i(TAG, "Starting news sync service!")

        Futures.addCallback(
                Futures.allAsList(DataSource.NEWS.map(this::dataSourceToSourcedRss)),
                SourcedNewsListCallback(this, job, newsRepository),
                AsyncTask.THREAD_POOL_EXECUTOR)

        return true
    }

    private fun dataSourceToSourcedRss(dataSource: DataSource): ListenableFuture<SourcedRss> =
            transform(newsRetrofitService.getRss(dataSource), Function<Rss, SourcedRss> { it: Rss? -> it!! to dataSource }, AsyncTask.THREAD_POOL_EXECUTOR)

    override fun onStopJob(job: JobParameters): Boolean = false

    class SourcedNewsListCallback(
            private val newsSyncJobService: NewsSyncJobService,
            private val job: JobParameters,
            private val newsRepository: NewsRepository
    ) : FutureCallback<List<SourcedRss>> {

        override fun onSuccess(result: List<SourcedRss>?) {
            Log.i(TAG, "Successful download!")

            val articles = mutableListOf<ArticleSummary>()

            result.orEmpty().forEach {
                Log.i(TAG, "Processed ${it.second} with ${it.first.channel.items.size} articles")
                it.first.channel.items.forEach { item ->
                    articles += ArticleSummary(item, it.second)
                }
            }

            newsRepository.updateArticles(articles)

            newsSyncJobService.jobFinished(job, false)
        }

        override fun onFailure(t: Throwable) {
            FirebaseCrash.logcat(Log.WARN, TAG, "Download failed!")
            FirebaseCrash.report(t)
            newsSyncJobService.jobFinished(job, true)
        }
    }

    companion object {
        private const val TAG = "NewsSyncJobService"

        @JvmStatic
        fun getRecurringNewsSyncJob(firebaseJobDispatcher: FirebaseJobDispatcher): Job =
                firebaseJobDispatcher.newJobBuilder()
                        .setReplaceCurrent(true)
                        .setTag("recurring_news_sync_job")
                        .setService(NewsSyncJobService::class.java)
                        .addConstraint(Constraint.DEVICE_CHARGING)
                        .addConstraint(Constraint.ON_UNMETERED_NETWORK)
                        .setLifetime(Lifetime.FOREVER)
                        .setRecurring(true)
                        .setTrigger(Trigger.executionWindow(TimeUnit.HOURS.toSeconds(1).toInt(), TimeUnit.HOURS.toSeconds(4).toInt()))
                        .build()

        @JvmStatic
        fun getInstantNewsSyncJob(firebaseJobDispatcher: FirebaseJobDispatcher): Job =
                firebaseJobDispatcher.newJobBuilder()
                        .setReplaceCurrent(true)
                        .setTag("instant_news_sync_job")
                        .setService(NewsSyncJobService::class.java)
                        .setLifetime(Lifetime.FOREVER)
                        .setTrigger(Trigger.NOW)
                        .build()
    }
}