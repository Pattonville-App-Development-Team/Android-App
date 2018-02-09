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

package org.pattonvillecs.pattonvilleapp.view.ui.spotlight

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View
import android.view.ViewTreeObserver
import org.jetbrains.anko.findOptional

/**
 * Utility function to make queueing menu item spotlights easier.
 *
 * @param menuItem the menu item to target
 * @param uniqueId an ID that is used to run the spotlight exactly once
 * @param subText smaller text appearing below
 * @param mainText large text appearing above
 */
fun Activity.showSpotlightOnMenuItem(@IdRes menuItem: Int, uniqueId: String, subText: String, mainText: String) {
    //This terrifies me...
    val viewTreeObserver = window.decorView.viewTreeObserver
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            findOptional<View>(menuItem)?.let {
                // Found it! Do what you need with the button
                SpotlightHelper.showSpotlight(this@showSpotlightOnMenuItem, it, uniqueId, subText, mainText)
                // Now you can get rid of this listener
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
    })
}