package org.pattonvillecs.pattonvilleapp.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarFragment.
     */
    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);


        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager_calendar);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;

                switch (position) {
                    case 0:
                        fragment = HomeFragment.newInstance();
                        break;
                    case 1:
                        fragment = NewsFragment.newInstance();
                        break;
                    case 2:
                        fragment = SettingsFragment.newInstance();
                        break;
                }

                return fragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String title = "null";

                switch (position) {
                    case 0:
                        title = "HomeFragment";
                        break;
                    case 1:
                        title = "NewsFragment";
                        break;
                    case 2:
                        title = "SettingsFragment";
                        break;
                }

                return title;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs_calendar);
        tabs.setupWithViewPager(viewPager);

        return view;
    }
}
