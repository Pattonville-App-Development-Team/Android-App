package org.pattonvillecs.pattonvilleapp.fragments.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntroBaseFragment;
import com.github.paolorotolo.appintro.CustomFontCache;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * Created by Mitchell Skaggs on 1/31/17.
 */
public final class AppIntroPreferenceFragment extends AppIntroBaseFragment {
    private int drawable;
    private int bgColor;
    private int titleColor;
    private int descColor;
    private String title;
    private String titleTypeface;
    private String description;
    private String descTypeface;
    private InitialPreferenceFragment fragment;

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

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            fragment = new InitialPreferenceFragment();
            transaction.replace(R.id.content_frame, fragment);
            transaction.commit();
        }

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(false); //Undo from super

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
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_DRAWABLE, drawable);
        outState.putString(ARG_TITLE, title);
        outState.putString(ARG_DESC, description);
        outState.putInt(ARG_BG_COLOR, bgColor);
        outState.putInt(ARG_TITLE_COLOR, titleColor);
        outState.putInt(ARG_DESC_COLOR, descColor);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.appintro_preferences_fragment;
    }
}
