package org.pattonvillecs.pattonvilleapp;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
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

    public static class SettingsFragment extends PreferenceFragment {

        private NumberPickerPreference mNewsPreference;
        private NumberPickerPreference mUpcomingEventsPreference;
        private NumberPickerPreference mPinnedEventsPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);

            mNewsPreference = (NumberPickerPreference) findPreference(getString(R.string.key_home_newsamount));
            mNewsPreference.setSummary(PreferenceUtils.getSharedPreferences(getActivity().getApplicationContext()).getInt(getString(R.string.key_home_newsamount), 3) + " News Articles");
            mNewsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    mNewsPreference.setSummary(newValue + " News Articles");
                    return true;
                }
            });

            mUpcomingEventsPreference = (NumberPickerPreference) findPreference(getString(R.string.key_home_eventsamount));
            mUpcomingEventsPreference.setSummary(PreferenceUtils.getSharedPreferences(getActivity().getApplicationContext()).getInt(getString(R.string.key_home_eventsamount), 3) + " Upcoming Events");
            mUpcomingEventsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    mUpcomingEventsPreference.setSummary(newValue + " Upcoming Events");
                    return true;
                }
            });

            mPinnedEventsPreference = (NumberPickerPreference) findPreference(getString(R.string.key_home_pinnedamount));
            mPinnedEventsPreference.setSummary(PreferenceUtils.getSharedPreferences(getActivity().getApplicationContext()).getInt(getString(R.string.key_home_pinnedamount), 3) + " Pinned Events");
            mPinnedEventsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    mPinnedEventsPreference.setSummary(newValue + " Pinned Events");
                    return true;
                }
            });
        }
    }
}
