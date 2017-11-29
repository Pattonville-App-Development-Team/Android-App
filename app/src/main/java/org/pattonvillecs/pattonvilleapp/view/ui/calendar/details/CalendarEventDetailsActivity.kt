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

package org.pattonvillecs.pattonvilleapp.view.ui.calendar.details

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_calendar_event_details.*
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.SpotlightUtils
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.PinnableCalendarEvent
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.PinnableCalendarEventItem.Companion.getDataSourcesSpannableStringBuilder
import org.pattonvillecs.pattonvilleapp.viewmodel.calendar.details.CalendarEventDetailsActivityViewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import javax.inject.Inject

class CalendarEventDetailsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var calendarRepository: CalendarRepository

    private lateinit var viewModel: CalendarEventDetailsActivityViewModel
    private lateinit var eventUid: String

    private var event: PinnableCalendarEvent? = null
    private var addToCalendarItem: MenuItem? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_add_to_calendar -> {
                addToCalendar()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addToCalendar() {
        event?.let { event ->
            Log.i(TAG, "addToCalendar: " + event)
            val startDate = event.calendarEvent.startDateTime
            val endDate = event.calendarEvent.endDateTime

            val locationString = event.calendarEvent.location

            val intent = Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.toEpochMilli())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate.toEpochMilli())
                    .putExtra(CalendarContract.Events.TITLE, event.calendarEvent.summary)

            if (locationString.isNotEmpty())
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, locationString)

            val resolvedActivity = intent.resolveActivity(packageManager)

            if (resolvedActivity != null)
                startActivity(intent)
            else
                Toast.makeText(this, R.string.insert_calendar_event_failure, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.activity_calendar_detail, menu)
        addToCalendarItem = menu.findItem(R.id.action_add_to_calendar)

        SpotlightUtils.showSpotlightOnMenuItem(this, R.id.action_add_to_calendar, "CalendarEventDetailsActivity_FABAddToCalendar", "Want to keep track of this event?\nAdd it to your personal calendar.", "Add To Calendar")

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_event_details)

        eventUid = intent.getStringExtra(CALENDAR_EVENT_KEY)

        viewModel = ViewModelProviders.of(this).get(CalendarEventDetailsActivityViewModel::class.java)
        viewModel.calendarRepository = calendarRepository

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.getCalendarEvent(eventUid).observe(this::getLifecycle) {
            event = it
            addToCalendarItem?.isEnabled = event != null
            it?.let {
                val startDate = LocalDateTime.ofInstant(it.startDateTime, ZoneId.systemDefault())
                val endDate = LocalDateTime.ofInstant(it.endDateTime, ZoneId.systemDefault())
                time_and_date.text = getString(R.string.date_time_newline, FORMATTER.format(startDate), FORMATTER.format(endDate))

                location.text = if (it.location.isEmpty()) {
                    getString(R.string.no_location)
                } else {
                    location.onClick { _ ->
                        val gmmIntentUri = Uri.parse("geo:" + PATTONVILLE_COORDINATES + "?q=" + Uri.encode(it.location))
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        //mapIntent.`package` = "com.google.android.apps.maps"
                        startActivity(mapIntent)
                    }
                    it.location
                }

                extra_info.text = it.summary

                datasources.text = getDataSourcesSpannableStringBuilder(it.dataSources, this)

                pinned_button.isChecked = it.pinned
                pinned_button.setEventListener({ _, buttonState ->
                    if (buttonState) bg { calendarRepository.pinEvent(it.calendarEvent) }
                    else bg { calendarRepository.unpinEvent(it.calendarEvent) }
                })
            }
        }
    }

    companion object {

        const val CALENDAR_EVENT_KEY = "calendar_event"
        const val PATTONVILLE_COORDINATES = "38.733249,-90.420162"
        private val TAG = CalendarEventDetailsActivity::class.java.simpleName
        private val FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)

        fun createIntent(context: Context, uid: String): Intent {
            return Intent(context, CalendarEventDetailsActivity::class.java)
                    .putExtra(CALENDAR_EVENT_KEY, uid)
        }
    }
}
