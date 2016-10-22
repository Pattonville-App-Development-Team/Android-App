package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.pattonvillecs.pattonvilleapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 10/18/2016.
 */

public class SingleDayEventAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private CalendarDay currentCalendarDay;
    private int count;
    private List<CalendarEvent> calendarEvents;

    public SingleDayEventAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        calendarEvents = new ArrayList<>();
    }

    public void setCurrentCalendarDay(CalendarDay newCalendarDay) {
        currentCalendarDay = newCalendarDay;
        count = newCalendarDay.getDay();
        calendarEvents.clear();
        DateFormat simpleDateFormat = SimpleDateFormat.getDateInstance();
        for (int i = 0; i < count; i++) {
            calendarEvents.add(new CalendarEvent("Date: " + simpleDateFormat.format(currentCalendarDay.getDate()) + " TEST" + i, currentCalendarDay.getDate(), "eventDetails", "Pattonville High School"));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
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
            view = mLayoutInflater.inflate(R.layout.dateless_event_list_item, parent, false);

        TextView topText = (TextView) view.findViewById(R.id.text_top);
        TextView bottomText = (TextView) view.findViewById(R.id.text_bottom);

        topText.setText(calendarEvents.get(position).getEventName());
        bottomText.setText("Smaller this time... " + calendarEvents.get(position).getEventName());

        return view;
    }
}
