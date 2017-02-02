package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.support.annotation.Nullable;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * Created by skaggsm on 1/4/17.
 */
public class EventAdapter extends FlexibleAdapter<EventFlexibleItem> {

    public EventAdapter() {
        this(new ArrayList<EventFlexibleItem>());
    }

    public EventAdapter(@Nullable List<EventFlexibleItem> items) {
        this(items, null);
    }

    public EventAdapter(@Nullable List<EventFlexibleItem> items, @Nullable Object listeners) {
        this(items, listeners, false);
    }

    public EventAdapter(@Nullable List<EventFlexibleItem> items, @Nullable Object listeners, boolean stableIds) {
        super(items, listeners, stableIds);
    }

    @Override
    public String onCreateBubbleText(int position) {
        EventFlexibleItem item = getItem(position);
        if (item != null)
            return DateFormat.getDateFormat(this.getRecyclerView().getContext()).format(item.pair.getValue().getStartDate().getDate());
        else
            return "";
    }
}
