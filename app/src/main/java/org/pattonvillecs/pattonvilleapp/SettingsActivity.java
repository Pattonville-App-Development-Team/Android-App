package org.pattonvillecs.pattonvilleapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.pattonvillecs.pattonvilleapp.preferences.NumberPickerPreference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle(R.string.title_activity_settings);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("SELECTED SCHOOLS", "These are selected: " + PreferenceUtils.getSelectedSchoolsSet(this));
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private NumberPickerPreference mNumberPickerPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);
            PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(this);

            mNumberPickerPreference = (NumberPickerPreference) findPreference("homenewsamount");
            mNumberPickerPreference.setSummary(PreferenceUtils.getSharedPreferences(getActivity().getApplicationContext()).getInt("homenewsamount", 3) + " News Articles");


        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            switch (key) {
                case "homenewsamount":
                    mNumberPickerPreference.setSummary(sharedPreferences.getInt("homenewsamount", 3) + " News Articles");
                    break;
            }
        }
    }
}
