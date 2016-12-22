package org.pattonvillecs.pattonvilleapp;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightListener;

import java.util.LinkedList;

/**
 * Created by Mitchell on 12/22/2016.
 */

public final class SpotlightHelper {

    private static final LinkedList<SpotlightView.Builder> builders = new LinkedList<>();
    private static boolean spotlightCurrentlyOpen = false;

    private SpotlightHelper() {
    }

    public static void showSpotlight(Activity activity, View target, String mainText, String subText) {
        int primaryColor = ContextCompat.getColor(activity, R.color.colorPrimary);
        int accentColor = ContextCompat.getColor(activity, R.color.colorAccent);
        builders.addLast(new SpotlightView.Builder(activity)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(false)
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
                .usageId("TEST" + Double.doubleToRawLongBits(Math.random())) //UNIQUE ID
                .setListener(new SpotlightListener() {
                    @Override
                    public void onUserClicked(String usageID) {
                        if (builders.size() > 0)
                            builders.pop().show();
                        else
                            spotlightCurrentlyOpen = false;
                    }
                }));
        if (builders.size() == 1 && !spotlightCurrentlyOpen) {
            builders.pop().show();
            spotlightCurrentlyOpen = true;
        }
    }
}
