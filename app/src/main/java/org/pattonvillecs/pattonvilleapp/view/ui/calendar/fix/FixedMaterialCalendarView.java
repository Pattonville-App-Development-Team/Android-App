/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pattonvillecs.pattonvilleapp.view.ui.calendar.fix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * This {@link MaterialCalendarView} fixes some issues with its parent class.
 * <ul>
 * <li>The {@link FixedMaterialCalendarView#onDateClickedMoveMonth} method scrolls multiple months instead of only one
 * <li>The {@link FixedMaterialCalendarView#onRestoreInstanceState} method ignores the saved tile widths and heights instead of persisting them
 * </ul>
 *
 * @author Mitchell Skaggs
 * @since 1.0.0
 */

public class FixedMaterialCalendarView extends MaterialCalendarView {
    private static final int DEFAULT_DAYS_IN_WEEK = 7;
    private static final int DAY_NAMES_ROW = 1;
    private static final String TAG = "FixedMatCalView";
    private static final Method getWeekCountBasedOnModeMethod;
    private static final Field tileHeightField, tileWidthField, adapterField, calendarModeField, pagerField;

    @Px
    private static final int INVALID_TILE_DIMENSION = -10;

    static {
        try {

            calendarModeField = MaterialCalendarView.class.getDeclaredField("calendarMode");
            getWeekCountBasedOnModeMethod = MaterialCalendarView.class.getDeclaredMethod("getWeekCountBasedOnMode");
            tileHeightField = MaterialCalendarView.class.getDeclaredField("tileHeight");
            tileWidthField = MaterialCalendarView.class.getDeclaredField("tileWidth");
            adapterField = MaterialCalendarView.class.getDeclaredField("adapter");
            pagerField = MaterialCalendarView.class.getDeclaredField("pager");

            getWeekCountBasedOnModeMethod.setAccessible(true);
            tileHeightField.setAccessible(true);
            tileWidthField.setAccessible(true);
            adapterField.setAccessible(true);
            calendarModeField.setAccessible(true);
            pagerField.setAccessible(true);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    public FixedMaterialCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.d(TAG, "Constructor called");

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaterialCalendarView, 0, 0);
        try {
            final int tileSize = a.getLayoutDimension(R.styleable.MaterialCalendarView_mcv_tileSize, INVALID_TILE_DIMENSION);
            if (tileSize > INVALID_TILE_DIMENSION) {
                setTileSize(tileSize);
            }

            final int tileWidth = a.getLayoutDimension(R.styleable.MaterialCalendarView_mcv_tileWidth, INVALID_TILE_DIMENSION);
            if (tileWidth > INVALID_TILE_DIMENSION) {
                setTileWidth(tileWidth);
            }

            final int tileHeight = a.getLayoutDimension(R.styleable.MaterialCalendarView_mcv_tileHeight, INVALID_TILE_DIMENSION);
            if (tileHeight > INVALID_TILE_DIMENSION) {
                setTileHeight(tileHeight);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }

        post(this::requestLayout);

        Log.d(TAG, "Tile height: " + getTileHeight());
        Log.d(TAG, "Tile width: " + getTileWidth());
        //noinspection deprecation
        Log.d(TAG, "Tile size: " + getTileSize());
    }

    public FixedMaterialCalendarView(Context context) {
        this(context, null);
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

    @Override
    public void dispatchOnDateSelected(CalendarDay day, boolean selected) {
        super.dispatchOnDateSelected(day, selected);
    }

    private CalendarMode getCalendarMode() {
        try {
            return (CalendarMode) calendarModeField.get(this);
        } catch (IllegalAccessException e) {
            throw new LinkageError("Calendar mode field was unable to be read!", e);
        }
    }

    /**
     * Moves the selected date to the given date. This method also supports selecting dates more than one month away from the current selection.
     *
     * @param selectedDate the new date to be selected
     * @param nowSelected  true if the date is now selected, false otherwise
     */
    public void onDateClickedMoveMonth(@NonNull final CalendarDay selectedDate, boolean nowSelected) {
        CalendarDay currentDate = getCurrentDate();
        final int selectedMonth = selectedDate.getMonth();
        final int selectedYear = selectedDate.getYear();

        CalendarMode calendarMode = getCalendarMode();
        while (calendarMode == CalendarMode.MONTHS && allowClickDaysOutsideCurrentMonth() && (currentDate.getMonth() != selectedMonth || currentDate.getYear() != selectedYear)) {
            if (currentDate.isAfter(selectedDate)) {
                goToPrevious();
            } else if (currentDate.isBefore(selectedDate)) {
                goToNext();
            }

            currentDate = getCurrentDate();
        }
        onDateClicked(selectedDate, nowSelected);
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        Log.d(TAG, "Finished Temp Detached");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "Detached");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "Attached");
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        Log.d(TAG, "Temp Detached");
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d(TAG, "Saving instance state");
        return super.onSaveInstanceState();
    }

    /**
     * This method undoes the undoing of the requested tile height and width. The values should come from the XML attributes of the layout, but instead they are overwritten by the saved state.
     *
     * @param state The frozen state that had previously been returned by {@link #onSaveInstanceState}.
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG, "Restoring instance state");

        int tileWidth = getTileWidth();
        int tileHeight = getTileHeight();

        super.onRestoreInstanceState(state);

        setTileWidth(tileWidth);
        setTileHeight(tileHeight);
    }

    @Override
    public void setTileHeight(int height) {
        try {
            tileHeightField.setInt(this, height);
            Log.d(TAG, "Tile height set to: " + height);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }

        if (!isInLayout())
            requestLayout();
    }

    @Override
    public void setTileWidth(int width) {
        try {
            tileWidthField.setInt(this, width);
            Log.d(TAG, "Tile width set to: " + width);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }

        if (!isInLayout())
            requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        Log.d(TAG, "onMeasure called height" + MeasureSpec.toString(heightMeasureSpec) + " width " + MeasureSpec.toString(widthMeasureSpec));

        //We need to disregard padding for a while. This will be added back later
        final int desiredWidth = specWidthSize - getPaddingLeft() - getPaddingRight();
        final int desiredHeight = specHeightSize - getPaddingTop() - getPaddingBottom();

        final int weekCount;
        try {
            weekCount = (int) getWeekCountBasedOnModeMethod.invoke(this);
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
            Log.d(TAG, "Measuring with valid tile height or width: Height: " + this.getTileHeight() + " Width: " + this.getTileWidth());
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

            @SuppressLint("Range")
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
