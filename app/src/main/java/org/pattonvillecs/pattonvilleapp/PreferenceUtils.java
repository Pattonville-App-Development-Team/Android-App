package org.pattonvillecs.pattonvilleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public final class PreferenceUtils {

    public static String[] getSelectedSchools(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        Set<String> selections = sharedPrefs.getStringSet("schoolselection", null);

        if (selections == null) {
            return new String[]{};
        } else {
            return selections.toArray(new String[]{});
        }
    }

    public static boolean getPowerSchoolIntent(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        return sharedPrefs.getBoolean("powerschoolintent", false);
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }
}
