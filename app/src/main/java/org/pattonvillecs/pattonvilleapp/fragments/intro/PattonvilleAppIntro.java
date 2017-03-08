package org.pattonvillecs.pattonvilleapp.fragments.intro;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;

import org.pattonvillecs.pattonvilleapp.R;

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
                "Select schools that you wish to receive information from. " +
                        "To change this at a later date, go to Settings", color));

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
