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

package org.pattonvillecs.pattonvilleapp

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View
import android.view.ViewTreeObserver

/**
 * Created by Mitchell Skaggs on 11/13/2017.
 */
object SpotlightUtils {
    @JvmStatic
    fun showSpotlightOnMenuItem(activity: Activity, @IdRes menuItem: Int, uniqueId: String, subText: String, mainText: String) {
        //This terrifies me...
        val viewTreeObserver = activity.window.decorView.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                activity.findViewById<View>(menuItem)?.let {
                    // Found it! Do what you need with the button
                    SpotlightHelper.showSpotlight(activity, it, uniqueId, subText, mainText)
                    // Now you can get rid of this listener
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }
}