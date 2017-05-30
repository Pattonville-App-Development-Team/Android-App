package org.pattonvillecs.pattonvilleapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.annimon.stream.Stream;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Final class to handle retrieving saved preference values
 *
 * @author Nathan Skelton
 */
public final class PreferenceUtils {
    public static final String APP_INTRO_FIRST_START_PREFERENCE_KEY = "first_start";
    static final String SCHOOL_SELECTION_PREFERENCE_KEY = "school_selection";
    private static final String POWERSCHOOL_INTENT_PREFERENCE_KEY = "powerschool_intent";
    private static final String NUTRISLICE_INTENT_PREFERENCE_KEY = "nutrislice_intent";
    private static final String HOME_NEWS_AMOUNT_KEY = "home_news_amount";
    private static final String HOME_EVENTS_AMOUNT_KEY = "home_events_amount";
    private static final String HOME_PINNED_AMOUNT_KEY = "home_pinned_amount";

    /**
     * Private constructor to prevent the creation of objects of this class
     */
    private PreferenceUtils() {
    }

    public static Set<DataSource> getSelectedSchoolsSet(Context context) {
        return getSelectedSchoolsSet(getSharedPreferences(context));
    }

    public static boolean getPowerSchoolIntent(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        return sharedPrefs.getBoolean(POWERSCHOOL_INTENT_PREFERENCE_KEY, false);
    }

    public static int getHomeNewsAmount(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        return sharedPrefs.getInt(HOME_NEWS_AMOUNT_KEY, 3);
    }

    public static int getHomeEventsAmount(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        return sharedPrefs.getInt(HOME_EVENTS_AMOUNT_KEY, 3);
    }

    public static int getHomePinnedAmount(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        return sharedPrefs.getInt(HOME_PINNED_AMOUNT_KEY, 3);
    }

    public static boolean getNutrisliceIntent(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        return sharedPrefs.getBoolean(NUTRISLICE_INTENT_PREFERENCE_KEY, false);
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static Set<DataSource> getSelectedSchoolsSet(SharedPreferences sharedPreferences) {
        return Stream.of(sharedPreferences.getStringSet(SCHOOL_SELECTION_PREFERENCE_KEY, new HashSet<>()))
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

    public static boolean getCarouselVisible(Context context) {
        return getSharedPreferences(context).getBoolean(context.getString(R.string.key_carousel_visibility), true);
    }
}
