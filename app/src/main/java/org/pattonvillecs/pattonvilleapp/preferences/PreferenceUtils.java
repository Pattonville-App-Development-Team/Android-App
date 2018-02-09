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

package org.pattonvillecs.pattonvilleapp.preferences;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.service.model.DataSource;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.pattonvillecs.pattonvilleapp.preferences.SharedPreferenceLiveDataKt.booleanLiveData;
import static org.pattonvillecs.pattonvilleapp.preferences.SharedPreferenceLiveDataKt.intLiveData;
import static org.pattonvillecs.pattonvilleapp.preferences.SharedPreferenceLiveDataKt.stringSetLiveData;

/**
 * Final class to handle retrieving saved preference values
 *
 * @author Nathan Skelton
 */
public final class PreferenceUtils {
    public static final String APP_INTRO_FIRST_START_PREFERENCE_KEY = "first_start";
    public static final String SCHOOL_SELECTION_PREFERENCE_KEY = "school_selection";

    /**
     * Private constructor to prevent the creation of objects of this class
     */
    private PreferenceUtils() {
    }

    public static Set<DataSource> getSelectedSchoolsSet(Context context) {
        return convertStringsToDataSources(getSharedPreferences(context).getStringSet(context.getString(R.string.key_school_selection), new HashSet<>()));
    }

    public static boolean getPowerSchoolIntent(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        return sharedPrefs.getBoolean(context.getString(R.string.key_powerschool_intent), false);
    }

    public static LiveData<Integer> getHomeNewsLiveData(Context context) {
        return intLiveData(getSharedPreferences(context), context.getString(R.string.key_home_newsamount), 3);
    }

    public static LiveData<Integer> getHomeEventsLiveData(Context context) {
        return intLiveData(getSharedPreferences(context), context.getString(R.string.key_home_eventsamount), 3);
    }

    public static LiveData<Integer> getHomePinnedLiveData(Context context) {
        return intLiveData(getSharedPreferences(context), context.getString(R.string.key_home_pinnedamount), 3);
    }

    public static boolean getNutrisliceIntent(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        return sharedPrefs.getBoolean(context.getString(R.string.key_nutrislice_intent), false);
    }

    @NonNull
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    private static Set<DataSource> convertStringsToDataSources(Set<String> strings) {
        return Stream.of(strings)
                .map(s -> {
                    switch (s) {
                        case "High School":
                            return DataSource.HIGH_SCHOOL;
                        case "Heights":
                            return DataSource.HEIGHTS_MIDDLE_SCHOOL;
                        case "Holman":
                            return DataSource.HOLMAN_MIDDLE_SCHOOL;
                        case "Remington":
                            return DataSource.REMINGTON_TRADITIONAL_SCHOOL;
                        case "Bridgeway":
                            return DataSource.BRIDGEWAY_ELEMENTARY;
                        case "Drummond":
                            return DataSource.DRUMMOND_ELEMENTARY;
                        case "Rose Acres":
                            return DataSource.ROSE_ACRES_ELEMENTARY;
                        case "Parkwood":
                            return DataSource.PARKWOOD_ELEMENTARY;
                        case "Willow Brook":
                            return DataSource.WILLOW_BROOK_ELEMENTARY;
                        case "Early Childhood":
                            return DataSource.EARLY_CHILDHOOD;
                        default:
                            throw new Error("Invalid school selection value! Was: " + s);
                    }
                }).collect(() -> EnumSet.of(DataSource.DISTRICT), Collection::add);
    }

    @NonNull
    public static LiveData<Set<DataSource>> getSelectedSchoolsLiveData(Context context) {
        return Transformations.map(
                stringSetLiveData(getSharedPreferences(context), context.getString(R.string.key_school_selection), new HashSet<>()),
                PreferenceUtils::convertStringsToDataSources);
    }

    @NotNull
    public static LiveData<Boolean> getCarouselVisibleLiveData(@NotNull Context context) {
        return booleanLiveData(getSharedPreferences(context), context.getString(R.string.key_carousel_visibility), true);
    }
}
