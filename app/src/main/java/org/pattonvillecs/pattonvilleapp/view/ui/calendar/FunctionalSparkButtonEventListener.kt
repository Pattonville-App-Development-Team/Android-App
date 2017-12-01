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

package org.pattonvillecs.pattonvilleapp.view.ui.calendar

import android.widget.ImageView
import com.varunest.sparkbutton.SparkButton
import com.varunest.sparkbutton.SparkEventListener

/**
 * This class makes it easy to supply a function to the [SparkButton.setEventListener] method.
 *
 * @author Mitchell Skaggs
 * @since 1.2.0
 */
class FunctionalSparkButtonEventListener(private val onEventFunction: (button: ImageView?, buttonState: Boolean) -> Unit) : SparkEventListener {
    override fun onEventAnimationEnd(button: ImageView?, buttonState: Boolean) {}

    override fun onEvent(button: ImageView?, buttonState: Boolean) {
        onEventFunction(button, buttonState)
    }

    override fun onEventAnimationStart(button: ImageView?, buttonState: Boolean) {}
}