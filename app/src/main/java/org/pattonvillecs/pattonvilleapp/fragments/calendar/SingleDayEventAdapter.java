package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.collections4.map.MultiValueMap;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Mitchell on 10/18/2016.
 */

public class SingleDayEventAdapter extends BaseAdapter {
    private static final String TAG = SingleDayEventAdapter.class.getSimpleName();
    private final LayoutInflater layoutInflater;
    private CalendarDay currentCalendarDay;
    private List<VEvent> calendarEvents;

    public SingleDayEventAdapter(final Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.calendarEvents = new ArrayList<>();
    }

    public void setCurrentCalendarDay(CalendarDay newCalendarDay, CalendarData calendarData) {
        Log.e(TAG, "Setting current calendar day " + newCalendarDay);
        currentCalendarDay = newCalendarDay;
        calendarEvents.clear();
        for (Map.Entry<DataSource, MultiValueMap<CalendarDay, VEvent>> entry : calendarData.getCalendars().entrySet()) {
            Collection<VEvent> events = entry.getValue().getCollection(currentCalendarDay);
            if (events != null)
                calendarEvents.addAll(events);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return calendarEvents.size();
    }

    @Override
    public VEvent getItem(int position) {
        return calendarEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = layoutInflater.inflate(R.layout.dateless_event_list_item, parent, false);

        TextView topText = (TextView) view.findViewById(R.id.text_top);
        TextView bottomText = (TextView) view.findViewById(R.id.text_bottom);

        VEvent calendarEvent = calendarEvents.get(position);
        Summary summary = calendarEvent.getSummary();
        Location location = calendarEvent.getLocation();

        if (summary != null)
            topText.setText(summary.getValue());
        else
            topText.setText(R.string.no_summary);

        if (location != null) {
            bottomText.setVisibility(View.VISIBLE);
            bottomText.setText(location.getValue());
        } else {
            bottomText.setVisibility(View.GONE);
            bottomText.setText(R.string.no_location);
        }

        return view;
    }
}
