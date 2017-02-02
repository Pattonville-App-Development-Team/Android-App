package org.pattonvillecs.pattonvilleapp.fragments.intro;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * Created by skaggsm on 1/27/17.
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

        addSlide(AppIntro2Fragment.newInstance("Welcome!", "", R.drawable.psd_logo, color));

        addSlide(AppIntro2Fragment.newInstance("The Spotlight", "The Spotlight explains features of the app when you first encounter them.", R.drawable.appintro_spotlight_image, color));

        addSlide(AppIntroPreferenceFragment.newInstance("Preferences", "Check schools that you wish to receive news, calendar events, and other information from. To change this at a later date, navigate to the app settings.", color));

        addSlide(AppIntro2Fragment.newInstance("", "Tap the check mark to enter the app...", R.drawable.psd_logo, color));

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
        finish();
    }

}