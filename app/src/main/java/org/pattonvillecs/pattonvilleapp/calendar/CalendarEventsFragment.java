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


import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.calendar.events.EventAdapter;
import org.pattonvillecs.pattonvilleapp.calendar.events.EventFlexibleItem;
import org.pattonvillecs.pattonvilleapp.calendar.events.FlexibleHasCalendarDay;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.utils.Utils;

import static org.pattonvillecs.pattonvilleapp.calendar.data.CalendarParsingUpdateData.CALENDAR_LISTENER_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarEventsFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String TAG = "CalendarEventsFragment";
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private TreeSet<EventFlexibleItem> calendarData = new TreeSet<>();
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
        pattonvilleApplication.registerPauseableListener(listener);
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
                recyclerView.scrollToPosition(finalLastBeforeToday);
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
        inflater.inflate(R.menu.search_icon_menu, menu);
        initSearchView(menu);
    }

    /**
     * Method to setup the search functionality of the list
     * <p>
     * Refer to the Flexible Adapter documentation, as this is a near replica implementation
     *
     * @param menu Menu object of current options menu
     */
    private void initSearchView(final Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {

            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    MenuItem listTypeItem = menu.findItem(R.id.news_menu_refresh);
                    if (listTypeItem != null)
                        listTypeItem.setVisible(false);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    MenuItem listTypeItem = menu.findItem(R.id.news_menu_refresh);
                    if (listTypeItem != null)
                        listTypeItem.setVisible(true);
                    return true;
                }
            });

            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_FULLSCREEN);
            searchView.setQueryHint(getString(R.string.action_search));
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_calendar_events, container, false);

        recyclerView = (RecyclerView) layout.findViewById(R.id.event_recycler_view);

        eventAdapter = new EventAdapter(null);
        recyclerView.setAdapter(eventAdapter);
        //TopSnappedSmoothScroller.MILLISECONDS_PER_INCH = 15;
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        //TopSnappedSmoothScroller.MILLISECONDS_PER_INCH = 100;
        eventAdapter.setDisplayHeadersAtStartUp(true);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                eventAdapter.setStickyHeaders(true);
            }
        });

        fastScroller = (FastScroller) layout.findViewById(R.id.fast_scroller);
        eventAdapter.setFastScroller(fastScroller, Utils.fetchAccentColor(getContext(), Color.RED)); // Default red to show an error

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

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

        noItemsTextView = (TextView) layout.findViewById(R.id.no_events_textview);

        firstInflationAfterCreation = savedInstanceState == null;

        return layout;
    }

    public void setCalendarData(TreeSet<EventFlexibleItem> calendarData) {
        this.calendarData = calendarData;

        if (calendarData.size() > 0)
            noItemsTextView.setVisibility(View.GONE);
        else
            noItemsTextView.setVisibility(View.VISIBLE);

        ArrayList<FlexibleHasCalendarDay> data = new ArrayList<FlexibleHasCalendarDay>(calendarData);
        Log.i(TAG, "Received data: " + data);

        eventAdapter.updateDataSet(data, true);
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (eventAdapter.hasNewSearchText(newText)) {
            Log.d(TAG, "onQueryTextChange newText: " + newText);
            eventAdapter.setSearchText(newText);
            eventAdapter.filterItems(new ArrayList<FlexibleHasCalendarDay>(calendarData), 100L);
        }
        return true;
    }
}
