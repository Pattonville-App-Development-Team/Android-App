package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.content.Context;
import android.content.Intent;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarEventDetailsActivity;

import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * Created by skaggsm on 1/9/17.
 */

public class EventDetailsOnItemClickListener implements FlexibleAdapter.OnItemClickListener {
    private final EventAdapter eventAdapter;
    private final Context context;

    public EventDetailsOnItemClickListener(EventAdapter eventAdapter, Context context) {
        this.eventAdapter = eventAdapter;
        this.context = context;
    }

    @Override
    public boolean onItemClick(int position) {
        VEvent calendarVEvent = eventAdapter.getItem(position).pair.getValue();
        context.startActivity(new Intent(context, CalendarEventDetailsActivity.class).putExtra("calendarEvent", new CalendarEvent(calendarVEvent)));
        return false;
    }
}
