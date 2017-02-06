package org.pattonvillecs.pattonvilleapp.fragments.calendar.fix;

import android.content.Context;
import android.support.v4.view.BetterViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * Created by Mitchell Skaggs on 10/12/16.
 */

public class FixedMaterialCalendarView extends MaterialCalendarView {
    private static final int DEFAULT_DAYS_IN_WEEK = 7;
    private static final int DAY_NAMES_ROW = 1;

    public FixedMaterialCalendarView(Context context) {
        super(context);
    }

    public FixedMaterialCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Clamp the size to the measure spec.
     *
     * @param size Size we want to be
     * @param spec Measure spec to clamp against
     * @return the appropriate size to pass to {@linkplain View#setMeasuredDimension(int, int)}
     */
    private static int clampSize(int size, int spec) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);
        switch (specMode) {
            case MeasureSpec.EXACTLY: {
                return specSize;
            }
            case MeasureSpec.AT_MOST: {
                return Math.min(size, specSize);
            }
            case MeasureSpec.UNSPECIFIED:
            default: {
                return size;
            }
        }
    }

    /**
     * This is abhorrent...
     *
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private int getWeekCountBasedOnMode() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field calendarModeField = MaterialCalendarView.class.getDeclaredField("calendarMode");
        calendarModeField.setAccessible(true);
        CalendarMode calendarMode = (CalendarMode) calendarModeField.get(this);

        Field mDynamicHeightEnabledField = MaterialCalendarView.class.getDeclaredField("mDynamicHeightEnabled");
        mDynamicHeightEnabledField.setAccessible(true);
        boolean mDynamicHeightEnabled = (boolean) mDynamicHeightEnabledField.get(this);

        Field adapterField = MaterialCalendarView.class.getDeclaredField("adapter");
        adapterField.setAccessible(true);
        Object adapter = adapterField.get(this);

        Field pagerField = MaterialCalendarView.class.getDeclaredField("pager");
        pagerField.setAccessible(true);
        BetterViewPager pager = (BetterViewPager) pagerField.get(this);

        Field weekCountField = CalendarMode.class.getDeclaredField("visibleWeeksCount");
        weekCountField.setAccessible(true);
        int weekCount = (int) weekCountField.get(calendarMode);

        boolean isInMonthsMode = calendarMode.equals(CalendarMode.MONTHS);
        if (isInMonthsMode && mDynamicHeightEnabled && adapter != null && pager != null) {
            Method getItemMethod = adapter.getClass().getDeclaredMethod("getItem", CalendarDay.class);
            Object currentItem = getItemMethod.invoke(adapter, pager.getClass().getMethod("getCurrentItem").invoke(pager));
            Calendar cal = (Calendar) currentItem.getClass().getMethod("getCalendar").invoke(currentItem);
            cal = (Calendar) cal.clone();
            //(Calendar) adapter.getItem(pager.getCurrentItem()).getCalendar().clone();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            //noinspection ResourceType
            cal.setFirstDayOfWeek(getFirstDayOfWeek());
            weekCount = cal.get(Calendar.WEEK_OF_MONTH);
        }
        return weekCount + DAY_NAMES_ROW;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        Log.i("MCV", "onMeasure called " + specWidthSize + " " + specHeightSize);

        //We need to disregard padding for a while. This will be added back later
        final int desiredWidth = specWidthSize - getPaddingLeft() - getPaddingRight();
        final int desiredHeight = specHeightSize - getPaddingTop() - getPaddingBottom();

        final int weekCount;
        try {
            weekCount = getWeekCountBasedOnMode();
        } catch (Exception e) {
            throw new Error(e);
        }

        final int viewTileHeight = getTopbarVisible() ? (weekCount + 1) : weekCount;

        //Calculate independent tile sizes for later
        int desiredTileWidth = desiredWidth / DEFAULT_DAYS_IN_WEEK;
        int desiredTileHeight = desiredHeight / viewTileHeight;

        int measureTileSize = -1;
        int measureTileWidth = -1;
        int measureTileHeight = -1;

        if (this.getTileWidth() != INVALID_TILE_DIMENSION || this.getTileHeight() != INVALID_TILE_DIMENSION) {
            if (this.getTileWidth() > 0) {
                //We have a tileWidth set, we should use that
                measureTileWidth = this.getTileWidth();
            } else {
                measureTileWidth = desiredTileWidth;
            }
            if (this.getTileHeight() > 0) {
                //We have a tileHeight set, we should use that
                measureTileHeight = this.getTileHeight();
            } else {
                measureTileHeight = desiredTileHeight;
            }
        } else if (specWidthMode == MeasureSpec.EXACTLY || specWidthMode == MeasureSpec.AT_MOST) {
            if (specHeightMode == MeasureSpec.EXACTLY || specHeightMode == MeasureSpec.AT_MOST) {
                //Pick the SMALLER of the two explicit sizes !!!CHANGED!!!
                measureTileSize = Math.min(desiredTileWidth, desiredTileHeight);
            } else {
                //Be the width size the user wants
                measureTileSize = desiredTileWidth;
            }
        } else if (specHeightMode == MeasureSpec.EXACTLY || specHeightMode == MeasureSpec.AT_MOST) {
            //Be the height size the user wants
            measureTileSize = desiredTileHeight;
        }

        if (measureTileSize > 0) {
            //Use measureTileSize if set
            measureTileHeight = measureTileSize;
            measureTileWidth = measureTileSize;
        } else if (measureTileSize <= 0) {
            if (measureTileWidth <= 0) {
                //Set width to default if no value were set
                measureTileWidth = dpToPx(DEFAULT_TILE_SIZE_DP);
            }
            if (measureTileHeight <= 0) {
                //Set height to default if no value were set
                measureTileHeight = dpToPx(DEFAULT_TILE_SIZE_DP);
            }
        }

        //Calculate our size based off our measured tile size
        int measuredWidth = measureTileWidth * DEFAULT_DAYS_IN_WEEK;
        int measuredHeight = measureTileHeight * viewTileHeight;

        //Put padding back in from when we took it away
        measuredWidth += getPaddingLeft() + getPaddingRight();
        measuredHeight += getPaddingTop() + getPaddingBottom();

        //Contract fulfilled, setting out measurements
        setMeasuredDimension(
                //We clamp inline because we want to use un-clamped versions on the children
                clampSize(measuredWidth, widthMeasureSpec),
                clampSize(measuredHeight, heightMeasureSpec)
        );

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            LayoutParams p = (LayoutParams) child.getLayoutParams();

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    DEFAULT_DAYS_IN_WEEK * measureTileWidth,
                    MeasureSpec.EXACTLY
            );

            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    p.height * measureTileHeight,
                    MeasureSpec.EXACTLY
            );

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }
}
