package org.pattonvillecs.pattonvilleapp.fragments.calendar;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
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

    private static StateListDrawable generateBackground(int color, int fadeTime, Rect bounds) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.setExitFadeDuration(fadeTime);
        drawable.addState(new int[]{android.R.attr.state_checked}, generateCircleDrawable(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.addState(new int[]{android.R.attr.state_pressed}, generateRippleDrawable(color, bounds));
        } else {
            drawable.addState(new int[]{android.R.attr.state_pressed}, generateCircleDrawable(color));
        }

        drawable.addState(new int[]{}, generateCircleDrawable(Color.TRANSPARENT));

        return drawable;
    }

    private static Drawable generateCircleDrawable(final int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        return drawable;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable generateRippleDrawable(final int color, Rect bounds) {
        ColorStateList list = ColorStateList.valueOf(color);
        Drawable mask = generateCircleDrawable(Color.WHITE);
        RippleDrawable rippleDrawable = new RippleDrawable(list, null, mask);
//        API 21
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            rippleDrawable.setBounds(bounds);
        }

//        API 22. Technically harmless to leave on for API 21 and 23, but not worth risking for 23+
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            int center = (bounds.left + bounds.right) / 2;
            rippleDrawable.setHotspotBounds(center, bounds.top, center, bounds.bottom);
        }

        return rippleDrawable;
    }

    private static int getThemeAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_calendar_month, container, false);

        final FixedMaterialCalendarView calendarView = (FixedMaterialCalendarView) layout.findViewById(R.id.calendar_calendar);
        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.getDay() % 3 == 0;
            }

            @Override
            public void decorate(DayViewFacade view) {
                StateListDrawable stateListDrawable = CalendarMonthFragment.generateBackground(Color.CYAN, getResources().getInteger(android.R.integer.config_shortAnimTime), new Rect(0, 0, 0, 0));
                view.setSelectionDrawable(stateListDrawable);

                view.addSpan(new DotSpan(calendarView.getTileWidth() / 10f, getThemeAccentColor(getContext())));
            }
        });
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);

        final ListView listView = (ListView) layout.findViewById(R.id.list_view_calendar);
        listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, Arrays.asList("Test1", "Test2")));

        ToggleButton toggleButton = (ToggleButton) layout.findViewById(R.id.button_calendar);
        toggleButton.setTextOn("GONE");
        toggleButton.setTextOff("VISIBLE");
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listView.setVisibility(View.GONE);
                    calendarView.setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                    calendarView.setVisibility(View.GONE);
                }
            }
        });

        return layout;
    }

}
