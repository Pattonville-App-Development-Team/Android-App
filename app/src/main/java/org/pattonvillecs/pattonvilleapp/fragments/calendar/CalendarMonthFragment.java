package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.SpotlightHelper;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.CalendarEvent;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;

import java.lang.reflect.Method;
import java.util.Map;

import static org.pattonvillecs.pattonvilleapp.SpotlightHelper.showSpotlight;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarMonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarMonthFragment extends Fragment implements CalendarFragment.OnCalendarDataUpdatedListener {

    public static final String TAG = "CalendarMonthFragment";
    private FixedMaterialCalendarView materialCalendarView;
    private ListView currentDayEventsListView;
    private CalendarDay dateSelected;
    private SingleDayEventAdapter singleDayEventAdapter;
    private CalendarFragment calendarFragment;
    private CalendarData calendarData = new CalendarData();

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
        Log.e(TAG, "New instance created...");
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
        calendarFragment.addOnCalendarDataUpdatedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        calendarFragment.removeOnCalendarDataUpdatedListener(this);
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

    private void setUpMaterialCalendarView(FixedMaterialCalendarView materialCalendarView) {
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                dateSelected = date;
                singleDayEventAdapter.setCurrentCalendarDay(SerializableCalendarDay.of(date), calendarData);
                Log.e(TAG, singleDayEventAdapter.getCount() + " events present");
                //materialCalendarView.invalidateDecorators();
            }
        });

        final int dotColor = CalendarDecoratorUtil.getThemeAccentColor(getContext());
        final float radius;
        {
            switch (getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources().getDisplayMetrics());
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getContext().getResources().getDisplayMetrics());
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
                for (Map.Entry<DataSource, MultiValueMap<SerializableCalendarDay, VEvent>> entry : calendarData.getCalendars().entrySet())
                    if (entry.getValue().containsKey(serializableCalendarDay)) {
                        numPresent += entry.getValue().size(serializableCalendarDay);
                        if (numPresent > 1)
                            return false;
                    }
                return numPresent == 1;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime), new Rect());
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
                for (Map.Entry<DataSource, MultiValueMap<SerializableCalendarDay, VEvent>> entry : calendarData.getCalendars().entrySet())
                    if (entry.getValue().containsKey(serializableCalendarDay)) {
                        numPresent += entry.getValue().size(serializableCalendarDay);
                        if (numPresent > 2)
                            return false;
                    }
                return numPresent == 2;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime), new Rect());
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
                for (Map.Entry<DataSource, MultiValueMap<SerializableCalendarDay, VEvent>> entry : calendarData.getCalendars().entrySet())
                    if (entry.getValue().containsKey(serializableCalendarDay)) {
                        numPresent += entry.getValue().size(serializableCalendarDay);
                        if (numPresent > 3)
                            return false;
                    }
                return numPresent == 3;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime), new Rect());
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
                for (Map.Entry<DataSource, MultiValueMap<SerializableCalendarDay, VEvent>> entry : calendarData.getCalendars().entrySet())
                    if (entry.getValue().containsKey(serializableCalendarDay)) {
                        numPresent += entry.getValue().size(serializableCalendarDay);
                        if (numPresent > 3)
                            return true;
                    }
                return numPresent > 3;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime), new Rect());
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
        Log.e(TAG, "onCreateView called");
        // Inflate the layout for this fragment
        LinearLayout rootLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_calendar_month, container, false);

        rootLinearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        rootLinearLayout.getLayoutTransition().setDuration(LayoutTransition.CHANGING, 200);

        materialCalendarView = (FixedMaterialCalendarView) rootLinearLayout.findViewById(R.id.calendar_calendar);
        setUpMaterialCalendarView(materialCalendarView);

        currentDayEventsListView = (ListView) rootLinearLayout.findViewById(R.id.list_view_calendar);
        currentDayEventsListView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        singleDayEventAdapter = new SingleDayEventAdapter(getContext());
        currentDayEventsListView.setAdapter(singleDayEventAdapter);

        currentDayEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                VEvent calendarVEvent = ((Pair<DataSource, VEvent>) parent.getAdapter().getItem(position)).getValue();
                CalendarMonthFragment.this.startActivity(new Intent(getContext(), CalendarEventDetailsActivity.class).putExtra("calendarEvent", new CalendarEvent(calendarVEvent)));
            }
        });
        if (savedInstanceState != null)
            dateSelected = savedInstanceState.getParcelable("dateSelected");

        int spotlightPadding;
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                spotlightPadding = 20;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                spotlightPadding = -250;
                break;
            case Configuration.ORIENTATION_UNDEFINED:
            default:
                throw new Error("Why would this ever happen?");
        }
        SpotlightHelper.showSpotlight(getActivity(), rootLinearLayout, spotlightPadding, "CalendarMonthFragment_SelectedDayEventList", "Events occurring on the selected day are shown here.", "Events");

        return rootLinearLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (dateSelected == null)
            dateSelected = CalendarDay.today();

        materialCalendarView.setCurrentDate(dateSelected);
        callOnDateClicked(materialCalendarView, dateSelected);
    }

    private void callOnDateClicked(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        try {
            Method toCall = MaterialCalendarView.class.getDeclaredMethod("onDateClicked", CalendarDay.class, boolean.class);
            toCall.setAccessible(true);
            toCall.invoke(materialCalendarView, dateSelected, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCalendarData(CalendarData calendarData) {
        this.calendarData = calendarData;
        materialCalendarView.invalidateDecorators();
    }
}
