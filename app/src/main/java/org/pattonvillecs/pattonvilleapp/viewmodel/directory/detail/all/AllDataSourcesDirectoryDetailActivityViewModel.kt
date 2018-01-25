/*
 * Copyright (C) 2017 - 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.viewmodel.directory.detail.all

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.service.repository.directory.DirectoryRepository
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.map
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.FacultyItem
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.all.DataSourceHeader

/**
 * Created by Mitchell Skaggs on 12/10/2017.
 */
class AllDataSourcesDirectoryDetailActivityViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var directoryRepository: DirectoryRepository

    private val _searchText: MutableLiveData<String?> = MutableLiveData()
    val searchText: LiveData<String?> = _searchText

    val title: LiveData<String> = MutableLiveData<String>().apply {
        value = "All Staff Directory"
    }

    val facultyItems: LiveData<List<FacultyItem>> by lazy {
        directoryRepository.getFacultyFromLocations(DataSource.ALL.toList()).map {
            val headerMap = mutableMapOf<DataSource, DataSourceHeader>().withDefault { DataSourceHeader(it) }
            it.sorted().map { FacultyItem(it, headerMap.getValue(it.location!!)) }
        }
    }

    fun setSearchText(it: String?) {
        _searchText.value = it
    }
}