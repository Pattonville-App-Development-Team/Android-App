package org.pattonvillecs.pattonvilleapp.calendar.events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IHeader;

/**
 * Created by Mitchell Skaggs on 1/4/17.
 */
public class EventAdapter extends FlexibleAdapter<FlexibleHasCalendarDay> {

    private final Map<CalendarDay, EventHeader> headerMap;

    public EventAdapter(@Nullable List<FlexibleHasCalendarDay> items) {
        this(items, null);
    }

    public EventAdapter(@Nullable List<FlexibleHasCalendarDay> items, @Nullable Object listeners) {
        this(items, listeners, false);
    }

    public EventAdapter(@Nullable List<FlexibleHasCalendarDay> items, @Nullable Object listeners, boolean stableIds) {
        super(items, listeners, stableIds);
        headerMap = new HashMap<>();
    }

    @Override
    public String onCreateBubbleText(int position) {
        return DateFormat.getDateFormat(this.getRecyclerView().getContext()).format(getItem(position).getCalendarDay().getDate());
    }

    @Override
    public IHeader getHeaderOf(@NonNull FlexibleHasCalendarDay item) {
        CalendarDay calendarDay = item.getCalendarDay();
        if (!headerMap.containsKey(calendarDay))
            headerMap.put(calendarDay, new EventHeader(calendarDay));
        return headerMap.get(calendarDay);
    }
}
