package org.pattonvillecs.pattonvilleapp.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * Created by Mitchell Skaggs on 1/31/17.
 */
public final class InitialPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.initial_preferences);
    }
}
