package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.FlexibleHasCalendarDay;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.common.TopSnappedSmoothScroller;
import eu.davidea.flexibleadapter.utils.Utils;

import static org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData.CALENDAR_LISTENER_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarEventsFragment extends Fragment {
    private static final String TAG = "CalendarEventsFragment";
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendarData = new ConcurrentHashMap<>();
    private PattonvilleApplication pattonvilleApplication;
    private PauseableListener<CalendarParsingUpdateData> listener;
    private TextView noItemsTextView;
    private FastScroller fastScroller;
    private boolean firstInflationAfterCreation;

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
        int lastBeforeToday = -1;
        Date today = new Date();
        Log.i(TAG, "Today is: " + SimpleDateFormat.getDateTimeInstance().format(today));
        Log.i(TAG, "Found " + eventAdapter.getItemCount() + " items");

        for (int i = 0; i < eventAdapter.getItemCount(); i++) {
            Date eventDate = eventAdapter.getItem(i).getCalendarDay().getDate();

            if (!eventDate.before(today)) {
                lastBeforeToday = i - 1;
                break;
            }
        }

        final int finalLastBeforeToday = lastBeforeToday;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(finalLastBeforeToday);
            }
        });
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

        eventAdapter = new EventAdapter(null);
        recyclerView.setAdapter(eventAdapter);
        TopSnappedSmoothScroller.MILLISECONDS_PER_INCH = 25;
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        TopSnappedSmoothScroller.MILLISECONDS_PER_INCH = 100;
        eventAdapter.setDisplayHeadersAtStartUp(true);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                eventAdapter.setStickyHeaders(true);
            }
        });

        fastScroller = (FastScroller) layout.findViewById(R.id.fast_scroller);
        eventAdapter.setFastScroller(fastScroller, Utils.fetchAccentColor(getContext(), Color.RED)); // Default red to show an error

        //This is used to move to the current day ONCE, and never activate again during the life of the fragment. It must wait until the first update of data before running.
        eventAdapter.addListener(new FlexibleAdapter.OnUpdateListener() {
            boolean firstRun = true;

            @Override
            public void onUpdateEmptyView(int size) {
                if (firstRun && size > 0 && firstInflationAfterCreation) {
                    firstRun = false;
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            goToCurrentDay();
                        }
                    });
                }
                Log.i(TAG, "Updated with size: " + size);
            }
        });

        noItemsTextView = (TextView) layout.findViewById(R.id.no_items_textview);

        firstInflationAfterCreation = savedInstanceState == null;

        return layout;
    }

    public void setCalendarData(ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendarData) {
        this.calendarData = calendarData;
        List<EventFlexibleItem> items = Stream.of(calendarData.entrySet())
                .flatMap(new Function<Map.Entry<DataSource, HashMultimap<CalendarDay, VEvent>>, Stream<Pair<DataSource, VEvent>>>() {
                    @Override
                    public Stream<Pair<DataSource, VEvent>> apply(final Map.Entry<DataSource, HashMultimap<CalendarDay, VEvent>> dataSourceHashMultimapEntry) {
                        return Stream.of(dataSourceHashMultimapEntry.getValue().entries()).map(new Function<Map.Entry<CalendarDay, VEvent>, Pair<DataSource, VEvent>>() {
                            @Override
                            public Pair<DataSource, VEvent> apply(Map.Entry<CalendarDay, VEvent> calendarDayVEventEntry) {
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
                .collect(Collectors.<EventFlexibleItem>toList());

        if (items.size() > 0)
            noItemsTextView.setVisibility(View.GONE);
        else
            noItemsTextView.setVisibility(View.VISIBLE);

        eventAdapter.updateDataSet(new ArrayList<FlexibleHasCalendarDay>(items), true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called!");
        listener.resume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called!");
        listener.attach(pattonvilleApplication);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause called!");
        listener.pause();
    }
}
