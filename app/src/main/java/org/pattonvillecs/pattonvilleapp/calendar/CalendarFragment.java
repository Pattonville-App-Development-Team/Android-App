/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pattonvillecs.pattonvilleapp.calendar;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.pattonvillecs.pattonvilleapp.MainActivity;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.util.Arrays;
import java.util.List;

import static org.pattonvillecs.pattonvilleapp.calendar.data.CalendarParsingUpdateData.CALENDAR_LISTENER_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    private static final String KEY_CURRENT_TAB = "CURRENT_TAB";
    private static final String TAG = "CalendarFragment";
    private ViewPager viewPager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PattonvilleApplication pattonvilleApplication;
    private PauseableListener<CalendarParsingUpdateData> listener;
    private TabLayout tabLayout;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(true);
                    new Handler().postDelayed(this::onRefresh, 500);
                    return true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach called");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_calendar_action_bar_menu_main, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated called");
        getActivity().setTitle(R.string.title_fragment_calendar);

        tabLayout = ((MainActivity) getActivity()).getTabLayout();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate called");
        setHasOptionsMenu(true);

        pattonvilleApplication = PattonvilleApplication.get(getActivity());
        listener = new PauseableListener<CalendarParsingUpdateData>(true) {
            @Override
            public int getIdentifier() {
                return CALENDAR_LISTENER_ID;
            }

            @Override
            public void onReceiveData(CalendarParsingUpdateData data) {
                super.onReceiveData(data);
                Log.d(TAG, "Received new data!");
                Log.d(TAG, "Size: " + data.getRunningCalendarAsyncTasks().size());

                checkRefresh(data);
            }

            private void checkRefresh(CalendarParsingUpdateData data) {
                boolean refresh = data.getRunningCalendarAsyncTasks().size() > 0;
                if (refresh && !swipeRefreshLayout.isRefreshing()) {
                    Log.d(TAG, "Starting refreshing");
                    swipeRefreshLayout.setRefreshing(true);
                } else if (!refresh && swipeRefreshLayout.isRefreshing()) {
                    Log.d(TAG, "Ending refreshing");
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onResume(CalendarParsingUpdateData data) {
                super.onResume(data);
                Log.d(TAG, "Received data after resume!");
                Log.d(TAG, "Size: " + data.getRunningCalendarAsyncTasks().size());

                checkRefresh(data);
            }

            @Override
            public void onPause(CalendarParsingUpdateData data) {
                super.onPause(data);
                Log.d(TAG, "Received data before pause!");
                Log.d(TAG, "Size: " + data.getRunningCalendarAsyncTasks().size());

                checkRefresh(data);
            }
        };
        pattonvilleApplication.registerPauseableListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");

        listener.unattach();
        pattonvilleApplication.unregisterPauseableListener(listener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called");

        listener.attach(pattonvilleApplication);

        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.pager_calendar);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            List<Fragment> fragments = Arrays.asList(
                    CalendarMonthFragment.newInstance(),
                    CalendarEventsFragment.newInstance(),
                    CalendarPinnedFragment.newInstance()
            );

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Month";
                    case 1:
                        return "Events";
                    case 2:
                        return "Pinned";
                    default:
                        return "Broken!";
                }
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_calendar);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setEnabled(false);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_CURRENT_TAB))
                viewPager.setCurrentItem(savedInstanceState.getInt(KEY_CURRENT_TAB));
        }

        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public synchronized void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_TAB, viewPager.getCurrentItem());
    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pattonvilleApplication.hardRefreshCalendarData();
            }
        }, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        listener.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");

        tabLayout.setVisibility(View.GONE);
        tabLayout.setupWithViewPager(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        listener.resume();
    }
}
