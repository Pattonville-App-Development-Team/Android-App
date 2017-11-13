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

package org.pattonvillecs.pattonvilleapp.ui.calendar

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

/**
 * Created by Mitchell on 10/22/2017.
 */

class CalendarFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentPage: MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 0 }


    //Hides the true type of _currentPage from accessors
    val currentPage: LiveData<Int> = _currentPage

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }
}
