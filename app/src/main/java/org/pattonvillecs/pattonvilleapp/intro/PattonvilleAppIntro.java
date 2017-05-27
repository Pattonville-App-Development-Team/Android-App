package org.pattonvillecs.pattonvilleapp.intro;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils;

/**
 * Created by Mitchell Skaggs on 1/27/17.
 */

public class PattonvilleAppIntro extends AppIntro2 {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            color = getColor(R.color.colorPrimary);
        else
            color = getResources().getColor(R.color.colorPrimary);

        addSlide(AppIntroPreferenceFragment.newInstance("School Selection",
                "Click the preference to select schools that you wish to receive information from.\n\n" +
                        "To change your selection at a later date, go to Settings.", color));

        showSkipButton(false);
        showStatusBar(false);

        setFlowAnimation();

        //setWizardMode(true);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        SharedPreferences.Editor editor = PreferenceUtils.getSharedPreferences(this).edit();
        //  Edit preference to make it false because we don't want this to run again
        editor.putBoolean(PreferenceUtils.APP_INTRO_FIRST_START_PREFERENCE_KEY, false);
        editor.apply();

        finish();
    }

}
