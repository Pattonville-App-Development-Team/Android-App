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

package org.pattonvillecs.pattonvilleapp.service.repository.calendar

import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.util.Log
import com.firebase.jobdispatcher.*
import com.google.common.base.Function
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures.*
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.crash.FirebaseCrash
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.component.VEvent
import org.pattonvillecs.pattonvilleapp.service.model.DataSource
import org.pattonvillecs.pattonvilleapp.service.model.calendar.DataSourceMarker.CREATOR.dataSource
import org.pattonvillecs.pattonvilleapp.service.model.calendar.SourcedCalendar
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.CalendarEvent
import org.pattonvillecs.pattonvilleapp.service.repository.DaggerJobService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * This class is a [JobService] that runs periodically. It downloads all school calendars and updates the local database with information.
 *
 * @since 1.2.0
 * @author Mitchell Skaggs
 */
class CalendarSyncJobService : DaggerJobService() {
    @field:Inject
    lateinit var calendarRepository: CalendarRepository

    @field:Inject
    lateinit var calendarRetrofitService: CalendarRetrofitService

    override fun onStartJob(job: JobParameters): Boolean {
        Log.i(TAG, "Starting calendar sync service!")
        addCallback(
                allAsList(DataSource.CALENDARS.map(this::dataSourceToSourcedCalendarFuture)),
                SourcedCalendarListCallback(this, job, calendarRepository),
                THREAD_POOL_EXECUTOR)
        return true
    }

    override fun onStopJob(job: JobParameters): Boolean = false

    private fun dataSourceToSourcedCalendarFuture(dataSource: DataSource): ListenableFuture<SourcedCalendar> =
            transform(calendarRetrofitService.getCalendar(dataSource), Function<Calendar?, SourcedCalendar> { it: Calendar? -> SourcedCalendar(dataSource, it!!) }, THREAD_POOL_EXECUTOR)

    class SourcedCalendarListCallback(
            private val calendarSyncJobService: CalendarSyncJobService,
            private val job: JobParameters,
            private val calendarRepository: CalendarRepository
    ) : FutureCallback<List<SourcedCalendar>> {

        override fun onSuccess(result: List<SourcedCalendar>?) {
            Log.i(TAG, "Successful download!")
            result?.forEach { sourcedCalendar ->
                val events = sourcedCalendar.calendar.components.filter { it is VEvent }.map { it as VEvent }.map { CalendarEvent(it) }
                val dataSourceMarkers = events.map { dataSource(it, sourcedCalendar.dataSource) }

                calendarRepository.updateEventsAndDataSourceMarkers(events, dataSourceMarkers)
                Log.i(TAG, "Inserted ${events.size} items into ${sourcedCalendar.dataSource}")
            }
            calendarSyncJobService.jobFinished(job, false)
        }

        override fun onFailure(t: Throwable) {
            FirebaseCrash.logcat(Log.WARN, TAG, "Download failed!")
            FirebaseCrash.report(t)
            calendarSyncJobService.jobFinished(job, true)
        }
    }

    companion object {
        private const val TAG = "CalendarSyncJobService"

        @JvmStatic
        fun getRecurringCalendarSyncJob(firebaseJobDispatcher: FirebaseJobDispatcher): Job =
                firebaseJobDispatcher.newJobBuilder()
                        .setReplaceCurrent(true)
                        .setTag("calendar_sync_job")
                        .setService(CalendarSyncJobService::class.java)
                        .addConstraint(Constraint.DEVICE_CHARGING)
                        .addConstraint(Constraint.ON_UNMETERED_NETWORK)
                        .setLifetime(Lifetime.FOREVER)
                        .setRecurring(true)
                        .setTrigger(Trigger.executionWindow(TimeUnit.HOURS.toSeconds(6).toInt(), TimeUnit.HOURS.toSeconds(12).toInt()))
                        .build()

        @JvmStatic
        fun getInstantCalendarSyncJob(firebaseJobDispatcher: FirebaseJobDispatcher): Job =
                firebaseJobDispatcher.newJobBuilder()
                        .setReplaceCurrent(true)
                        .setTag("instant_calendar_sync_job")
                        .setService(CalendarSyncJobService::class.java)
                        .setLifetime(Lifetime.FOREVER)
                        .setTrigger(Trigger.NOW)
                        .build()
    }
}