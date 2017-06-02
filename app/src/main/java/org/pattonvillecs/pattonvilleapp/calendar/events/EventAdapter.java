/*
 * Copyright (C) 2017  Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, and Nathan Skelton
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
