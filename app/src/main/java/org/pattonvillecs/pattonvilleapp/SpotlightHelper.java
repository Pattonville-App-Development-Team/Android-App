/*
 * Copyright (C) 2017  Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, and Nathan Skelton
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

package org.pattonvillecs.pattonvilleapp;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightListener;

import java.util.LinkedList;

/**
 * Created by Mitchell Skaggs on 12/22/2016.
 */

public final class SpotlightHelper {

    private static final boolean DEBUG_MODE_ENABLED = false;
    private static final LinkedList<SpotlightView.Builder> builders = new LinkedList<>();
    private static final String TAG = "SpotlightHelper";
    private static volatile boolean spotlightCurrentlyOpen = false;

    private SpotlightHelper() {
    }

    public static void showSpotlight(Activity activity, final View target, String uniqueID, String subText, String mainText) {
        showSpotlight(activity, target, 20, uniqueID, subText, mainText);
    }

    public static void showSpotlight(Activity activity, final View target, int targetPadding, String uniqueID, String subText, String mainText) {

        // Get the colors to use. Pattonville green for highlights, white for text on dark background
        int primaryColor = ContextCompat.getColor(activity, R.color.colorPrimary);
        int accentColor = ContextCompat.getColor(activity, R.color.colorAccent);

        // Initialize the builder for the eventual Spotlight
        @SuppressWarnings("ConstantConditions")
        final SpotlightView.Builder builder = new SpotlightView.Builder(activity)
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
                .targetPadding(targetPadding)
                .lineAnimDuration(400)
                .lineAndArcColor(primaryColor)
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId(DEBUG_MODE_ENABLED ? "DEBUG_MODE_" + Double.doubleToRawLongBits(Math.random()) : uniqueID); //UNIQUE ID

        // Create an OnAttachStateChangeListener that will remove the builder from the queue and reset the spotlightCurrentlyOpen flag if it doesn't find itself in the queue (Already been popped and shown)
        final View.OnAttachStateChangeListener onAttachStateChangeListener = new View.OnAttachStateChangeListener() {

            /**
             * Only for logging
             *
             * @param v target view
             */
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.d(TAG, "onAttach of " + builder);
                Log.d(TAG, "BuilderQueue: " + builders);
            }

            /**
             * Removes the corresponding builder. If not found, assumes that it has already been popped and is currently shown
             *
             * @param v target view
             */
            @Override
            public void onViewDetachedFromWindow(View v) {
                //Used to cut down on virtual method calls
                LinkedList<SpotlightView.Builder> tempBuilders = builders;
                Log.d(TAG, "onDetach of " + builder);
                Log.i(TAG, "ToPop: " + tempBuilders);
                //Try to remove it
                boolean wasPresent = tempBuilders.remove(builder);
                //Has this builder already been popped (shown)?
                if (!wasPresent)
                    spotlightCurrentlyOpen = false;
                Log.i(TAG, "PostPop: " + tempBuilders);
            }
        };

        // Create a listener for the eventual Spotlight that is notified when a Spotlight is closed normally. Removes the OnAttachStateChangeListener and shows the next builder if there is one
        SpotlightListener spotlightListener = new SpotlightListener() {
            /**
             * Removes the target's OnAttachStateChangeListener and continues the chain of Spotlights. If there are no more, resets the spotlightCurrentlyOpen flag
             *
             * @param usageID ID of the Spotlight that was shut down
             */
            @Override
            public void onUserClicked(String usageID) {
                target.removeOnAttachStateChangeListener(onAttachStateChangeListener);
                if (builders.size() > 0)
                    builders.pop().show();
                else
                    spotlightCurrentlyOpen = false;
            }
        };
        //Set the builder's listener. builder must be final, so it is mutated here, later, where spotlightListener is visible
        builder.setListener(spotlightListener);

        //Add the builder to the queue
        builders.addLast(builder);
        //Kick-start the queue if no spotlight is open
        if (builders.size() == 1 && !spotlightCurrentlyOpen) {
            builders.pop().show();
            spotlightCurrentlyOpen = true;
        }

        //Set the Spotlight target's OnAttachStateChangeListener now, after everything has settled down and any Spotlights have started
        target.addOnAttachStateChangeListener(onAttachStateChangeListener);
    }
}
