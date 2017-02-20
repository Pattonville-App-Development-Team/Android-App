package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.support.v7.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * Created by skaggsm on 2/16/17.
 */

public interface FlexibleHasCalendarDay<VH extends RecyclerView.ViewHolder> extends IFlexible<VH> {
    CalendarDay getCalendarDay();
}
