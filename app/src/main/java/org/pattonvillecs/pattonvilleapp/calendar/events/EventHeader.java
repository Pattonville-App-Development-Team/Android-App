/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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
import android.view.View;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.pattonvillecs.pattonvilleapp.R;

import java.text.SimpleDateFormat;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by Mitchell Skaggs on 2/16/17.
 */

public class EventHeader extends AbstractHeaderItem<EventHeader.EventHeaderViewHolder> implements FlexibleHasCalendarDay<EventHeader.EventHeaderViewHolder> {
    @NonNull
    private final CalendarDay calendarDay;

    public EventHeader(@NonNull CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, EventHeaderViewHolder holder, int position, List payloads) {
        holder.headerText.setText(SimpleDateFormat.getDateInstance().format(calendarDay.getDate()));
    }

    @Override
    public int getLayoutRes() {
        return R.layout.calendar_event_date_header;
    }

    @Override
    public EventHeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new EventHeaderViewHolder(view, adapter, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventHeader that = (EventHeader) o;

        return calendarDay.equals(that.calendarDay);

    }

    @Override
    public int hashCode() {
        return calendarDay.hashCode();
    }

    @NonNull
    @Override
    public CalendarDay getCalendarDay() {
        return calendarDay;
    }

    @Override
    public int compareTo(FlexibleHasCalendarDay<?> o) {
        return EventFlexibleItem.compare(this.getCalendarDay(), o.getCalendarDay());
    }

    static class EventHeaderViewHolder extends FlexibleViewHolder {
        private final TextView headerText;

        public EventHeaderViewHolder(View view, FlexibleAdapter adapter) {
            this(view, adapter, false);
        }

        public EventHeaderViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);

            headerText = (TextView) view.findViewById(R.id.calendar_event_header_text);
        }
    }
}
