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

import java.util.Set;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

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
        Intent intent = new Intent(activity, CalendarEventDetailsActivity.class).putExtra("calendarEvent", calendarEvent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View listItem = eventAdapter.getRecyclerView().getLayoutManager().getChildAt(position); // Should work mostly

            if (listItem == null) { // Resort to more drastic measures if the view isn't found easily
                Set<FlexibleViewHolder> boundViewHolders = eventAdapter.getAllBoundViewHolders();
                for (FlexibleViewHolder flexibleViewHolder : boundViewHolders)
                    if (flexibleViewHolder.getAdapterPosition() == position) {
                        listItem = flexibleViewHolder.itemView;
                        break;
                    }
            }

            if (listItem != null) {
                View schoolColorCircle = listItem.findViewById(R.id.school_color_circle);
                View textTop = listItem.findViewById(R.id.text_top);
                View textBottom = listItem.findViewById(R.id.text_bottom);

                activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity,
                        Pair.create(textTop, activity.getResources().getString(R.string.text_top_transition_name)),
                        Pair.create(textBottom, activity.getResources().getString(R.string.text_bottom_transition_name)),
                        Pair.create(schoolColorCircle, activity.getResources().getString(R.string.school_color_circle_transition_name))
                ).toBundle());
            }
        } else {
            activity.startActivity(intent);
        }
        return false;
    }
}
