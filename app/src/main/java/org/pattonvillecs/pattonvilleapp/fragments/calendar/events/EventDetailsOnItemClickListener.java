package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarEventDetailsActivity;

import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * Created by skaggsm on 1/9/17.
 */

public class EventDetailsOnItemClickListener implements FlexibleAdapter.OnItemClickListener {
    private final EventAdapter eventAdapter;
    private final Activity activity;

    public EventDetailsOnItemClickListener(EventAdapter eventAdapter, Activity activity) {
        this.eventAdapter = eventAdapter;
        this.activity = activity;
    }

    @Override
    public boolean onItemClick(int position) {
        VEvent calendarVEvent = eventAdapter.getItem(position).pair.getValue();
        View view = eventAdapter.getRecyclerView().getChildAt(position).findViewById(R.id.school_color_circle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(new Intent(activity, CalendarEventDetailsActivity.class).putExtra("calendarEvent", new CalendarEvent(calendarVEvent)), ActivityOptions.makeSceneTransitionAnimation(activity, view, "color").toBundle());
        } else {
            activity.startActivity(new Intent(activity, CalendarEventDetailsActivity.class).putExtra("calendarEvent", new CalendarEvent(calendarVEvent)));
        }
        return false;
    }
}
