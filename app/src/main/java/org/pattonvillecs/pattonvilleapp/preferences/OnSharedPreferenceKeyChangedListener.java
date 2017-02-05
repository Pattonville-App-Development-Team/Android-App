package org.pattonvillecs.pattonvilleapp.preferences;

import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Mitchell Skaggs on 1/26/17.
 */
public interface OnSharedPreferenceKeyChangedListener {
    Set<String> getListenedKeys();

    void keyChanged(SharedPreferences sharedPreferences, String key);
}
