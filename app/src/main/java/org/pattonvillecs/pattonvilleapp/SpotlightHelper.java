package org.pattonvillecs.pattonvilleapp;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.wooplr.spotlight.SpotlightView;

/**
 * Created by Mitchell on 12/22/2016.
 */

public final class SpotlightHelper {
    private SpotlightHelper() {
    }

    public static SpotlightView.Builder setupSpotlight(Activity activity, View target, String mainText, String subText) {
        int primaryColor = ContextCompat.getColor(activity, R.color.colorPrimary);
        int accentColor = ContextCompat.getColor(activity, R.color.colorAccent);
        return new SpotlightView.Builder(activity)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(primaryColor)
                .headingTvSize(32)
                .headingTvText(mainText)
                .subHeadingTvColor(accentColor)
                .subHeadingTvSize(16)
                .subHeadingTvText(subText)
                .maskColor(Color.parseColor("#dc000000"))
                .target(target)
                .lineAnimDuration(400)
                .lineAndArcColor(primaryColor)
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("TEST" + Double.doubleToRawLongBits(Math.random())); //UNIQUE ID
    }
}
