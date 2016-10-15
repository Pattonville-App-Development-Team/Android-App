package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.FixedMaterialCalendarView;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarMonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarMonthFragment extends Fragment {

    public static final String TAG = "CalendarMonthFragment";
    private FixedMaterialCalendarView mCalendarView;
    private int calendarMonthSlideInDrawerHeightPixels;
    private ListView mListView;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_calendar_month, container, false);

        mCalendarView = (FixedMaterialCalendarView) layout.findViewById(R.id.calendar_calendar);
        mCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.getDay() % 3 == 0;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarDecoratorUtil.generateBackground(Color.CYAN, getResources().getInteger(android.R.integer.config_shortAnimTime), new Rect(0, 0, 0, 0));
                view.setSelectionDrawable(stateListDrawable);

                view.addSpan(new DotSpan(mCalendarView.getTileWidth() / 10f, CalendarDecoratorUtil.getThemeAccentColor(getContext())));
            }
        });
        mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            }
        });

        mListView = (ListView) layout.findViewById(R.id.list_view_calendar);
        mListView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, Arrays.asList("Test1", "Test2")));

        calendarMonthSlideInDrawerHeightPixels = getResources().getDimensionPixelSize(R.dimen.calendar_month_slide_in_drawer_height);

        ToggleButton toggleButton = (ToggleButton) layout.findViewById(R.id.button_calendar);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openCurrentEventsDrawer();
                } else {
                    closeCurrentEventsDrawer();
                }
            }
        });

        layout.getLayoutAnimation();

        return layout;
    }

    private void openCurrentEventsDrawer() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, calendarMonthSlideInDrawerHeightPixels).setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mListView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                mListView.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mListView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
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

    private void closeCurrentEventsDrawer() {
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
