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

package org.pattonvillecs.pattonvilleapp.preferences

import android.arch.lifecycle.LiveData
import android.content.SharedPreferences

abstract class SharedPreferenceLiveData<T>(private val sharedPrefs: SharedPreferences,
                                           private val key: String,
                                           private val defValue: T) : LiveData<T>() {

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == this.key) {
            value = getValueFromPreferences(sharedPrefs, key, defValue)
        }
    }

    abstract fun getValueFromPreferences(sharedPrefs: SharedPreferences, key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(sharedPrefs, key, defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}

private class SharedPreferenceLiveDataFunction<T>(sharedPrefs: SharedPreferences,
                                                  key: String,
                                                  defValue: T,
                                                  val function: (SharedPreferences, String, T) -> T) : SharedPreferenceLiveData<T>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(sharedPrefs: SharedPreferences, key: String, defValue: T): T =
            function(sharedPrefs, key, defValue)
}

fun SharedPreferences.intLiveData(key: String, defValue: Int): SharedPreferenceLiveData<Int> =
        SharedPreferenceLiveDataFunction(this, key, defValue) { fSharedPrefs, fKey, fDefValue -> fSharedPrefs.getInt(fKey, fDefValue) }

fun SharedPreferences.stringLiveData(key: String, defValue: String): SharedPreferenceLiveData<String> =
        SharedPreferenceLiveDataFunction(this, key, defValue) { fSharedPrefs, fKey, fDefValue -> fSharedPrefs.getString(fKey, fDefValue) }

fun SharedPreferences.booleanLiveData(key: String, defValue: Boolean): SharedPreferenceLiveData<Boolean> =
        SharedPreferenceLiveDataFunction(this, key, defValue) { fSharedPrefs, fKey, fDefValue -> fSharedPrefs.getBoolean(fKey, fDefValue) }

fun SharedPreferences.floatLiveData(key: String, defValue: Float): SharedPreferenceLiveData<Float> =
        SharedPreferenceLiveDataFunction(this, key, defValue) { fSharedPrefs, fKey, fDefValue -> fSharedPrefs.getFloat(fKey, fDefValue) }

fun SharedPreferences.longLiveData(key: String, defValue: Long): SharedPreferenceLiveData<Long> =
        SharedPreferenceLiveDataFunction(this, key, defValue) { fSharedPrefs, fKey, fDefValue -> fSharedPrefs.getLong(fKey, fDefValue) }

fun SharedPreferences.stringSetLiveData(key: String, defValue: Set<String>): SharedPreferenceLiveData<Set<String>> =
        SharedPreferenceLiveDataFunction(this, key, defValue) { fSharedPrefs, fKey, fDefValue -> fSharedPrefs.getStringSet(fKey, fDefValue) }