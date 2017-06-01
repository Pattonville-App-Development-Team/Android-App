package org.pattonvillecs.pattonvilleapp.intro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.github.paolorotolo.appintro.AppIntro2;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils;

/**
 * Created by Mitchell Skaggs on 1/27/17.
 */

public class PattonvilleAppIntro extends AppIntro2 {
    private static final String TAG = PattonvilleAppIntro.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int color = ActivityCompat.getColor(this, R.color.colorPrimary);

        addSlide(AppIntroPreferenceFragment.newInstance("Select Schools",
                "Tap the card to select schools that you wish to receive information from to proceed.\n\n" +
                        "To change your selection at a later date, go to Settings.", color));

        showSkipButton(false);
        showStatusBar(false);
        setProgressButtonEnabled(false);

        setFlowAnimation();

        View view = findViewById(android.R.id.content);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CardView cardView = (CardView) PattonvilleAppIntro.this.findViewById(R.id.intro_preference_cardview);
                if (cardView != null) {
                    Log.i(TAG, "onGlobalLayout: " + cardView);
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    cardView.setOnTouchListener((v, event) -> {
                        Log.i(TAG, "onTouch!");
                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                setProgressButtonEnabled(true);
                                break;
                            default:
                                break;
                        }
                        return false;
                    });
                }
            }
        });

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
