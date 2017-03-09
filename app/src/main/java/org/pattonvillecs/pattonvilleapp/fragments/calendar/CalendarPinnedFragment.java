package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.google.common.collect.HashMultimap;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventAdapter;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventFlexibleItem;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.FlexibleHasCalendarDay;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.pinned.PinnedEventsContract;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

import static org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData.CALENDAR_LISTENER_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarPinnedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarPinnedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = CalendarPinnedFragment.class.getSimpleName();
    private static final int PINNED_EVENTS_LOADER_ID = 1;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private TextView noItemsTextView;

    private PattonvilleApplication pattonvilleApplication;
    private PauseableListener<CalendarParsingUpdateData> listener;
    private Loader<Cursor> cursorLoader;

    private Optional<Set<String>> pinnedUIDs = Optional.empty();
    private Optional<ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>>> calendarData = Optional.empty();

    public CalendarPinnedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarPinnedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarPinnedFragment newInstance() {
        CalendarPinnedFragment fragment = new CalendarPinnedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        pattonvilleApplication.registerPauseableListener(listener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cursorLoader = getLoaderManager().initLoader(PINNED_EVENTS_LOADER_ID, null, this);
    }

    private void setCalendarData(ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendarData) {
        this.calendarData = Optional.ofNullable(calendarData);
        updatePinnedContent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_calendar_pinned, container, false);


        recyclerView = (RecyclerView) layout.findViewById(R.id.event_recycler_view);

        eventAdapter = new EventAdapter(null);

        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));

        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(dividerItemDecoration);

        noItemsTextView = (TextView) layout.findViewById(R.id.no_pinned_items_textview);

        return layout;
    }

    private void updatePinnedContent() {
        if ((!pinnedUIDs.isPresent() || pinnedUIDs.get().size() == 0) || // If pinned events are empty or gone
                (!calendarData.isPresent() || calendarData.get().size() == 0)) // OR if calendar events are empty or gone
        {
            noItemsTextView.setVisibility(View.VISIBLE);
        } else
            //There is definitely stuff to display now
            noItemsTextView.setVisibility(View.GONE);

        final Set<String> uids = pinnedUIDs.orElse(new HashSet<String>());

        List<EventFlexibleItem> items = calendarData.map(new Function<ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>>, List<EventFlexibleItem>>() {
            @Override
            public List<EventFlexibleItem> apply(ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendarData) {
                return CalendarParsingUpdateData.getAllEvents(calendarData);
            }
        }).orElse(new ArrayList<EventFlexibleItem>());

        items = Stream.of(items).filter(new Predicate<EventFlexibleItem>() {
            @Override
            public boolean test(EventFlexibleItem value) {
                return uids.contains(value.vEvent.getUid().getValue());
            }
        }).collect(Collectors.<EventFlexibleItem>toList());

        eventAdapter.updateDataSet(new ArrayList<FlexibleHasCalendarDay>(items), true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener.unattach();
        pattonvilleApplication.unregisterPauseableListener(listener);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PINNED_EVENTS_LOADER_ID:
                return new CursorLoader(this.getContext(), PinnedEventsContract.PinnedEventsTable.CONTENT_URI, null, null, null, null);
            default:
                throw new IllegalArgumentException("Unsupported ID!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished");
        Set<String> uids = new HashSet<>();
        while (data.moveToNext()) {
            uids.add(data.getString(1));
        }
        Log.i(TAG, "Data: " + uids);
        setPinnedData(uids);
    }

    private void setPinnedData(Set<String> uids) {
        pinnedUIDs = Optional.ofNullable(uids);
        updatePinnedContent();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Loader reset");
    }
}
