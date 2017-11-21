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

package org.pattonvillecs.pattonvilleapp.model.calendar.job

import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.google.common.base.Function
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures.*
import com.google.common.util.concurrent.ListenableFuture
import dagger.android.AndroidInjection
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.component.VEvent
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarEvent
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.model.calendar.DataSourceMarker
import org.pattonvillecs.pattonvilleapp.model.calendar.SourcedCalendar
import org.threeten.bp.Instant
import javax.inject.Inject

/**
 * Created by Mitchell Skaggs on 11/20/2017.
 */
class CalendarSyncJobService : JobService() {
    @field:Inject
    lateinit var calendarRepository: CalendarRepository

    @field:Inject
    lateinit var calendarRetrofitService: CalendarRetrofitService

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onStartJob(job: JobParameters): Boolean {
        Log.i(TAG, "Starting calendar sync service!")
        addCallback(
                allAsList(DataSource.CALENDARS.map(this::dataSourceToSourcedCalendarFuture)),
                SourcedCalendarListCallback(this, job, calendarRepository),
                THREAD_POOL_EXECUTOR)
        return true
    }

    override fun onStopJob(job: JobParameters): Boolean = false

    private fun dataSourceToSourcedCalendarFuture(dataSource: DataSource): ListenableFuture<SourcedCalendar>? =
            transform(calendarRetrofitService.getCalendar(dataSource), Function<Calendar, SourcedCalendar> { it?.let { SourcedCalendar(dataSource, it) } }, THREAD_POOL_EXECUTOR)

    class SourcedCalendarListCallback(
            private val calendarSyncJobService: CalendarSyncJobService,
            private val job: JobParameters,
            private val calendarRepository: CalendarRepository
    ) : FutureCallback<List<SourcedCalendar?>> {

        override fun onSuccess(result: List<SourcedCalendar?>?) {
            Log.i(TAG, "Successful download!")
            result?.forEach {
                it?.let { sourcedCalendar ->
                    val vEvents = sourcedCalendar.calendar.components.filter { it is VEvent }.map { it as VEvent }
                    val events = vEvents.map { CalendarEvent(it.uid.value, it.summary.value, it.location.value, Instant.ofEpochMilli(it.startDate.date.time), Instant.ofEpochMilli(it.endDate.date.time)) }
                    val dataSourceMarkers = vEvents.map { DataSourceMarker(it.uid.value, sourcedCalendar.dataSource) }

                    calendarRepository.insertEventsAndDataSourceMarkers(events, dataSourceMarkers)
                    Log.i(TAG, "Inserted ${events.size} items into ${sourcedCalendar.dataSource}")
                }
            }
            calendarSyncJobService.jobFinished(job, false)
        }

        override fun onFailure(t: Throwable) {
            Log.i(TAG, "Download failed!", t)
            calendarSyncJobService.jobFinished(job, true)
        }
    }

    companion object {
        private const val TAG = "CalendarSyncJobService"
    }
}