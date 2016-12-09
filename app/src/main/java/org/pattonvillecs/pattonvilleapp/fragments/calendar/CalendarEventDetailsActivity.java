package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.pattonvillecs.pattonvilleapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CalendarEventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CalendarEvent calendarEvent = getIntent().getParcelableExtra("calendarEvent");
        if (calendarEvent == null)
            throw new Error("Event required!");

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(calendarEvent.getEventName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(view, "Added to your calendar", Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(Color.DKGRAY);
                /*if (Build.VERSION.SDK_INT >= 23)
                    snackbar = snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent, getTheme()));
                else
                    snackbar = snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));*/
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(CalendarEventDetailsActivity.this, "Sorry, undo is not supported at this time.", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        TextView timeDateTextView = (TextView) findViewById(R.id.time_and_date);
        DateFormat dateFormatter = SimpleDateFormat.getDateInstance();
        DateFormat timeFormatter = SimpleDateFormat.getTimeInstance();
        timeDateTextView.setText(dateFormatter.format(calendarEvent.getDateAndTime()) + "\n" + timeFormatter.format(calendarEvent.getDateAndTime()));
        timeDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        TextView locationTextView = (TextView) findViewById(R.id.location);
        if (calendarEvent.getEventLocation().isEmpty()) {
            locationTextView.setText(calendarEvent.getEventLocation());
            locationTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:38.733249,-90.420162?q=" + Uri.encode(calendarEvent.getEventLocation()));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        } else {
            locationTextView.setText(R.string.no_location);
        }

        TextView extraInfoTextView = (TextView) findViewById(R.id.extra_info);
        extraInfoTextView.setText(calendarEvent.getEventName() + "\n" + calendarEvent.getEventDetails());
    }
}
