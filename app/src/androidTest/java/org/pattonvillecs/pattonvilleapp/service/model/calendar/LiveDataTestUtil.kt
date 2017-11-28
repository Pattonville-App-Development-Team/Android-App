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

package org.pattonvillecs.pattonvilleapp.service.model.calendar

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * This class waits for an update from the [LiveData] and returns the first value.
 *
 * @author Mitchell Skaggs
 * @since 1.2.0
 */

@Throws(InterruptedException::class)
fun <T> LiveData<T>.awaitValue(): T {
    val data = AtomicReference<T>()
    val latch = CountDownLatch(1)
    this.observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            data.set(t)
            latch.countDown()
            this@awaitValue.removeObserver(this)
        }
    })
    latch.await(2, TimeUnit.SECONDS)
    return data.get()
}
