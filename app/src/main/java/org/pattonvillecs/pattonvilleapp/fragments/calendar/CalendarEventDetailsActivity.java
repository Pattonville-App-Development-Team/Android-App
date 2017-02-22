package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventFlexibleItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CalendarEventDetailsActivity extends AppCompatActivity {

    public static final String CALENDAR_EVENT_KEY = "calendar_event";
    public static final String PATTONVILLE_COORDINATES = "38.733249,-90.420162";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_event_details);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EventFlexibleItem event = getIntent().getParcelableExtra(CALENDAR_EVENT_KEY);
        if (event == null)
            throw new Error("Event required!");

        TextView timeDateTextView = (TextView) findViewById(R.id.time_and_date);

        //DateFormat dateFormatter = SimpleDateFormat.getDateInstance();
        //DateFormat timeFormatter = SimpleDateFormat.getTimeInstance();
        DateFormat dateTimeFormatter = SimpleDateFormat.getDateTimeInstance();

        timeDateTextView.setText(dateTimeFormatter.format(event.vEvent.getStartDate().getDate()));
        timeDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        TextView locationTextView = (TextView) findViewById(R.id.location);
        if (event.vEvent.getLocation() != null) {
            final String location = event.vEvent.getLocation().getValue();
            locationTextView.setText(location);
            locationTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMapsActivityForPattonvilleLocation(location);
                }
            });
        } else {
            locationTextView.setText(R.string.no_location);
            locationTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMapsActivityForPattonvilleLocation("Pattonville High School");
                }
            });
        }

        TextView extraInfoTextView = (TextView) findViewById(R.id.extra_info);
        if (event.vEvent.getSummary() != null)
            extraInfoTextView.setText(event.vEvent.getSummary().getValue());

        ImageView schoolColorCircle = (ImageView) findViewById(R.id.school_color_circle);
        schoolColorCircle.setColorFilter(event.dataSource.calendarColor);

        //SpotlightHelper.showSpotlight(this, fab, "CalendarEventDetailsActivity_FABAddToCalendar", "Want to keep track of this event?\nAdd it to your personal calendar.", "Add To Calendar");
    }

    private void startMapsActivityForPattonvilleLocation(String location) {
        Uri gmmIntentUri = Uri.parse("geo:" + PATTONVILLE_COORDINATES + "?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
