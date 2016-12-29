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

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.collections4.map.MultiValueMap;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.SpotlightHelper;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;

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
                materialCalendarView.setCurrentDate(CalendarDay.today());
                try {
                    Method toCall = MaterialCalendarView.class.getDeclaredMethod("onDateClicked", CalendarDay.class, boolean.class);
                    toCall.setAccessible(true);
                    toCall.invoke(materialCalendarView, CalendarDay.today(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView called");
        // Inflate the layout for this fragment
        LinearLayout rootLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_calendar_month, container, false);

        rootLinearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        rootLinearLayout.getLayoutTransition().setDuration(LayoutTransition.CHANGING, 200);

        materialCalendarView = (FixedMaterialCalendarView) rootLinearLayout.findViewById(R.id.calendar_calendar);
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                dateSelected = date;
                singleDayEventAdapter.setCurrentCalendarDay(date, calendarData);
                Log.e(TAG, singleDayEventAdapter.getCount() + " events present");
                //materialCalendarView.invalidateDecorators();
            }
        });
        materialCalendarView.addDecorator(new DayViewDecorator() {
            float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources().getDisplayMetrics());

            @Override
            public boolean shouldDecorate(final CalendarDay day) {
                return calendarData != null
                        && Stream.of(calendarData.getCalendars()).anyMatch(new Predicate<Map.Entry<DataSource, MultiValueMap<CalendarDay, VEvent>>>() {
                    @Override
                    public boolean test(Map.Entry<DataSource, MultiValueMap<CalendarDay, VEvent>> value) {
                        return value.getValue().containsKey(day);
                    }
                });
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.LTGRAY, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime), new Rect());
                view.setSelectionDrawable(stateListDrawable);

                //materialCalendarView.getChildAt(1).getWidth() / 7f / 10f
                view.addSpan(new DotSpan(radius, CalendarDecoratorUtil.getThemeAccentColor(getContext())));
            }
        });

        currentDayEventsListView = (ListView) rootLinearLayout.findViewById(R.id.list_view_calendar);
        currentDayEventsListView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        singleDayEventAdapter = new SingleDayEventAdapter(getContext());
        currentDayEventsListView.setAdapter(singleDayEventAdapter);

        currentDayEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VEvent calendarVEvent = (VEvent) parent.getAdapter().getItem(position);
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
