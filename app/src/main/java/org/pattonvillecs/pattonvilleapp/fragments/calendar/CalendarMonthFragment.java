package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.SpotlightHelper;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventAdapter;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventFlexibleItem;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.FlexibleHasCalendarDay;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

import static org.pattonvillecs.pattonvilleapp.SpotlightHelper.showSpotlight;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarMonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarMonthFragment extends Fragment {

    public static final String TAG = "CalendarMonthFragment";
    private static final String KEY_DATE_SELECTED = "currentDateSelected";

    private FixedMaterialCalendarView fixedMaterialCalendarView;
    private RecyclerView eventRecyclerView;
    private CalendarDay currentDateSelected;
    private EventAdapter eventAdapter;
    private TreeSet<EventFlexibleItem> calendarData = new TreeSet<>();
    private PattonvilleApplication pattonvilleApplication;
    private NestedScrollView nestedScrollView;
    private PauseableListener<CalendarParsingUpdateData> listener;


    public CalendarMonthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarMonthFragment.
     */
    public static CalendarMonthFragment newInstance() {
        Log.i(TAG, "New instance created...");
        return new CalendarMonthFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_DATE_SELECTED, currentDateSelected);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        pattonvilleApplication = PattonvilleApplication.get(getActivity());
        listener = new PauseableListener<CalendarParsingUpdateData>(true) {
            @Override
            public int getIdentifier() {
                return CalendarParsingUpdateData.CALENDAR_LISTENER_ID;
            }

            @Override
            public void onReceiveData(CalendarParsingUpdateData data) {
                super.onReceiveData(data);
                Log.i(TAG, "Received new data!");

                updateCalendarData(data.getCalendarData());
            }

            @Override
            public void onResume(CalendarParsingUpdateData data) {
                super.onResume(data);
                Log.i(TAG, "Received data after resume!");

                updateCalendarData(data.getCalendarData());
            }

            @Override
            public void onPause(CalendarParsingUpdateData data) {
                super.onPause(data);
                Log.i(TAG, "Received data before pause!");
            }
        };
        pattonvilleApplication.registerPauseableListener(listener);
    }

    private void updateCalendarData(TreeSet<EventFlexibleItem> calendarData) {
        this.calendarData = calendarData;
        fixedMaterialCalendarView.invalidateDecorators();
        setRecyclerViewItems(currentDateSelected);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener.unattach();
        pattonvilleApplication.unregisterPauseableListener(listener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_calendar_action_bar_menu_goto_today, menu);

        //This terrifies me...
        final ViewTreeObserver viewTreeObserver = getActivity().getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View menuButton = getActivity().findViewById(R.id.action_goto_today);
                // This could be called when the button is not there yet, so we must test for null
                if (menuButton != null) {
                    // Found it! Do what you need with the button
                    showSpotlight(getActivity(), menuButton, "CalendarMonthFragment_MenuButtonGoToCurrentDay", "Touch this button to return to the current day.", "Today");
                    // Now you can get rid of this listener
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_goto_today:
                setDisplayedDayToDay(CalendarDay.today());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDisplayedDayToDay(CalendarDay calendarDay) {
        fixedMaterialCalendarView.setCurrentDate(calendarDay);
        //fixedMaterialCalendarView.dispatchOnDateSelected(calendarDay, true);
    }

    private List<FlexibleHasCalendarDay> getItemsForDay(@NonNull CalendarDay calendarDay) {
        List<FlexibleHasCalendarDay> events = new ArrayList<>();
        for (EventFlexibleItem item : calendarData) {
            if (calendarDay.equals(item.getCalendarDay())) {
                events.add(item);
            }
        }
        return events;
    }

    private void setRecyclerViewItems(CalendarDay date) {
        eventAdapter.updateDataSet(getItemsForDay(date), true);
    }

    private float getDotRadius() {
        if (fixedMaterialCalendarView != null)
            return Math.max(
                    5, //Minimum radius of 5
                    Math.min(
                            fixedMaterialCalendarView.getWidth(),
                            fixedMaterialCalendarView.getHeight()
                    ) / 150f
            );
        else
            return 10;
    }

    private boolean containsEventsOnDate(CalendarDay calendarDay, int toFind, boolean useGreaterThan) {
        int numPresent = 0;
        for (EventFlexibleItem item : calendarData)
            if (calendarDay.isAfter(item.getCalendarDay()))
                continue;
            else if (calendarDay.equals(item.getCalendarDay())) {
                numPresent += item.dataSources.size();
                if (numPresent > toFind)
                    return useGreaterThan;
            } else if (calendarDay.isBefore(item.getCalendarDay())) {
                break;
            }
        if (useGreaterThan)
            return numPresent > toFind;
        else
            return numPresent == toFind;
    }

    private void setUpMaterialCalendarView() {
        Log.d(TAG, "Starting MCV setup");

        //fixedMaterialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        fixedMaterialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                currentDateSelected = date;
                setRecyclerViewItems(date);
            }
        });
        fixedMaterialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                if (nestedScrollView != null) {
                    nestedScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            nestedScrollView.fullScroll(View.FOCUS_UP);
                        }
                    });
                }
            }
        });
        fixedMaterialCalendarView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                Log.d(TAG, "MCV New layout: " + left + " " + top + " " + right + " " + bottom + "; Old layout: " + oldLeft + " " + oldTop + " " + oldRight + " " + oldBottom);
                Log.d(TAG, "MCV tile height: " + fixedMaterialCalendarView.getTileHeight());

                if (left != oldLeft
                        || top != oldTop
                        || right != oldRight
                        || bottom != oldBottom)
                    fixedMaterialCalendarView.post(new Runnable() {
                        @Override
                        public void run() {
                            fixedMaterialCalendarView.invalidateDecorators();
                        }
                    });
            }
        });

        final int dotColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getActivity().getTheme());

        fixedMaterialCalendarView.addDecorators(
                //Single decorator
                new DayViewDecorator() {

                    @Override
                    public boolean shouldDecorate(final CalendarDay day) {
                        return calendarData != null && containsEventsOnDate(day, 1, false);

                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        //StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY);
                        //view.setSelectionDrawable(stateListDrawable);

                        view.addSpan(EnhancedDotSpan.createSingle(getDotRadius(), dotColor));
                    }
                },
                //Double decorator
                new DayViewDecorator() {

                    @Override
                    public boolean shouldDecorate(final CalendarDay day) {
                        return calendarData != null && containsEventsOnDate(day, 2, false);

                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        //StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY);
                        //view.setSelectionDrawable(stateListDrawable);

                        Pair<EnhancedDotSpan, EnhancedDotSpan> pair = EnhancedDotSpan.createPair(getDotRadius(), dotColor, dotColor);
                        view.addSpan(pair.getLeft());
                        view.addSpan(pair.getRight());
                    }
                },
                //Triple decorator
                new DayViewDecorator() {

                    @Override
                    public boolean shouldDecorate(final CalendarDay day) {
                        return calendarData != null && containsEventsOnDate(day, 3, false);

                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        //StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY);
                        //view.setSelectionDrawable(stateListDrawable);

                        Triple<EnhancedDotSpan, EnhancedDotSpan, EnhancedDotSpan> triple = EnhancedDotSpan.createTriple(getDotRadius(), dotColor, dotColor, dotColor);
                        view.addSpan(triple.getLeft());
                        view.addSpan(triple.getMiddle());
                        view.addSpan(triple.getRight());
                    }
                },
                //>Three decorator
                new DayViewDecorator() {

                    @Override
                    public boolean shouldDecorate(final CalendarDay day) {
                        return calendarData != null && containsEventsOnDate(day, 3, true);

                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        //StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY);
                        //view.setSelectionDrawable(stateListDrawable);

                        Triple<EnhancedDotSpan, EnhancedDotSpan, EnhancedDotSpan> triple = EnhancedDotSpan.createTripleWithPlus(getDotRadius(), dotColor, dotColor, dotColor);
                        view.addSpan(triple.getLeft());
                        view.addSpan(triple.getMiddle());
                        view.addSpan(triple.getRight());
                    }
                });

        Log.d(TAG, "Finished MCV setup");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        // Inflate the layout for this fragment
        View rootLayout = inflater.inflate(R.layout.fragment_calendar_month, container, false);

        //rootLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        //rootLayout.getLayoutTransition().setDuration(LayoutTransition.CHANGING, 200);

        fixedMaterialCalendarView = (FixedMaterialCalendarView) rootLayout.findViewById(R.id.calendar_calendar);
        setUpMaterialCalendarView();

        eventRecyclerView = (RecyclerView) rootLayout.findViewById(R.id.event_recycler_view);
        eventRecyclerView.setNestedScrollingEnabled(false);
        eventAdapter = new EventAdapter(null);
        eventRecyclerView.setAdapter(eventAdapter);
        eventRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        eventRecyclerView.getLayoutManager().setAutoMeasureEnabled(true);

        if (savedInstanceState != null)
            currentDateSelected = savedInstanceState.getParcelable(KEY_DATE_SELECTED);
        else
            currentDateSelected = CalendarDay.today();

        int spotlightPadding;
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                spotlightPadding = 20;
                nestedScrollView = (NestedScrollView) rootLayout;
                nestedScrollView.setSmoothScrollingEnabled(true);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                spotlightPadding = -250;
                nestedScrollView = null;
                break;
            case Configuration.ORIENTATION_UNDEFINED:
            default:
                throw new Error("Why would this ever happen?");
        }
        SpotlightHelper.showSpotlight(getActivity(), rootLayout, spotlightPadding, "CalendarMonthFragment_SelectedDayEventList", "Events occurring on the selected day are shown here.", "Events");

        return rootLayout;
    }

    @Override
    public void onStart() {
        super.onStart();

        fixedMaterialCalendarView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setDisplayedDayToDay(currentDateSelected);
            }
        }, 1000);

        listener.attach(pattonvilleApplication);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        listener.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        listener.resume();
    }
}
