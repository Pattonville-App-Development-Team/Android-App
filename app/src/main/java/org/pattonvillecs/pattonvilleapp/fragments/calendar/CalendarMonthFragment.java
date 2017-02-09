package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.common.collect.HashMultimap;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.SpotlightHelper;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventAdapter;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventDetailsOnItemClickListener;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventFlexibleItem;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

import static org.pattonvillecs.pattonvilleapp.SpotlightHelper.showSpotlight;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarMonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarMonthFragment extends Fragment implements SwipeRefreshLayout.OnChildScrollUpCallback {

    public static final String TAG = "CalendarMonthFragment";
    private static final Method onDateClickedMethod;

    static {
        Method method = null;
        try {
            method = MaterialCalendarView.class.getDeclaredMethod("onDateClicked", CalendarDay.class, boolean.class);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        onDateClickedMethod = method;
    }

    private FixedMaterialCalendarView materialCalendarView;
    private RecyclerView eventRecyclerView;
    private CalendarDay dateSelected;
    private EventAdapter eventAdapter;
    private ConcurrentMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> calendarData = new ConcurrentHashMap<>();
    private PattonvilleApplication pattonvilleApplication;
    private NestedScrollView nestedScrollView;
    private PauseableListener<CalendarParsingUpdateData> listener;
    private CalendarFragment calendarFragment;


    public CalendarMonthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarMonthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarMonthFragment newInstance() {
        Log.i(TAG, "New instance created...");
        return new CalendarMonthFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("dateSelected", dateSelected);
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
                return CalendarParsingUpdateData.CALENDAR_LISTENER_ID;
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

    private void setCalendarData(ConcurrentMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> calendarData) {
        this.calendarData = calendarData;
        materialCalendarView.invalidateDecorators();
        setRecyclerViewItems(dateSelected);
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
                CalendarDay today = CalendarDay.today();
                materialCalendarView.setCurrentDate(today);
                callOnDateClicked(materialCalendarView, today);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<EventFlexibleItem> getItemsForDay(CalendarDay calendarDay) {
        List<EventFlexibleItem> events = new ArrayList<>();
        for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.entrySet()) {
            if (entry.getValue().containsKey(SerializableCalendarDay.of(calendarDay)))
                for (VEvent vEvent : entry.getValue().get(SerializableCalendarDay.of(calendarDay))) {
                    events.add(new EventFlexibleItem(new ImmutablePair<>(entry.getKey(), vEvent)));
                }
        }
        return events;
    }

    private void setRecyclerViewItems(CalendarDay date) {
        eventAdapter.clear();
        eventAdapter.addItems(0, getItemsForDay(date));
    }

    private float getDotRadius() {
        if (materialCalendarView != null)
            return Math.max(
                    5, //Minimum radius of 5
                    Math.min(
                            materialCalendarView.getWidth(),
                            materialCalendarView.getHeight()
                    ) / 150f
            );
        else
            return 10;
    }

    private void setUpMaterialCalendarView(final FixedMaterialCalendarView materialCalendarView) {
        Log.d(TAG, "Starting MCV setup");

        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                dateSelected = date;
                setRecyclerViewItems(date);
            }
        });
        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
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
        materialCalendarView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                Log.d(TAG, "MCV New layout: " + left + " " + top + " " + right + " " + bottom + "; Old layout: " + oldLeft + " " + oldTop + " " + oldRight + " " + oldBottom);
                Log.d(TAG, "MCV tile height: " + materialCalendarView.getTileHeight());

                if (left != oldLeft
                        || top != oldTop
                        || right != oldRight
                        || bottom != oldBottom)
                    materialCalendarView.post(new Runnable() {
                        @Override
                        public void run() {
                            materialCalendarView.invalidateDecorators();
                        }
                    });
            }
        });

        final int dotColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getActivity().getTheme());

        materialCalendarView.addDecorators(
                //Single decorator
                new DayViewDecorator() {

                    @Override
                    public boolean shouldDecorate(final CalendarDay day) {
                        if (calendarData == null)
                            return false;

                        SerializableCalendarDay serializableCalendarDay = SerializableCalendarDay.of(day);
                        int numPresent = 0;
                        for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.entrySet())
                            if (entry.getValue().containsKey(serializableCalendarDay)) {
                                numPresent += entry.getValue().get(serializableCalendarDay).size();
                                if (numPresent > 1)
                                    return false;
                            }
                        return numPresent == 1;
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
                        if (calendarData == null)
                            return false;

                        SerializableCalendarDay serializableCalendarDay = SerializableCalendarDay.of(day);
                        int numPresent = 0;
                        for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.entrySet())
                            if (entry.getValue().containsKey(serializableCalendarDay)) {
                                numPresent += entry.getValue().get(serializableCalendarDay).size();
                                if (numPresent > 2)
                                    return false;
                            }
                        return numPresent == 2;
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
                        if (calendarData == null)
                            return false;

                        SerializableCalendarDay serializableCalendarDay = SerializableCalendarDay.of(day);
                        int numPresent = 0;
                        for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.entrySet())
                            if (entry.getValue().containsKey(serializableCalendarDay)) {
                                numPresent += entry.getValue().get(serializableCalendarDay).size();
                                if (numPresent > 3)
                                    return false;
                            }
                        return numPresent == 3;
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
                        if (calendarData == null)
                            return false;

                        SerializableCalendarDay serializableCalendarDay = SerializableCalendarDay.of(day);
                        int numPresent = 0;
                        for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.entrySet())
                            if (entry.getValue().containsKey(serializableCalendarDay)) {
                                numPresent += entry.getValue().get(serializableCalendarDay).size();
                                if (numPresent > 3)
                                    return true;
                            }
                        return numPresent > 3;
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

        materialCalendarView = (FixedMaterialCalendarView) rootLayout.findViewById(R.id.calendar_calendar);
        setUpMaterialCalendarView(materialCalendarView);

        eventRecyclerView = (RecyclerView) rootLayout.findViewById(R.id.event_recycler_view);
        eventRecyclerView.setNestedScrollingEnabled(false);
        eventAdapter = new EventAdapter();
        eventRecyclerView.setAdapter(eventAdapter);
        eventRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        eventRecyclerView.getLayoutManager().setAutoMeasureEnabled(true);

        eventRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "Touch, canScroll=" + eventRecyclerView.canScrollVertically(-1) + ", " + ((View) eventRecyclerView.getParent()).canScrollVertically(-1) + ": " + event);
                return false;
            }
        });
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        //eventRecyclerView.addItemDecoration(dividerItemDecoration);

        eventAdapter.addListener(new EventDetailsOnItemClickListener(eventAdapter, getActivity()));

        if (savedInstanceState != null)
            dateSelected = savedInstanceState.getParcelable("dateSelected");

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
        if (dateSelected == null)
            dateSelected = CalendarDay.today();

        materialCalendarView.setCurrentDate(dateSelected);
        callOnDateClicked(materialCalendarView, dateSelected);

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

    private void callOnDateClicked(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        try {
            onDateClickedMethod.invoke(materialCalendarView, calendarDay, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
        if (nestedScrollView != null)
            return nestedScrollView.canScrollVertically(-1);
        else if (eventRecyclerView != null)
            return eventRecyclerView.canScrollVertically(-1);
        else
            return false;
    }
}
