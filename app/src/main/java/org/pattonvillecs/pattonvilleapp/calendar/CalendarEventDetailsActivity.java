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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Function;

import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.Location;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.SpotlightHelper;
import org.pattonvillecs.pattonvilleapp.calendar.events.EventFlexibleItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarEventDetailsActivity extends AppCompatActivity {

    public static final String CALENDAR_EVENT_KEY = "calendar_event";
    public static final String PATTONVILLE_COORDINATES = "38.733249,-90.420162";
    private static final String TAG = CalendarEventDetailsActivity.class.getSimpleName();
    private EventFlexibleItem event;

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
        Log.i(TAG, "addToCalendar: " + event.vEvent);
        Date startDate = event.vEvent.getStartDate().getDate();
        Date endDate = event.vEvent.getEndDate(true).getDate();

        Location location = event.vEvent.getLocation();
        String locationString = location != null && !location.getValue().isEmpty() ? location.getValue() : null;

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.getTime())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate.getTime())
                .putExtra(CalendarContract.Events.TITLE, event.vEvent.getSummary().getValue());

        if (locationString != null)
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, locationString);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_calendar_detail, menu);

        //This terrifies me...
        final ViewTreeObserver viewTreeObserver = getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View menuButton = findViewById(R.id.action_add_to_calendar);
                // This could be called when the button is not there yet, so we must test for null
                if (menuButton != null) {
                    // Found it! Do what you need with the button
                    SpotlightHelper.showSpotlight(CalendarEventDetailsActivity.this, menuButton, "CalendarEventDetailsActivity_FABAddToCalendar", "Want to keep track of this event?\nAdd it to your personal calendar.", "Add To Calendar");
                    // Now you can get rid of this listener
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });
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

        TextView timeDateTextView = (TextView) findViewById(R.id.time_and_date);

        DateFormat dateFormatter = SimpleDateFormat.getDateInstance(DateFormat.FULL);
        DateFormat timeFormatter = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        //DateFormat dateTimeFormatter = SimpleDateFormat.getDateTimeInstance();
        Date startDate = event.vEvent.getStartDate().getDate();
        Date endDate = Optional.ofNullable(event.vEvent.getEndDate(true)).map(new Function<DtEnd, Date>() {
            @Override
            public Date apply(DtEnd dtEnd) {
                return dtEnd.getDate();
            }
        }).orElse(startDate);

        String startDateString = dateFormatter.format(startDate);
        String endDateString = dateFormatter.format(endDate);
        String startTimeString = timeFormatter.format(startDate);
        String endTimeString = timeFormatter.format(endDate);

        String combinedDateString = startDateString.equals(endDateString) ? startDateString : startDateString + " - " + endDateString;
        String combinedTimeString = startTimeString.equals(endTimeString) ? startTimeString : startTimeString + " - " + endTimeString;

        timeDateTextView.setText(combinedDateString + '\n' + combinedTimeString);

        TextView locationTextView = (TextView) findViewById(R.id.location);
        final Location location = event.vEvent.getLocation();
        final String locationString = location != null && !location.getValue().isEmpty() ? location.getValue() : null;
        if (locationString != null) {
            locationTextView.setText(locationString);
            locationTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMapsActivityForPattonvilleLocation(locationString);
                }
            });
        } else {
            locationTextView.setText(R.string.no_location);
        }

        TextView extraInfoTextView = (TextView) findViewById(R.id.extra_info);
        if (event.vEvent.getSummary() != null)
            extraInfoTextView.setText(event.vEvent.getSummary().getValue());

        TextView dataSourcesTextView = (TextView) findViewById(R.id.datasources);
        dataSourcesTextView.setText(event.getDataSourcesSpannableStringBuilder(getApplicationContext().getAssets()));
    }

    private void startMapsActivityForPattonvilleLocation(String location) {
        Uri gmmIntentUri = Uri.parse("geo:" + PATTONVILLE_COORDINATES + "?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
