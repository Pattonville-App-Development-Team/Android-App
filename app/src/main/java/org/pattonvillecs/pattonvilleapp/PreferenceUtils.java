package org.pattonvillecs.pattonvilleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Set;

public final class PreferenceUtils {

    public static DataSource[] SELECTED_SCHOOLS = {};

    public static DataSource[] getSelectedSchools(Context context) {
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
