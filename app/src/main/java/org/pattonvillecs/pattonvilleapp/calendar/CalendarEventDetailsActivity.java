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

package org.pattonvillecs.pattonvilleapp.calendar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.SpotlightUtils;
import org.pattonvillecs.pattonvilleapp.model.calendar.event.PinnableCalendarEvent;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import static org.pattonvillecs.pattonvilleapp.calendar.events.EventFlexibleItem.getDataSourcesSpannableStringBuilder;

public class CalendarEventDetailsActivity extends AppCompatActivity {

    public static final String CALENDAR_EVENT_KEY = "calendar_event";
    public static final String PATTONVILLE_COORDINATES = "38.733249,-90.420162";
    private static final String TAG = CalendarEventDetailsActivity.class.getSimpleName();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);

    private PinnableCalendarEvent event;

    public static Intent createIntent(Context context, PinnableCalendarEvent pinnableCalendarEvent) {
        return new Intent(context, CalendarEventDetailsActivity.class)
                .putExtra(CalendarEventDetailsActivity.CALENDAR_EVENT_KEY, pinnableCalendarEvent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_to_calendar:
                addToCalendar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addToCalendar() {
        Log.i(TAG, "addToCalendar: " + event);
        Instant startDate = event.getCalendarEvent().getStartDateTime();
        Instant endDate = event.getCalendarEvent().getEndDateTime();

        String locationString = event.getCalendarEvent().getLocation();

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.toEpochMilli())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate.toEpochMilli())
                .putExtra(CalendarContract.Events.TITLE, event.getCalendarEvent().getSummary());

        if (!locationString.isEmpty())
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, locationString);

        ComponentName resolvedActivity = intent.resolveActivity(getPackageManager());

        if (resolvedActivity != null)
            startActivity(intent);
        else
            Toast.makeText(this, R.string.insert_calendar_event_failure, Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_calendar_detail, menu);
        SpotlightUtils.showSpotlightOnMenuItem(this, R.id.action_add_to_calendar, "CalendarEventDetailsActivity_FABAddToCalendar", "Want to keep track of this event?\nAdd it to your personal calendar.", "Add To Calendar");
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_event_details);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        event = getIntent().getParcelableExtra(CALENDAR_EVENT_KEY);
        if (event == null)
            throw new Error("Event required!");

        TextView timeDateTextView = findViewById(R.id.time_and_date);

        LocalDateTime startDate = LocalDateTime.ofInstant(event.getCalendarEvent().getStartDateTime(), ZoneId.systemDefault());
        LocalDateTime endDate = LocalDateTime.ofInstant(event.getCalendarEvent().getEndDateTime(), ZoneId.systemDefault());

        timeDateTextView.setText(getString(R.string.date_time_newline, FORMATTER.format(startDate), FORMATTER.format(endDate)));

        TextView locationTextView = findViewById(R.id.location);
        final String location = event.getCalendarEvent().getLocation();
        if (!location.isEmpty()) {
            locationTextView.setText(location);
            locationTextView.setOnClickListener(v -> startMapsActivityForPattonvilleLocation(location));
        } else {
            locationTextView.setText(R.string.no_location);
        }

        TextView extraInfoTextView = findViewById(R.id.extra_info);

        extraInfoTextView.setText(event.getCalendarEvent().getSummary());

        TextView dataSourcesTextView = findViewById(R.id.datasources);
        dataSourcesTextView.setText(getDataSourcesSpannableStringBuilder(event.getDataSources(), getApplicationContext()));
    }

    private void startMapsActivityForPattonvilleLocation(String location) {
        Uri gmmIntentUri = Uri.parse("geo:" + PATTONVILLE_COORDINATES + "?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
