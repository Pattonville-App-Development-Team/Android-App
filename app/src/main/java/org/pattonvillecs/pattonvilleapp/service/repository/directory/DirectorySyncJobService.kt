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

package org.pattonvillecs.pattonvilleapp.service.repository.directory

import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.util.Log
import com.firebase.jobdispatcher.*
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures.addCallback
import com.google.firebase.crash.FirebaseCrash
import org.pattonvillecs.pattonvilleapp.service.model.directory.Directory
import org.pattonvillecs.pattonvilleapp.service.repository.DaggerJobService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * This class is a [JobService] that runs periodically. It downloads the directory and updates the local database with information.
 *
 * @since 1.3.0
 * @author Mitchell Skaggs
 */
class DirectorySyncJobService : DaggerJobService() {
    @field:Inject
    lateinit var directoryRepository: DirectoryRepository

    @field:Inject
    lateinit var directoryRetrofitService: DirectoryRetrofitService

    override fun onStartJob(job: JobParameters): Boolean {
        Log.i(TAG, "Starting directory sync service!")

        addCallback(
                directoryRetrofitService.directory,
                DirectoryCallback(this, job, directoryRepository),
                THREAD_POOL_EXECUTOR)

        return true
    }

    override fun onStopJob(job: JobParameters): Boolean = false

    class DirectoryCallback(
            private val directorySyncJobService: DirectorySyncJobService,
            private val job: JobParameters,
            private val directoryRepository: DirectoryRepository
    ) : FutureCallback<Directory> {
        override fun onSuccess(result: Directory?) {

            result?.let {
                directoryRepository.upsertAll(it)
            }

            directorySyncJobService.jobFinished(job, false)
        }

        override fun onFailure(t: Throwable) {
            FirebaseCrash.logcat(Log.WARN, TAG, "Download failed!")
            Log.w(TAG, "Exception thrown:", t)
            FirebaseCrash.report(t)
            directorySyncJobService.jobFinished(job, true)
        }
    }

    companion object {
        private const val TAG = "DirectorySyncJobService"

        @JvmStatic
        fun getRecurringDirectorySyncJob(firebaseJobDispatcher: FirebaseJobDispatcher): Job =
                firebaseJobDispatcher.newJobBuilder()
                        .setReplaceCurrent(true)
                        .setTag("directory_sync_job")
                        .setService(DirectorySyncJobService::class.java)
                        .addConstraint(Constraint.DEVICE_CHARGING)
                        .addConstraint(Constraint.ON_UNMETERED_NETWORK)
                        .setLifetime(Lifetime.FOREVER)
                        .setRecurring(true)
                        .setTrigger(Trigger.executionWindow(TimeUnit.HOURS.toSeconds(24).toInt(), TimeUnit.HOURS.toSeconds(48).toInt()))
                        .build()
    }
}