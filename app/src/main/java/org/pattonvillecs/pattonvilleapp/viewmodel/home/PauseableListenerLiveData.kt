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

package org.pattonvillecs.pattonvilleapp.viewmodel.home

import android.arch.lifecycle.LiveData
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListenable
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener

class PauseableListenerLiveData<T>(private val listenerId: Int, private val pauseableListenable: PauseableListenable) : LiveData<T>() {

    private val listener = object : PauseableListener<T>(false) {
        override fun getIdentifier(): Int {
            return listenerId
        }

        override fun onReceiveData(data: T) {
            super.onReceiveData(data)
            value = data
        }

        override fun onResume(data: T) {
            super.onResume(data)
            value = data
        }

        override fun onPause(data: T) {
            super.onPause(data)
            value = data
        }
    }

    override fun onActive() {
        super.onActive()
        pauseableListenable.registerPauseableListener(listener)
        listener.attach(pauseableListenable)
        listener.resume()
    }

    override fun onInactive() {
        super.onInactive()
        listener.pause()
        listener.unattach()
        pauseableListenable.unregisterPauseableListener(listener)
    }
}