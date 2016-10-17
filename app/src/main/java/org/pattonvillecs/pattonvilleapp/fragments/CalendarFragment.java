package org.pattonvillecs.pattonvilleapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarEventsFragment;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarMonthFragment;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarPinnedFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    private static final String KEY_CURRENT_TAB = "CURRENT_TAB";

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
        return new CalendarFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_calendar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        final ResourceFragment resourceFragment = ResourceFragment.retrieveResourceFragment(getActivity().getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager_calendar);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;

                switch (position) {
                    case 0:
                        fragment = CalendarMonthFragment.newInstance();
                        break;
                    case 1:
                        fragment = CalendarEventsFragment.newInstance();
                        break;
                    case 2:
                        fragment = CalendarPinnedFragment.newInstance();
                        break;
                }

                return fragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String title = "Broken!";

                switch (position) {
                    case 0:
                        title = "Month";
                        break;
                    case 1:
                        title = "Events";
                        break;
                    case 2:
                        title = "Pinned";
                        break;
                }

                return title;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                resourceFragment.put(KEY_CURRENT_TAB, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs_calendar);
        tabs.setupWithViewPager(viewPager);

        viewPager.setCurrentItem((Integer) resourceFragment.getOrDefault(KEY_CURRENT_TAB, 0));

        return view;
    }
}
