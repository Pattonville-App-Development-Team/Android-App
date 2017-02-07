package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import static org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData.CALENDAR_LISTENER_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_CURRENT_TAB = "CURRENT_TAB";
    private static final String TAG = "CalendarFragment";
    private ViewPager viewPager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PattonvilleApplication pattonvilleApplication;
    private PauseableListener<CalendarParsingUpdateData> listener;
    private ViewPager.OnPageChangeListener onPageChangeListener;

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
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pattonvilleApplication = PattonvilleApplication.get(getActivity());
        listener = new PauseableListener<CalendarParsingUpdateData>(true) {
            @Override
            public int getIdentifier() {
                return CALENDAR_LISTENER_ID;
            }

            @Override
            public void onReceiveData(CalendarParsingUpdateData data) {
                super.onReceiveData(data);
                Log.i(TAG, "Received new data!");
                Log.i(TAG, "Size: " + data.getRunningCalendarAsyncTasks().size());

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
                Log.i(TAG, "Received data after resume!");
                Log.i(TAG, "Size: " + data.getRunningCalendarAsyncTasks().size());

                checkRefresh(data);
            }

            @Override
            public void onPause(CalendarParsingUpdateData data) {
                super.onPause(data);
                Log.i(TAG, "Received data before pause!");
                Log.i(TAG, "Size: " + data.getRunningCalendarAsyncTasks().size());

                checkRefresh(data);
            }
        };
        pattonvilleApplication.registerPauseableListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        listener.unattach();
        pattonvilleApplication.unregisterPauseableListener(listener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        viewPager.removeOnPageChangeListener(onPageChangeListener);
        swipeRefreshLayout.setOnRefreshListener(null);
    }

    @Override
    public void onStart() {
        super.onStart();

        listener.attach(pattonvilleApplication);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.pager_calendar);
        viewPager.setOffscreenPageLimit(2);
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
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                setSwipeRefreshEnabledDisabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        };
        viewPager.addOnPageChangeListener(onPageChangeListener);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs_calendar);
        tabs.setupWithViewPager(viewPager);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_calendar);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_CURRENT_TAB))
                viewPager.setCurrentItem(savedInstanceState.getInt(KEY_CURRENT_TAB));
        }

        return view;
    }

    public void setSwipeRefreshEnabledDisabled(boolean enabled) {
        if (swipeRefreshLayout != null) {
            Log.i(TAG, "Set swipe state to: " + enabled + " from " + tag);
            swipeRefreshLayout.setEnabled(enabled);
        }
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

    @Override
    public void onRefresh() {
        pattonvilleApplication.refreshCalendarData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause called");
        listener.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume called");
        listener.resume();
    }
}
