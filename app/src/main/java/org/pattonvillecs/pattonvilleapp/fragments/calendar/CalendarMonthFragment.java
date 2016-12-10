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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;

import java.lang.reflect.Method;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarMonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarMonthFragment extends Fragment {

    public static final String TAG = "CalendarMonthFragment";
    private FixedMaterialCalendarView mCalendarView;
    private ListView mMaxHeightListView;
    private CalendarDay dateSelected;
    private SingleDayEventAdapter mSingleDayEventAdapter;
    private LinearLayout mLinearLayout;

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
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
                break;
        }
        return true;
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

        mMaxHeightListView = (ListView) mLinearLayout.findViewById(R.id.list_view_calendar);
        mMaxHeightListView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mSingleDayEventAdapter = new SingleDayEventAdapter(getContext(), mCalendarView);
        mMaxHeightListView.setAdapter(mSingleDayEventAdapter);

        mMaxHeightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VEvent calendarVEvent = (VEvent) parent.getAdapter().getItem(position);
                CalendarMonthFragment.this.startActivity(new Intent(getContext(), CalendarEventDetailsActivity.class).putExtra("calendarEvent", new CalendarEvent(calendarVEvent)));
            }
        });
        if (savedInstanceState != null)
            dateSelected = savedInstanceState.getParcelable("dateSelected");


        return mLinearLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        //dateSelected = null;
        if (dateSelected != null) {
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
}
