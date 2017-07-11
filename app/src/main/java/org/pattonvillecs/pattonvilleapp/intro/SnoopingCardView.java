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

package org.pattonvillecs.pattonvilleapp.intro;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import static android.content.ContentValues.TAG;

/**
 * Created by Mitchell on 6/1/2017.
 */

public class SnoopingCardView extends CardView {
    public SnoopingCardView(Context context) {
        super(context);
    }

    public SnoopingCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnoopingCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (super.onInterceptTouchEvent(event))
            return true;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "onInterceptTouchEvent: UP or CANCEL");
                requestDisallowInterceptTouchEvent(true); //Needed to prevent infinite loop of intercepting its own dispatch
                dispatchTouchEvent(event);
                requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }

        return false;
    }
}
