package org.pattonvillecs.pattonvilleapp.fragments.calendar.events;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.R;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by skaggsm on 1/4/17.
 */
public class EventViewHolder extends FlexibleViewHolder {
    private static final String TAG = "EventViewHolder";
    final TextView topText, bottomText, shortSchoolName;
    final ImageView schoolColorImageView;

    public EventViewHolder(View view, FlexibleAdapter adapter) {
        this(view, adapter, false);
    }

    public EventViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
        super(view, adapter, stickyHeader);
        topText = (TextView) view.findViewById(R.id.text_top);
        bottomText = (TextView) view.findViewById(R.id.text_bottom);
        schoolColorImageView = (ImageView) view.findViewById(R.id.school_color_circle);
        shortSchoolName = (TextView) view.findViewById(R.id.school_short_name);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Log.i(TAG, "Clicked view: " + view);
    }
}
