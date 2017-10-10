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

package org.pattonvillecs.pattonvilleapp.calendar;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.calendar.events.EventAdapter;
import org.pattonvillecs.pattonvilleapp.calendar.events.EventFlexibleItem;
import org.pattonvillecs.pattonvilleapp.calendar.pinned.PinnedEventsContract;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

import static org.pattonvillecs.pattonvilleapp.calendar.data.CalendarParsingUpdateData.CALENDAR_LISTENER_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarPinnedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarPinnedFragment extends DaggerFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PINNED_EVENTS_LOADER_ID = 1;
    private static final String TAG = CalendarPinnedFragment.class.getSimpleName();
    private static final String KEY_PINNED_UIDS = "pinned_uids";

    @Inject
    protected CalendarRepository calendarDao;

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private TextView noItemsTextView;

    private PattonvilleApplication pattonvilleApplication;
    private PauseableListener<CalendarParsingUpdateData> listener;

    private Set<String> pinnedUIDs = new HashSet<>();
    private NavigableSet<EventFlexibleItem> calendarData = new TreeSet<>();

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
        getLoaderManager().initLoader(PINNED_EVENTS_LOADER_ID, null, this);
    }

    private void setCalendarData(TreeSet<EventFlexibleItem> calendarData) {
        this.calendarData.clear();
        this.calendarData.addAll(calendarData);
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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        noItemsTextView = (TextView) layout.findViewById(R.id.no_pinned_items_textview);

        return layout;
    }

    private void updatePinnedContent() {
        if ((pinnedUIDs.size() == 0) || // If pinned events are empty or gone
                (calendarData.size() == 0)) // OR if calendar events are empty or gone
        {
            noItemsTextView.setVisibility(View.VISIBLE);
        } else
            //There is definitely stuff to display now
            noItemsTextView.setVisibility(View.GONE);

        List<EventFlexibleItem> items = new ArrayList<>(calendarData);

        Iterators.removeIf(items.iterator(), new Predicate<EventFlexibleItem>() {
            @Override
            public boolean apply(EventFlexibleItem input) {
                return !pinnedUIDs.contains(input.vEvent.getUid().getValue());
            }
        });

        eventAdapter.updateDataSet(new ArrayList<>(items), true);
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
        getLoaderManager().restartLoader(PINNED_EVENTS_LOADER_ID, null, this);
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

    private void setPinnedData(Collection<String> uids) {
        Log.i(TAG, "setPinnedData: News uids: " + uids);
        pinnedUIDs.clear();
        pinnedUIDs.addAll(uids);
        updatePinnedContent();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Loader reset");
    }
}
