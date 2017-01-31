package org.pattonvillecs.pattonvilleapp;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroBaseFragment;
import com.github.paolorotolo.appintro.CustomFontCache;

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

        addSlide(AppIntroPreferenceFragment.newInstance("Preferences", "Description", color));

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

    public static final class AppIntroPreferenceFragment extends AppIntroBaseFragment {
        private int drawable, bgColor, titleColor, descColor, layoutId;
        private String title, titleTypeface, description, descTypeface;

        public static AppIntroPreferenceFragment newInstance(String title, String description, int color) {
            AppIntroPreferenceFragment appIntroPreferenceFragment = new AppIntroPreferenceFragment();
            Bundle bundle = new Bundle();

            bundle.putString(ARG_TITLE, title);
            bundle.putString(ARG_DESC, description);
            bundle.putInt(ARG_BG_COLOR, color);

            appIntroPreferenceFragment.setArguments(bundle);
            return appIntroPreferenceFragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(getLayoutId(), container, false);

            TextView title = (TextView) view.findViewById(com.github.paolorotolo.appintro.R.id.title);
            TextView description = (TextView) view.findViewById(com.github.paolorotolo.appintro.R.id.description);
            LinearLayout mainLayout = (LinearLayout) view.findViewById(com.github.paolorotolo.appintro.R.id.main);

            title.setText(this.title);
            if (titleColor != 0) {
                title.setTextColor(titleColor);
            }
            if (titleTypeface != null && titleTypeface.equals("")) {
                if (CustomFontCache.get(titleTypeface, getContext()) != null) {
                    title.setTypeface(CustomFontCache.get(titleTypeface, getContext()));
                }
            }
            description.setText(this.description);
            if (descColor != 0) {
                description.setTextColor(descColor);
            }
            if (descTypeface != null && descTypeface.equals("")) {
                if (CustomFontCache.get(descTypeface, getContext()) != null) {
                    description.setTypeface(CustomFontCache.get(descTypeface, getContext()));
                }
            }
            mainLayout.setBackgroundColor(bgColor);

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new InitialPreferenceFragment());
            transaction.commit();

            return view;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setRetainInstance(true);

            if (getArguments() != null && getArguments().size() != 0) {
                drawable = getArguments().getInt(ARG_DRAWABLE);
                title = getArguments().getString(ARG_TITLE);
                titleTypeface = getArguments().containsKey(ARG_TITLE_TYPEFACE) ?
                        getArguments().getString(ARG_TITLE_TYPEFACE) : "";
                description = getArguments().getString(ARG_DESC);
                descTypeface = getArguments().containsKey(ARG_DESC_TYPEFACE) ?
                        getArguments().getString(ARG_DESC_TYPEFACE) : "";
                bgColor = getArguments().getInt(ARG_BG_COLOR);
                titleColor = getArguments().containsKey(ARG_TITLE_COLOR) ?
                        getArguments().getInt(ARG_TITLE_COLOR) : 0;
                descColor = getArguments().containsKey(ARG_DESC_COLOR) ?
                        getArguments().getInt(ARG_DESC_COLOR) : 0;
            }
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (savedInstanceState != null) {
                drawable = savedInstanceState.getInt(ARG_DRAWABLE);
                title = savedInstanceState.getString(ARG_TITLE);
                titleTypeface = savedInstanceState.getString(ARG_TITLE_TYPEFACE);
                description = savedInstanceState.getString(ARG_DESC);
                descTypeface = savedInstanceState.getString(ARG_DESC_TYPEFACE);
                bgColor = savedInstanceState.getInt(ARG_BG_COLOR);
                titleColor = savedInstanceState.getInt(ARG_TITLE_COLOR);
                descColor = savedInstanceState.getInt(ARG_DESC_COLOR);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putInt(ARG_DRAWABLE, drawable);
            outState.putString(ARG_TITLE, title);
            outState.putString(ARG_DESC, description);
            outState.putInt(ARG_BG_COLOR, bgColor);
            outState.putInt(ARG_TITLE_COLOR, titleColor);
            outState.putInt(ARG_DESC_COLOR, descColor);
            super.onSaveInstanceState(outState);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.appintro_preferences_fragment;
        }
    }

    public static final class InitialPreferenceFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.initial_preferences);
        }
    }
}
