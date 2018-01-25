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

package org.pattonvillecs.pattonvilleapp.viewmodel.directory.detail.single

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.service.repository.directory.DirectoryRepository
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.map
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.switchMap
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.FacultyItem
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.IFacultyItem
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.single.DataSourceSummaryItem

/**
 * Created by Mitchell Skaggs on 12/10/2017.
 */
class SingleDataSourceDirectoryDetailActivityViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var directoryRepository: DirectoryRepository

    private val _dataSource: MutableLiveData<DataSource> = MutableLiveData()
    val dataSource: LiveData<DataSource> = _dataSource

    private val _searchText: MutableLiveData<String?> = MutableLiveData()
    val searchText: LiveData<String?> = _searchText

    val title: LiveData<String> by lazy {
        dataSource.map { "${it.shortName} Directory" }
    }

    fun setDataSource(dataSource: DataSource) {
        _dataSource.value = dataSource
    }

    val facultyItems: LiveData<List<IFacultyItem<*>>> by lazy {
        dataSource.switchMap { dataSource ->
            directoryRepository.getFacultyFromLocations(dataSource).map {
                mutableListOf<IFacultyItem<*>>(DataSourceSummaryItem(dataSource)) + it.sorted().map { FacultyItem(it) }
            }
        }
    }

    fun setSearchText(it: String?) {
        _searchText.value = it
    }
}