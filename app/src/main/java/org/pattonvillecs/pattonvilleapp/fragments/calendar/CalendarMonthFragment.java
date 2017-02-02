package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.SpotlightHelper;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarData;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventAdapter;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventDetailsOnItemClickListener;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;

import java.lang.reflect.Method;
import java.util.Map;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

import static org.pattonvillecs.pattonvilleapp.SpotlightHelper.showSpotlight;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarMonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarMonthFragment extends Fragment {

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
    private CalendarData calendarData = new CalendarData();
    private PattonvilleApplication pattonvilleApplication;
    private NestedScrollView nestedScrollView;


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

        pattonvilleApplication = PattonvilleApplication.get(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_calendar_action_bar_menu, menu);

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

    private void setUpMaterialCalendarView(final FixedMaterialCalendarView materialCalendarView) {
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                dateSelected = date;
                eventAdapter.clear();
                eventAdapter.addItems(0, calendarData.getItemsForDay(date));
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

        final int dotColor = CalendarDecoratorUtil.getThemeAccentColor(getContext());
        final float radius;
        {
            switch (getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getContext().getResources().getDisplayMetrics());
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getContext().getResources().getDisplayMetrics());
                    break;
                case Configuration.ORIENTATION_UNDEFINED:
                default:
                    throw new Error("Why would this ever happen?");
            }
        }
        //Single decorator
        materialCalendarView.addDecorator(new DayViewDecorator() {

            @Override
            public boolean shouldDecorate(final CalendarDay day) {
                if (calendarData == null)
                    return false;

                SerializableCalendarDay serializableCalendarDay = SerializableCalendarDay.of(day);
                int numPresent = 0;
                for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.getCalendars().entrySet())
                    if (entry.getValue().containsKey(serializableCalendarDay)) {
                        numPresent += entry.getValue().get(serializableCalendarDay).size();
                        if (numPresent > 1)
                            return false;
                    }
                return numPresent == 1;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY);
                view.setSelectionDrawable(stateListDrawable);

                //materialCalendarView.getChildAt(1).getWidth() / 7f / 10f
                view.addSpan(EnhancedDotSpan.createSingle(radius, dotColor));
            }
        });
        //Double decorator
        materialCalendarView.addDecorator(new DayViewDecorator() {

            @Override
            public boolean shouldDecorate(final CalendarDay day) {
                if (calendarData == null)
                    return false;

                SerializableCalendarDay serializableCalendarDay = SerializableCalendarDay.of(day);
                int numPresent = 0;
                for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.getCalendars().entrySet())
                    if (entry.getValue().containsKey(serializableCalendarDay)) {
                        numPresent += entry.getValue().get(serializableCalendarDay).size();
                        if (numPresent > 2)
                            return false;
                    }
                return numPresent == 2;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY);
                view.setSelectionDrawable(stateListDrawable);

                //materialCalendarView.getChildAt(1).getWidth() / 7f / 10f
                Pair<EnhancedDotSpan, EnhancedDotSpan> pair = EnhancedDotSpan.createPair(radius, dotColor, dotColor);
                view.addSpan(pair.getLeft());
                view.addSpan(pair.getRight());
            }
        });
        //Triple decorator
        materialCalendarView.addDecorator(new DayViewDecorator() {

            @Override
            public boolean shouldDecorate(final CalendarDay day) {
                if (calendarData == null)
                    return false;

                SerializableCalendarDay serializableCalendarDay = SerializableCalendarDay.of(day);
                int numPresent = 0;
                for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.getCalendars().entrySet())
                    if (entry.getValue().containsKey(serializableCalendarDay)) {
                        numPresent += entry.getValue().get(serializableCalendarDay).size();
                        if (numPresent > 3)
                            return false;
                    }
                return numPresent == 3;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY);
                view.setSelectionDrawable(stateListDrawable);

                //materialCalendarView.getChildAt(1).getWidth() / 7f / 10f
                Triple<EnhancedDotSpan, EnhancedDotSpan, EnhancedDotSpan> triple = EnhancedDotSpan.createTriple(radius, dotColor, dotColor, dotColor);
                view.addSpan(triple.getLeft());
                view.addSpan(triple.getMiddle());
                view.addSpan(triple.getRight());
            }
        });
        //>Triple decorator
        materialCalendarView.addDecorator(new DayViewDecorator() {

            @Override
            public boolean shouldDecorate(final CalendarDay day) {
                if (calendarData == null)
                    return false;

                SerializableCalendarDay serializableCalendarDay = SerializableCalendarDay.of(day);
                int numPresent = 0;
                for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.getCalendars().entrySet())
                    if (entry.getValue().containsKey(serializableCalendarDay)) {
                        numPresent += entry.getValue().get(serializableCalendarDay).size();
                        if (numPresent > 3)
                            return true;
                    }
                return numPresent > 3;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY);
                view.setSelectionDrawable(stateListDrawable);

                //materialCalendarView.getChildAt(1).getWidth() / 7f / 10f
                Triple<EnhancedDotSpan, EnhancedDotSpan, EnhancedDotSpan> triple = EnhancedDotSpan.createTripleWithPlus(radius, dotColor, dotColor, dotColor);
                view.addSpan(triple.getLeft());
                view.addSpan(triple.getMiddle());
                view.addSpan(triple.getRight());
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called");
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
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called");
    }

    private void callOnDateClicked(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        try {
            onDateClickedMethod.invoke(materialCalendarView, calendarDay, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void updateCalendarData(Map<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> calendarData) {
        for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : calendarData.entrySet()) {
            this.calendarData.getCalendars().put(entry.getKey(), entry.getValue());
        }
        materialCalendarView.invalidateDecorators();
        callOnDateClicked(materialCalendarView, dateSelected);
    }
}
