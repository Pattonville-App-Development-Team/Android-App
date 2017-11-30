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

package org.pattonvillecs.pattonvilleapp.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

/**
 * Helpful extension methods to make acquiring a ViewModel less verbose.
 *
 * @since 1.2.0
 * @author Mitchell Skaggs
 */
@JvmOverloads
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(tag: String? = null): T =
        if (tag == null)
            ViewModelProviders.of(this).get(T::class.java)
        else
            ViewModelProviders.of(this).get(tag, T::class.java)

@JvmOverloads
inline fun <reified T : ViewModel> Fragment.getViewModel(tag: String? = null): T =
        if (tag == null)
            ViewModelProviders.of(this).get(T::class.java)
        else
            ViewModelProviders.of(this).get(tag, T::class.java)