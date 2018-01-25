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

package org.pattonvillecs.pattonvilleapp.viewmodel.directory

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.view.ui.directory.AbstractDirectoryItem
import org.pattonvillecs.pattonvilleapp.view.ui.directory.AllDataSourcesDirectoryItem
import org.pattonvillecs.pattonvilleapp.view.ui.directory.SingleDataSourceDirectoryItem

/**
 * Created by Mitchell Skaggs on 12/10/2017.
 */
class DirectoryListFragmentViewModel(application: Application) : AndroidViewModel(application) {
    val directoryItems by lazy {
        MutableLiveData<List<AbstractDirectoryItem>>().apply {
            val list = mutableListOf<AbstractDirectoryItem>()

            list.addAll(DataSource.ALL.sortedWith(DataSource.DEFAULT_ORDERING).map { SingleDataSourceDirectoryItem(it) })
            list.add(AllDataSourcesDirectoryItem())

            value = list
        }
    }
}