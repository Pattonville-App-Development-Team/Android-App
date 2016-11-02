package org.pattonvillecs.pattonvilleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * Created by skeltonn on 10/31/16.
 */

public final class PreferenceUtils {

    public static String[] getSelectedSchools(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> selections = sharedPrefs.getStringSet("schoolselection", null);

        if (selections == null) {
            return new String[]{};
        } else {
            return selections.toArray(new String[]{});
        }
    }
}
