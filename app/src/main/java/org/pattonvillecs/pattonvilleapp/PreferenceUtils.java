package org.pattonvillecs.pattonvilleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.annimon.stream.Stream;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class PreferenceUtils {
    public static final String SCHOOL_SELECTION_PREFERENCE_KEY = "school_selection";
    public static final String POWERSCHOOL_INTENT_PREFERENCE_KEY = "powerschool_intent";

    private PreferenceUtils() {
    }

    public static Set<DataSource> getSelectedSchoolsSet(Context context) {
        return Stream.of(getSharedPreferences(context).getStringSet(SCHOOL_SELECTION_PREFERENCE_KEY, new HashSet<String>()))
                .map(new Function<String, DataSource>() {
                    @Override
                    public DataSource apply(String s) {
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
                            default:
                                throw new Error("Invalid school selection value! Was: " + s);
                        }
                    }
                }).collect(new Supplier<EnumSet<DataSource>>() {
                    @Override
                    public EnumSet<DataSource> get() {
                        return EnumSet.noneOf(DataSource.class);
                    }
                }, new BiConsumer<EnumSet<DataSource>, DataSource>() {
                    @Override
                    public void accept(EnumSet<DataSource> value1, DataSource value2) {
                        value1.add(value2);
                    }
                });
    }

    public static boolean getPowerSchoolIntent(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        return sharedPrefs.getBoolean(POWERSCHOOL_INTENT_PREFERENCE_KEY, false);
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static void registerOnSharedPreferenceChangeListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences(context).registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(listener);
    }
}
