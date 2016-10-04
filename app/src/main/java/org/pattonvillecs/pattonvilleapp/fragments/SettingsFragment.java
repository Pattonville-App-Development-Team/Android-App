package org.pattonvillecs.pattonvilleapp.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * Created by skeltonn on 10/4/16.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
