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

        private NumberPickerPreference mNewsPreference;
        private NumberPickerPreference mUpcomingEventsPreference;
        private NumberPickerPreference mPinnedEventsPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);
            PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(this);

            mNewsPreference = (NumberPickerPreference) findPreference(getString(R.string.key_home_newsamount));
            mNewsPreference.setSummary(PreferenceUtils.getSharedPreferences(getActivity().getApplicationContext()).getInt(getString(R.string.key_home_newsamount), 3) + " News Articles");

            mUpcomingEventsPreference = (NumberPickerPreference) findPreference(getString(R.string.key_home_eventsamount));
            mUpcomingEventsPreference.setSummary(PreferenceUtils.getSharedPreferences(getActivity().getApplicationContext()).getInt(getString(R.string.key_home_eventsamount), 3) + " Upcoming Events");

            mPinnedEventsPreference = (NumberPickerPreference) findPreference(getString(R.string.key_home_pinnedamount));
            mPinnedEventsPreference.setSummary(PreferenceUtils.getSharedPreferences(getActivity().getApplicationContext()).getInt(getString(R.string.key_home_pinnedamount), 3) + " Pinned Events");

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            switch (key) {
                case PreferenceUtils.HOME_NEWS_AMOUNT_KEY:
                    mNewsPreference.setSummary(sharedPreferences.getInt(getString(R.string.key_home_newsamount), 3) + " News Articles");
                    break;
                case PreferenceUtils.HOME_EVENTS_AMOUNT_KEY:
                    mUpcomingEventsPreference.setSummary(sharedPreferences.getInt(getString(R.string.key_home_eventsamount), 3) + " Upcoming Events");
                    break;
                case PreferenceUtils.HOME_PINNED_AMOUNT_KEY:
                    mPinnedEventsPreference.setSummary(sharedPreferences.getInt(getString(R.string.key_home_pinnedamount), 3) + " Pinned Events");
                    break;
            }
        }
    }
}
