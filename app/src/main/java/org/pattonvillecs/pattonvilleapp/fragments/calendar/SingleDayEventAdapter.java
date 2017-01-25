package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.google.common.collect.HashMultimap;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarData;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;

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
    private List<Pair<DataSource, VEvent>> calendarEvents;

    public SingleDayEventAdapter(final Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.calendarEvents = new ArrayList<>();
    }

    public void setCurrentCalendarDay(SerializableCalendarDay newCalendarDay, CalendarData calendarData) {
        Log.i(TAG, "Setting current calendar day " + newCalendarDay);
        calendarEvents.clear();
        for (final Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.getCalendars().entrySet()) {
            final Collection<VEvent> events = entry.getValue().get(newCalendarDay);
            if (events != null)
                calendarEvents.addAll(
                        Stream.of(events)
                                .map(new Function<VEvent, Pair<DataSource, VEvent>>() {
                                    @Override
                                    public Pair<DataSource, VEvent> apply(VEvent vEvent) {
                                        return new ImmutablePair<>(entry.getKey(), vEvent);
                                    }
                                })
                                .collect(Collectors.<Pair<DataSource, VEvent>>toList()));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return calendarEvents.size();
    }

    @Override
    public Pair<DataSource, VEvent> getItem(int position) {
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
            view = layoutInflater.inflate(R.layout.calendar_dateless_event_list_item, parent, false);

        TextView topText = (TextView) view.findViewById(R.id.text_top);
        TextView bottomText = (TextView) view.findViewById(R.id.text_bottom);

        Pair<DataSource, VEvent> pair = calendarEvents.get(position);
        VEvent calendarEvent = pair.getValue();
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
        ImageView schoolColorImageView = (ImageView) view.findViewById(R.id.school_color_circle);
        schoolColorImageView.setColorFilter(pair.getKey().calendarColor);

        return view;
    }
}
