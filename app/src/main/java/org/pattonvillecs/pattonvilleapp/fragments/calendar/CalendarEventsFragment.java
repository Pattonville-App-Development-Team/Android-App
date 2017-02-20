package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.google.common.collect.HashMultimap;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventAdapter;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventFlexibleItem;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventHeader;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.FlexibleHasCalendarDay;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.utils.Utils;

import static org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData.CALENDAR_LISTENER_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarEventsFragment extends Fragment implements SwipeRefreshLayout.OnChildScrollUpCallback {
    private static final String TAG = "CalendarEventsFragment";
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private ConcurrentMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> calendarData = new ConcurrentHashMap<>();
    private PattonvilleApplication pattonvilleApplication;
    private PauseableListener<CalendarParsingUpdateData> listener;
    private TextView noItemsTextView;
    private FastScroller fastScroller;
    private CalendarFragment calendarFragment;

    public CalendarEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarEventsFragment.
     */
    public static CalendarEventsFragment newInstance() {
        return new CalendarEventsFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        calendarFragment = (CalendarFragment) getParentFragment();
        pattonvilleApplication = PattonvilleApplication.get(getActivity());
        listener = new PauseableListener<CalendarParsingUpdateData>(true) {
            @Override
            public int getIdentifier() {
                return CALENDAR_LISTENER_ID;
            }

            @Override
            public void onReceiveData(CalendarParsingUpdateData data) {
                super.onReceiveData(data);
                Log.i(TAG, "Received new data!");

                setCalendarData(data.getCalendarData());
            }

            @Override
            public void onResume(CalendarParsingUpdateData data) {
                super.onResume(data);
                Log.i(TAG, "Received data after resume!");

                setCalendarData(data.getCalendarData());
            }

            @Override
            public void onPause(CalendarParsingUpdateData data) {
                super.onPause(data);
                Log.i(TAG, "Received data before pause!");
            }
        };
    }

    private void goToCurrentDay() {
        int mostRecentEventPosition = 0;
        Date today = new Date();
        Log.i(TAG, "Today is: " + SimpleDateFormat.getDateInstance().format(today));

        for (int i = 0; i < eventAdapter.getItemCount(); i++) {
            Date eventDate = eventAdapter.getItem(i).getCalendarDay().getDate();
            if (!eventDate.after(today))
                mostRecentEventPosition = i;
            else {
                mostRecentEventPosition = i;
                break;
            }
        }

        recyclerView.scrollToPosition(mostRecentEventPosition);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        listener.unattach();
        pattonvilleApplication.unregisterPauseableListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_goto_today:
                goToCurrentDay();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_calendar_action_bar_menu_goto_today, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_calendar_events, container, false);

        recyclerView = (RecyclerView) layout.findViewById(R.id.event_recycler_view);

        FlexibleAdapter.enableLogs(true);
        eventAdapter = new EventAdapter(null);
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        eventAdapter.setDisplayHeadersAtStartUp(true);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                eventAdapter.setStickyHeaders(true);
            }
        });

        fastScroller = (FastScroller) layout.findViewById(R.id.fast_scroller);
        eventAdapter.setFastScroller(fastScroller, Utils.fetchAccentColor(getContext(), Color.RED));

        noItemsTextView = (TextView) layout.findViewById(R.id.no_items_textview);

        if (savedInstanceState != null)
            goToCurrentDay();

        return layout;
    }

    public void setCalendarData(ConcurrentMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> calendarData) {
        this.calendarData = calendarData;
        List<FlexibleHasCalendarDay> items = Stream.of(calendarData.entrySet())
                .flatMap(new Function<Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>>, Stream<Pair<DataSource, VEvent>>>() {
                    @Override
                    public Stream<Pair<DataSource, VEvent>> apply(final Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> dataSourceHashMultimapEntry) {
                        return Stream.of(dataSourceHashMultimapEntry.getValue().entries()).map(new Function<Map.Entry<SerializableCalendarDay, VEvent>, Pair<DataSource, VEvent>>() {
                            @Override
                            public Pair<DataSource, VEvent> apply(Map.Entry<SerializableCalendarDay, VEvent> calendarDayVEventEntry) {
                                return new ImmutablePair<>(dataSourceHashMultimapEntry.getKey(), calendarDayVEventEntry.getValue());
                            }
                        });
                    }
                })
                .sorted(new Comparator<Pair<DataSource, VEvent>>() {
                    @Override
                    public int compare(Pair<DataSource, VEvent> o1, Pair<DataSource, VEvent> o2) {
                        //This is Google Calendar style scrolling: future events to the bottom
                        return o1.getValue().getStartDate().getDate().compareTo(o2.getValue().getStartDate().getDate());
                    }
                })
                .map(new Function<Pair<DataSource, VEvent>, EventFlexibleItem>() {
                    @Override
                    public EventFlexibleItem apply(Pair<DataSource, VEvent> dataSourceVEventPair) {
                        return new EventFlexibleItem(dataSourceVEventPair.getKey(), dataSourceVEventPair.getValue());
                    }
                })
                .collect(Collectors.<FlexibleHasCalendarDay>toList());

        Map<CalendarDay, EventHeader> headers = new HashMap<>();
        for (FlexibleHasCalendarDay flexibleHasCalendarDay : items) {
            if (flexibleHasCalendarDay instanceof EventFlexibleItem) {
                EventFlexibleItem eventFlexibleItem = (EventFlexibleItem) flexibleHasCalendarDay;

                CalendarDay calendarDay = eventFlexibleItem.getCalendarDay();

                if (!headers.containsKey(calendarDay)) {
                    Log.i(TAG, "Making header for " + calendarDay);
                    headers.put(calendarDay, new EventHeader(calendarDay));
                }

                eventFlexibleItem.setHeader(headers.get(calendarDay));
            }
        }

        if (items.size() > 0)
            noItemsTextView.setVisibility(View.GONE);
        else
            noItemsTextView.setVisibility(View.VISIBLE);

        eventAdapter.updateDataSet(items, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        listener.resume();
    }

    @Override
    public void onStart() {
        super.onStart();
        listener.attach(pattonvilleApplication);
    }

    @Override
    public void onPause() {
        super.onPause();
        listener.pause();
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
        if (recyclerView != null)
            return recyclerView.canScrollVertically(-1);
        else
            return false;
    }
}
