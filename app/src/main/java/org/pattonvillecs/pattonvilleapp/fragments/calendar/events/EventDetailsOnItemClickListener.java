package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.util.Pair;
import android.view.View;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.DataSource;
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
        org.apache.commons.lang3.tuple.Pair<DataSource, VEvent> pair = eventAdapter.getItem(position).pair;
        CalendarEvent calendarEvent = new CalendarEvent(pair);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View listItem = eventAdapter.getRecyclerView().getChildAt(position);
            View schoolColorCircle = listItem.findViewById(R.id.school_color_circle);
            View textTop = listItem.findViewById(R.id.text_top);
            View textBottom = listItem.findViewById(R.id.text_bottom);

            activity.startActivity(new Intent(activity, CalendarEventDetailsActivity.class).putExtra("calendarEvent", calendarEvent), ActivityOptions.makeSceneTransitionAnimation(activity,
                    new Pair<>(textTop, "text_top"),
                    new Pair<>(textBottom, "text_bottom"),
                    new Pair<>(schoolColorCircle, "school_color_circle")
            ).toBundle());
        } else {
            activity.startActivity(new Intent(activity, CalendarEventDetailsActivity.class).putExtra("calendarEvent", calendarEvent));
        }
        return false;
    }
}
