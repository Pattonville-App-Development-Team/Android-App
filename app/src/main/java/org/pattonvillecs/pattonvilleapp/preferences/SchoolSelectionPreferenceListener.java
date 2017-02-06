package org.pattonvillecs.pattonvilleapp.preferences;

import org.pattonvillecs.pattonvilleapp.PreferenceUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mitchell Skaggs on 1/26/17.
 */

public abstract class SchoolSelectionPreferenceListener implements OnSharedPreferenceKeyChangedListener {
    private static final Set<String> keys;

    static {
        HashSet<String> set = new HashSet<>(1, .75f);
        set.add(PreferenceUtils.SCHOOL_SELECTION_PREFERENCE_KEY);
        keys = Collections.unmodifiableSet(set);
    }

    @Override
    public Set<String> getListenedKeys() {
        return keys;
    }
}
