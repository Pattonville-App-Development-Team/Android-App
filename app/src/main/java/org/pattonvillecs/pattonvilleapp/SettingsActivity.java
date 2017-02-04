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

            mNewsPreference = (NumberPickerPreference) findPreference("homenewsamount");
            mNewsPreference.setSummary(PreferenceUtils.getSharedPreferences(getActivity().getApplicationContext()).getInt("homenewsamount", 3) + " News Articles");

            mUpcomingEventsPreference = (NumberPickerPreference) findPreference("homeeventsamount");
            mUpcomingEventsPreference.setSummary(PreferenceUtils.getSharedPreferences(getActivity().getApplicationContext()).getInt("homeeventsamount", 3) + " Upcoming Events");

            mPinnedEventsPreference = (NumberPickerPreference) findPreference("homepinnedamount");
            mPinnedEventsPreference.setSummary(PreferenceUtils.getSharedPreferences(getActivity().getApplicationContext()).getInt("homepinnedamount", 3) + " Pinned Events");

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            switch (key) {
                case "homenewsamount":
                    mNewsPreference.setSummary(sharedPreferences.getInt("homenewsamount", 3) + " News Articles");
                    break;
                case "homeeventsamount":
                    mUpcomingEventsPreference.setSummary(sharedPreferences.getInt("homeeventsamount", 3) + " Upcoming Events");
                    break;
                case "homepinnedamount":
                    mPinnedEventsPreference.setSummary(sharedPreferences.getInt("homepinnedamount", 3) + " Pinned Events");
                    break;
            }
        }
    }
}
