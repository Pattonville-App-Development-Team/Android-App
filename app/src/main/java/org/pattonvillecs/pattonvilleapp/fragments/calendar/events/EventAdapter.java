package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.support.annotation.Nullable;
import android.text.format.DateFormat;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * Created by Mitchell Skaggs on 1/4/17.
 */
public class EventAdapter extends FlexibleAdapter<FlexibleHasCalendarDay> {

    public EventAdapter(@Nullable List<FlexibleHasCalendarDay> items) {
        super(items);
    }

    public EventAdapter(@Nullable List<FlexibleHasCalendarDay> items, @Nullable Object listeners) {
        super(items, listeners);
    }

    public EventAdapter(@Nullable List<FlexibleHasCalendarDay> items, @Nullable Object listeners, boolean stableIds) {
        super(items, listeners, stableIds);
    }

    @Override
    public String onCreateBubbleText(int position) {
        return DateFormat.getDateFormat(this.getRecyclerView().getContext()).format(getItem(position).getCalendarDay().getDate());
    }
}
