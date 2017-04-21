package org.pattonvillecs.pattonvilleapp.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * Activity plus inner fragment to handle preferences for the application
 *
 * @author Nathan Skelton
 */
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

    /**
     * Our preference fragment which is inserted into SettingsActivity
     */
    public static class SettingsFragment extends PreferenceFragment {

        private NumberPickerPreference mNewsPreference;
        private NumberPickerPreference mUpcomingEventsPreference;
        private NumberPickerPreference mPinnedEventsPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);

            Context context = getActivity().getApplicationContext();

            // Setup the summary for each preference, through setting an onPreferenceChangeListener
            // and setting the initial summary string

            mNewsPreference = (NumberPickerPreference) findPreference(getString(R.string.key_home_newsamount));
            mNewsPreference.setSummary(PreferenceUtils.getSharedPreferences(context)
                    .getInt(getString(R.string.key_home_newsamount), 3) +
                    context.getString(R.string.preference_summary_news));
            mNewsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                mNewsPreference.setSummary(newValue +
                        context.getString(R.string.preference_summary_news));
                return true;
            });

            mUpcomingEventsPreference = (NumberPickerPreference) findPreference(getString(R.string.key_home_eventsamount));
            mUpcomingEventsPreference.setSummary(PreferenceUtils.getSharedPreferences(context)
                    .getInt(getString(R.string.key_home_eventsamount), 3) +
                    context.getString(R.string.preference_summary_upcoming));
            mUpcomingEventsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                mUpcomingEventsPreference.setSummary(newValue +
                        context.getString(R.string.preference_summary_upcoming));
                return true;
            });

            mPinnedEventsPreference = (NumberPickerPreference) findPreference(getString(R.string.key_home_pinnedamount));
            mPinnedEventsPreference.setSummary(PreferenceUtils.getSharedPreferences(context)
                    .getInt(getString(R.string.key_home_pinnedamount), 3) +
                    context.getString(R.string.preference_summary_pinned));
            mPinnedEventsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                mPinnedEventsPreference.setSummary(newValue +
                        context.getString(R.string.preference_summary_pinned));
                return true;
            });
        }
    }
}
