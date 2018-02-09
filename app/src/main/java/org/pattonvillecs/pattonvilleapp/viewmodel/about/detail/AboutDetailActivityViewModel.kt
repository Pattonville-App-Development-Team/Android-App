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

package org.pattonvillecs.pattonvilleapp.viewmodel.about.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.IntegerRes
import org.pattonvillecs.pattonvilleapp.view.ui.about.detail.LinkItem

/**
 * @author Mitchell Skaggs
 * @since 1.2.0
 */
class AboutDetailActivityViewModel : ViewModel() {
    private val _developerName: MutableLiveData<String> = MutableLiveData()
    val developerName: LiveData<String> = _developerName

    private val _developerText: MutableLiveData<String> = MutableLiveData()
    val developerText: LiveData<String> = _developerText

    private val _developerImageResource: MutableLiveData<Int> = MutableLiveData()
    val developerImageResource: LiveData<Int> = _developerImageResource

    private val _developerLinks: MutableLiveData<List<LinkItem>> = MutableLiveData()
    val developerLinks: LiveData<List<LinkItem>> = _developerLinks

    fun setDeveloperName(name: String) {
        _developerName.value = name
    }

    fun setDeveloperText(text: String) {
        _developerText.value = text
    }

    fun setDeveloperImageResource(@IntegerRes resourceId: Int) {
        _developerImageResource.value = resourceId
    }

    fun setDeveloperLinks(links: List<LinkItem>) {
        _developerLinks.value = links
    }
}