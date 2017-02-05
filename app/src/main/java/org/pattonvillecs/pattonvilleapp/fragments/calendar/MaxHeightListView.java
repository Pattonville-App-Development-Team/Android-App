package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * Created by Mitchell Skaggs on 10/19/2016.
 */
public class MaxHeightListView extends ListView {

    private static final int DEFAULT_HEIGHT_PX = 200;
    private static final String TAG = "MaxHeightListView";
    private int maxHeight;

    public MaxHeightListView(Context context) {
        super(context);
    }

    public MaxHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public MaxHeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    private static String getMeasureSpecModeName(int measureMode) {
        switch (measureMode) {
            case MeasureSpec.UNSPECIFIED:
                return "UNSPECIFIED";
            case MeasureSpec.AT_MOST:
                return "AT_MOST";
            case MeasureSpec.EXACTLY:
                return "EXACTLY";
            default:
                throw new IllegalArgumentException("Invalid parameter, measureMode was not a MeasureSpec mode int!");
        }
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightListView);
            //200 is a default value
            maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.MaxHeightListView_maxHeight, DEFAULT_HEIGHT_PX);

            styledAttrs.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int requestedMode = MeasureSpec.getMode(heightMeasureSpec);
        String requestedModeName = getMeasureSpecModeName(requestedMode);
        int requestedHeight = MeasureSpec.getSize(heightMeasureSpec);

        int newMode = MeasureSpec.AT_MOST;//requestedMode == MeasureSpec.EXACTLY ? MeasureSpec.EXACTLY : MeasureSpec.AT_MOST;
        String newModeName = getMeasureSpecModeName(newMode);
        int newHeight = 10000;//Math.min(maxHeight, requestedHeight);

        Log.i(TAG, "Overrode measure of height " + requestedHeight + " and mode " + requestedModeName + " to measure of height " + newHeight + " and mode " + newModeName);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeight, newMode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
