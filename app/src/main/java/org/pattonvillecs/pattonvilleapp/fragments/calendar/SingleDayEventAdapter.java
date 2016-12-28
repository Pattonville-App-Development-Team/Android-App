package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.map.MultiValueMap;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PreferenceUtils;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mitchell on 10/18/2016.
 */

public class SingleDayEventAdapter extends BaseAdapter {
    private static final String TAG = SingleDayEventAdapter.class.getSimpleName();
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final FixedMaterialCalendarView mMaterialCalendarView;
    private final Activity mActivity;
    private CalendarDay currentCalendarDay;
    private List<VEvent> calendarEvents;
    private MultiValueMap<CalendarDay, VEvent> parsedCalendarEvents;

    public SingleDayEventAdapter(Activity activity, final Context context, final FixedMaterialCalendarView materialCalendarView, RequestQueue requestQueue) {
        mContext = context;
        mActivity = activity;
        mLayoutInflater = LayoutInflater.from(context);
        this.mMaterialCalendarView = materialCalendarView;
        this.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                Log.e(TAG, "Dataset changed!");
                SingleDayEventAdapter.this.mMaterialCalendarView.invalidateDecorators();
            }
        });
        calendarEvents = new ArrayList<>();
        parsedCalendarEvents = MultiValueMap.multiValueMap(new HashMap<CalendarDay, HashSet<VEvent>>(), new Factory<HashSet<VEvent>>() {
            @Override
            public HashSet<VEvent> create() {
                return new HashSet<>();
            }
        });

        Set<DataSource> selectedSchools = PreferenceUtils.getSelectedSchoolsSet(mContext);
        for (DataSource source : selectedSchools) {
            requestQueue.add(new StringRequest(source.dataLink, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    StringReader stringReader = new StringReader(fixICalStrings(response));
                    CalendarBuilder calendarBuilder = new CalendarBuilder();
                    Calendar calendar = null;
                    try {
                        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
                        calendar = calendarBuilder.build(stringReader);
                    } catch (IOException | ParserException e) {
                        e.printStackTrace();
                    }
                    if (calendar != null) {
                        Set<VEvent> vEventSet = Stream.of(calendar.getComponents()).filter(new Predicate<CalendarComponent>() {
                            @Override
                            public boolean test(CalendarComponent value) {
                                return value instanceof VEvent;
                            }
                        }).map(new Function<CalendarComponent, VEvent>() {
                            @Override
                            public VEvent apply(CalendarComponent value) {
                                return (VEvent) value;
                            }
                        }).collect(Collectors.<VEvent>toSet());
                        for (VEvent vEvent : vEventSet) {
                            Date vEventDate = vEvent.getStartDate().getDate();
                            parsedCalendarEvents.put(CalendarDay.from(vEventDate), vEvent);
                        }
                        materialCalendarView.removeDecorators();
                        materialCalendarView.addDecorator(new DayViewDecorator() {
                            float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mContext.getResources().getDisplayMetrics());

                            @Override
                            public boolean shouldDecorate(CalendarDay day) {
                                return parsedCalendarEvents.containsKey(day);
                            }

                            @Override
                            public void decorate(DayViewFacade view) {
                                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY, context.getResources().getInteger(android.R.integer.config_shortAnimTime), null);
                                view.setSelectionDrawable(stateListDrawable);

                                //materialCalendarView.getChildAt(1).getWidth() / 7f / 10f
                                view.addSpan(new DotSpan(radius, CalendarDecoratorUtil.getThemeAccentColor(mContext)));
                            }
                        });
                        updateEventList();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.getLocalizedMessage());
                }
            }));
        }
    }

    static String fixICalStrings(String iCalString) {
        return iCalString.replace("FREQ=;", "FREQ=YEARLY;");
    }

    public void setCurrentCalendarDay(CalendarDay newCalendarDay) {
        Log.e(TAG, "Setting current calendar day " + newCalendarDay);
        currentCalendarDay = newCalendarDay;
        calendarEvents.clear();
        updateEventList();
    }

    private void updateEventList() {
        if (parsedCalendarEvents.containsKey(currentCalendarDay))
            calendarEvents.addAll(parsedCalendarEvents.getCollection(currentCalendarDay));
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
            view = mLayoutInflater.inflate(R.layout.dateless_event_list_item, parent, false);

        TextView topText = (TextView) view.findViewById(R.id.text_top);
        TextView bottomText = (TextView) view.findViewById(R.id.text_bottom);

        VEvent calendarEvent = calendarEvents.get(position);
        Summary summary = calendarEvent.getSummary();
        Location location = calendarEvent.getLocation();

        if (summary != null)
            topText.setText(String.valueOf(summary.getValue()));
        else
            topText.setText(R.string.no_summary);

        if (location != null)
            bottomText.setText(String.valueOf(location.getValue()));
        else
            bottomText.setText(R.string.no_location);

        return view;
    }
}
