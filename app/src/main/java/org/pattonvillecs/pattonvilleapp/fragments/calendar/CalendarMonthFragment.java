package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarMonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarMonthFragment extends Fragment {

    public static final String TAG = "CalendarMonthFragment";
    private static final int NUM_ITEMS_SHOWN = 3;
    private FixedMaterialCalendarView mCalendarView;
    private int calendarMonthSlideInDrawerHeightPixels;
    private ListView mListView;
    private ArrayAdapter<String> listViewArrayAdapter;
    private boolean currentEventsDrawerOpen = false;
    private CalendarDay dateSelected;
    private boolean drawerInMotion = false;
    //private ResourceFragment resourceFragment;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        mCalendarView.invalidateDecorators();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView called");
        // Inflate the layout for this fragment
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_calendar_month, container, false);

        mCalendarView = (FixedMaterialCalendarView) layout.findViewById(R.id.calendar_calendar);
        mCalendarView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED); // Compute hypothetical bounds of the calendar view if it could wrap_content
        mCalendarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean isCalendarViewDirty = true; // Stops spam of updates caused by mCalendarView.invalidateDecorators()

            @Override
            public void onGlobalLayout() {
                if (getActivity() != null && !drawerInMotion) // If the calendar fragment is still attached to an activity
                {
                    if (isCalendarViewDirty) {
                        Log.e(TAG, "Decorators invalidated from layout change");
                        mCalendarView.invalidateDecorators(); // TODO Fix slow layout updating... Perhaps with a mutating DotSpan? Not too important right now
                        isCalendarViewDirty = false;
                    }
                } else
                    isCalendarViewDirty = true;
            }
        });
        mCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.getDay() % 3 == 0;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.CYAN, getResources().getInteger(android.R.integer.config_shortAnimTime), new Rect(0, 0, 0, 0));
                view.setSelectionDrawable(stateListDrawable);

                view.addSpan(new DotSpan(mCalendarView.getChildAt(1).getWidth() / 7f / 10f, CalendarDecoratorUtil.getThemeAccentColor(getContext())));
            }
        });
        mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                dateSelected = date;
                listViewArrayAdapter.clear();
                DateFormat simpleDateFormat = SimpleDateFormat.getDateInstance();
                for (int i = 0; i < 10; i++) {
                    listViewArrayAdapter.add("Date: " + simpleDateFormat.format(date.getDate()) + " TEST" + i);
                }
                openCurrentEventsDrawer();
            }
        });

        mListView = (ListView) layout.findViewById(R.id.list_view_calendar);
        listViewArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        mListView.setAdapter(listViewArrayAdapter);
        listViewArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                Log.e(TAG, "Dataset changed!");
                measureDrawerHeight(getActivity().getResources().getConfiguration().orientation);
            }
        });

        if (savedInstanceState != null)
            dateSelected = savedInstanceState.getParcelable("dateSelected");

        return layout;
    }

    private void measureDrawerHeight(int orientation) {
        Log.e(TAG, "Measuring drawer height");
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                mListView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED); // Compute hypothetical bounds of a SINGLE ITEM if it could wrap_content
                calendarMonthSlideInDrawerHeightPixels = Math.max(mListView.getMeasuredHeight() * NUM_ITEMS_SHOWN, (int) (getActivity().getResources().getDisplayMetrics().heightPixels * .2)); // Show desired number of items
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                mListView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.makeMeasureSpec(mCalendarView.getMeasuredHeight(), View.MeasureSpec.AT_MOST));
                calendarMonthSlideInDrawerHeightPixels = mListView.getMeasuredHeight();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (dateSelected != null) {
            try {
                Method toCall = MaterialCalendarView.class.getDeclaredMethod("onDateClicked", CalendarDay.class, boolean.class);
                toCall.setAccessible(true);
                toCall.invoke(mCalendarView, dateSelected, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            measureDrawerHeight(getActivity().getResources().getConfiguration().orientation);
            openCurrentEventsDrawer();
        }
    }

    private void openCurrentEventsDrawer() {
        Log.e(TAG, "Called openCurrentEventsDrawer");
        Log.e(TAG, "Drawer open status: " + currentEventsDrawerOpen);
        if (currentEventsDrawerOpen)
            return;
        Log.e(TAG, "Opening drawer to height: " + calendarMonthSlideInDrawerHeightPixels);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, calendarMonthSlideInDrawerHeightPixels).setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mListView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                mListView.requestLayout();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                drawerInMotion = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawerInMotion = false;
                mCalendarView.invalidateDecorators();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.start();
        if (calendarMonthSlideInDrawerHeightPixels != 0)
            currentEventsDrawerOpen = true;
    }

    private void closeCurrentEventsDrawer() {
        if (!currentEventsDrawerOpen)
            return;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(calendarMonthSlideInDrawerHeightPixels, 0).setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mListView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                mListView.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                drawerInMotion = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawerInMotion = false;
                currentEventsDrawerOpen = false;
                mCalendarView.invalidateDecorators();
                //resourceFragment.put(KEY_CURRENT_EVENTS_DRAWER_OPEN, false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
    }

}
