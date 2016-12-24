package org.pattonvillecs.pattonvilleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class PreferenceUtils {
    @Deprecated
    static DataSource[] SELECTED_SCHOOLS = {};

    private PreferenceUtils() {
    }

    public static Set<DataSource> getSelectedSchoolsSet(Context context) {
        final EnumSet<DataSource> enumSet = EnumSet.noneOf(DataSource.class);
        Stream.of(getSharedPreferences(context).getStringSet("schoolselection", new HashSet<String>()))
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
                })
                .forEach(new Consumer<DataSource>() {
                    @Override
                    public void accept(DataSource dataSource) {
                        enumSet.add(dataSource);
                    }
                });
        return enumSet;
    }

    @Deprecated
    static DataSource[] getSelectedSchools(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        Set<String> selections = sharedPrefs.getStringSet("schoolselection", null);

        if (selections == null) {
            return new DataSource[]{};
        }

        ArrayList<DataSource> schools = new ArrayList<>();

        for (String selection : selections.toArray(new String[]{})) {

            switch (selection) {
                case "High School":
                    schools.add(DataSource.HIGH_SCHOOL);
                    break;
                case "Heights":
                    schools.add(DataSource.HEIGHTS_MIDDLE_SCHOOL);
                    break;
                case "Holman":
                    schools.add(DataSource.HOLMAN_MIDDLE_SCHOOL);
                    break;
                case "Remington":
                    schools.add(DataSource.REMINGTON_TRADITIONAL_SCHOOL);
                    break;
                case "Bridgeway":
                    schools.add(DataSource.BRIDGEWAY_ELEMENTARY);
                    break;
                case "Drummond":
                    schools.add(DataSource.DRUMMOND_ELEMENTARY);
                    break;
                case "Rose Acres":
                    schools.add(DataSource.ROSE_ACRES_ELEMENTARY);
                    break;
                case "Parkwood":
                    schools.add(DataSource.PARKWOOD_ELEMENTARY);
                    break;
                case "Willow Brook":
                    schools.add(DataSource.WILLOW_BROOK_ELEMENTARY);
                    break;
            }
        }

        return schools.toArray(new DataSource[]{});
    }

    public static boolean getPowerSchoolIntent(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        return sharedPrefs.getBoolean("powerschoolintent", false);
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }
}
