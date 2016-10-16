package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import org.pattonvillecs.pattonvilleapp.fragments.ResourceFragment;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;

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
    private static final String KEY_CURRENT_EVENTS_DRAWER_OPEN = "currentEventsDrawerOpen";
    private static final int NUM_ITEMS_SHOWN = 3;
    private FixedMaterialCalendarView mCalendarView;
    private int calendarMonthSlideInDrawerHeightPixels;
    private ListView mListView;
    private ArrayAdapter<String> listViewArrayAdapter;
    private boolean currentEventsDrawerOpen = false;
    private ResourceFragment resourceFragment;

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
        CalendarMonthFragment fragment = new CalendarMonthFragment();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_calendar_month, container, false);

        mCalendarView = (FixedMaterialCalendarView) layout.findViewById(R.id.calendar_calendar);
        mCalendarView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED); // Compute hypothetical bounds of the calendar view if it could wrap_content
        mCalendarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCalendarView.invalidateDecorators(); // !!!THIS CAUSES MEGA-LAG!!! TODO Fix slow layout updating... Perhaps with a mutating DotSpan?
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
                switch (getActivity().getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        mListView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED); // Compute hypothetical bounds of a SINGLE ITEM if it could wrap_content
                        calendarMonthSlideInDrawerHeightPixels = mListView.getMeasuredHeight() * NUM_ITEMS_SHOWN; // Show desired number of items
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        calendarMonthSlideInDrawerHeightPixels = mListView.getHeight();
                        break;
                }
            }
        });

        resourceFragment = ResourceFragment.retrieveResourceFragment(getActivity().getSupportFragmentManager());
        if ((Boolean) resourceFragment.getOrDefault(KEY_CURRENT_EVENTS_DRAWER_OPEN, false)) {
            Log.e(TAG, "About to call openCurrentEventsDrawer");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openCurrentEventsDrawer();
                }
            }, 2000);
        }

        return layout;
    }

    private void openCurrentEventsDrawer() {
        Log.e(TAG, "Called openCurrentEventsDrawer");
        Log.e(TAG, "Drawer open status: " + currentEventsDrawerOpen);
        if (currentEventsDrawerOpen)
            return;
        Log.e(TAG, "Opening drawer");
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, calendarMonthSlideInDrawerHeightPixels).setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mListView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                mListView.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.start();
        mListView.setVisibility(View.VISIBLE);
        currentEventsDrawerOpen = true;
        resourceFragment.put(KEY_CURRENT_EVENTS_DRAWER_OPEN, true);
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
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mListView.setVisibility(View.GONE);
                currentEventsDrawerOpen = false;
                resourceFragment.put(KEY_CURRENT_EVENTS_DRAWER_OPEN, false);
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
