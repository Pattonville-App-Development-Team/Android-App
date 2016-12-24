package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.SpotlightHelper;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;

import java.lang.reflect.Method;

import static org.pattonvillecs.pattonvilleapp.SpotlightHelper.showSpotlight;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarMonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarMonthFragment extends Fragment {

    public static final String TAG = "CalendarMonthFragment";
    private FixedMaterialCalendarView mCalendarView;
    private ListView mListView;
    private CalendarDay dateSelected;
    private SingleDayEventAdapter mSingleDayEventAdapter;
    private LinearLayout mLinearLayout;
    private View mGotoTodayAction;

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
                mCalendarView.setCurrentDate(CalendarDay.today());
                try {
                    Method toCall = MaterialCalendarView.class.getDeclaredMethod("onDateClicked", CalendarDay.class, boolean.class);
                    toCall.setAccessible(true);
                    toCall.invoke(mCalendarView, CalendarDay.today(), true);
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
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_calendar_month, container, false);

        mLinearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mLinearLayout.getLayoutTransition().setDuration(LayoutTransition.CHANGING, 200);

        mCalendarView = (FixedMaterialCalendarView) mLinearLayout.findViewById(R.id.calendar_calendar);
        mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                dateSelected = date;
                mSingleDayEventAdapter.setCurrentCalendarDay(date);
                Log.e(TAG, "Setting layout");
                Log.e(TAG, mSingleDayEventAdapter.getCount() + " events present");
            }
        });

        mListView = (ListView) mLinearLayout.findViewById(R.id.list_view_calendar);
        mListView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mSingleDayEventAdapter = new SingleDayEventAdapter(getActivity(), getContext(), mCalendarView, PattonvilleApplication.get(getActivity()).getRequestQueue());
        mListView.setAdapter(mSingleDayEventAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VEvent calendarVEvent = (VEvent) parent.getAdapter().getItem(position);
                CalendarMonthFragment.this.startActivity(new Intent(getContext(), CalendarEventDetailsActivity.class).putExtra("calendarEvent", new CalendarEvent(calendarVEvent)));
            }
        });
        if (savedInstanceState != null)
            dateSelected = savedInstanceState.getParcelable("dateSelected");

        SpotlightHelper.showSpotlight(getActivity(), mLinearLayout, "CalendarMonthFragment_SelectedDayEventList", "Events occurring on the selected day are shown here.", "Events");

        return mLinearLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (dateSelected == null)
            dateSelected = CalendarDay.today();

        mCalendarView.setCurrentDate(dateSelected);
        try {
            Method toCall = MaterialCalendarView.class.getDeclaredMethod("onDateClicked", CalendarDay.class, boolean.class);
            toCall.setAccessible(true);
            toCall.invoke(mCalendarView, dateSelected, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
