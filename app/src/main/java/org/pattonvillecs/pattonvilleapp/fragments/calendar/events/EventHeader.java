package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by skaggsm on 2/16/17.
 */

public class EventHeader extends AbstractHeaderItem<EventHeader.EventHeaderViewHolder> {
    @NonNull
    private final CalendarDay calendarDay;

    public EventHeader(@NonNull CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, EventHeaderViewHolder holder, int position, List payloads) {
    }

    @Override
    public int getLayoutRes() {
        return R.layout.calendar_event_date_header;
    }

    @Override
    public EventHeaderViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new EventHeaderViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter, true);
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

    static class EventHeaderViewHolder extends FlexibleViewHolder {
        public EventHeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
        }

        public EventHeaderViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
        }
    }
}
