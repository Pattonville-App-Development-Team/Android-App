package org.pattonvillecs.pattonvilleapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarFragment;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarPinnedFragment;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventAdapter;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventFlexibleItem;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.FlexibleHasCalendarDay;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.pinned.PinnedEventsContract;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;
import org.pattonvillecs.pattonvilleapp.news.NewsFragment;
import org.pattonvillecs.pattonvilleapp.news.NewsParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.news.articles.NewsArticle;
import org.pattonvillecs.pattonvilleapp.news.articles.NewsRecyclerViewAdapter;
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import eu.davidea.flexibleadapter.common.DividerItemDecoration;

public class HomeFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final int PINNED_PREFERENCE_VALUES_INDEX = 2;
    private static final int EVENTS_PREFERENCE_VALUES_INDEX = 1;
    private static final int NEWS_PREFERENCE_VALUES_INDEX = 0;
    private static final int PINNED_EVENTS_LOADER_ID = 2;
    public static int[] sampleImages = {
            R.drawable.test_news_1, R.drawable.test_news_2, R.drawable.test_news_3, R.drawable.test_news_4,
            R.drawable.test_news_1, R.drawable.test_news_2, R.drawable.test_news_3, R.drawable.test_news_4,
            R.drawable.test_news_1, R.drawable.test_news_2, R.drawable.test_news_3, R.drawable.test_news_4};
    public static String[] samplePinnedEvents = {"Pinned Event 1", "Pinned Event 2", "Pinned Event 3"};
    private CarouselView carouselView;
    private TextView newsSeeMoreTextView, upcomingSeeMoreTextView, pinnedSeeMoreTextView, homeNewsLoadingTextView;
    private ImageView newsSeeMoreArrow, upcomingSeeMoreArrow, pinnedSeeMoreArrow;
    private NavigationView mNavigationView;
    private RecyclerView mHomeNewsRecyclerView, mHomeCalendarEventRecyclerView;
    private NewsRecyclerViewAdapter mHomeNewsAdapter;
    private PattonvilleApplication pattonvilleApplication;
    private List<NewsArticle> mNewsArticles;
    private PauseableListener<CalendarParsingUpdateData> calendarListener;
    private EventAdapter mHomeCalendarEventAdapter;
    private EventAdapter mHomeCalendarPinnedAdapter;
    private TreeSet<EventFlexibleItem> calendarData = new TreeSet<>();
    private int[] preferenceValues = new int[3];
    private ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };
    private PauseableListener<NewsParsingUpdateData> homeListener;
    private OnFragmentInteractionListener mListener;
    private Loader<Cursor> cursorLoader;
    private Set<String> pinnedUIDs = new HashSet<>();
    private LinearLayout homeNewsHeader;
    private LinearLayout homeEventsHeader;
    private LinearLayout homePinnedHeader;
    private TextView homeNoItemsView;
    private RecyclerView mHomeCalendarPinnedRecyclerView;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        calendarListener.attach(pattonvilleApplication);
        homeListener.attach(pattonvilleApplication);
    }

    @Override
    public void onResume() {
        super.onResume();
        homeListener.resume();
        calendarListener.resume();

        //TODO fix this terrible hack. There needs to be a method to recalculate each news section individually
        if (preferenceValues[NEWS_PREFERENCE_VALUES_INDEX] != PreferenceUtils.getHomeNewsAmount(getContext()) || preferenceValues[EVENTS_PREFERENCE_VALUES_INDEX] != PreferenceUtils.getHomeEventsAmount(getContext()) || preferenceValues[PINNED_PREFERENCE_VALUES_INDEX] != PreferenceUtils.getHomePinnedAmount(getContext())) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        homeListener.pause();
        calendarListener.pause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pattonvilleApplication = PattonvilleApplication.get(getActivity());
        cursorLoader = getLoaderManager().initLoader(PINNED_EVENTS_LOADER_ID, null, this);
        homeListener = new PauseableListener<NewsParsingUpdateData>(true) {
            @Override
            public int getIdentifier() {
                return NewsParsingUpdateData.NEWS_LISTENER_ID;
            }

            @Override
            public void onReceiveData(NewsParsingUpdateData data) {
                super.onReceiveData(data);
                Log.d(TAG, "Received new data!");
                Log.d(TAG, "Size: " + data.getRunningNewsAsyncTasks().size());

                setNewsArticles(data);
            }

            @Override
            public void onResume(NewsParsingUpdateData data) {
                super.onResume(data);
                Log.d(TAG, "Received data after resume!");
                Log.d(TAG, "Size: " + data.getRunningNewsAsyncTasks().size());

                setNewsArticles(data);
            }

            @Override
            public void onPause(NewsParsingUpdateData data) {
                super.onPause(data);
                Log.d(TAG, "Received data before pause!");
                Log.d(TAG, "Size: " + data.getRunningNewsAsyncTasks().size());

            }

            private void setNewsArticles(NewsParsingUpdateData data) {
                List<NewsArticle> newNewsArticles = Stream.of(data.getNewsData())
                        .flatMap(new Function<Map.Entry<DataSource, List<NewsArticle>>, Stream<NewsArticle>>() {
                            @Override
                            public Stream<NewsArticle> apply(Map.Entry<DataSource, List<NewsArticle>> dataSourceListEntry) {
                                return Stream.of(dataSourceListEntry.getValue());
                            }
                        })
                        .sorted(new Comparator<NewsArticle>() {
                            @Override
                            public int compare(NewsArticle o1, NewsArticle o2) {
                                return -o1.getPublishDate().compareTo(o2.getPublishDate());
                            }
                        })
                        .limit(preferenceValues[NEWS_PREFERENCE_VALUES_INDEX])
                        .collect((Collectors.<NewsArticle>toList()));

                Log.i(TAG, "Loaded news articles from " + data.getNewsData().keySet() + " " + newNewsArticles.size());
                mNewsArticles = newNewsArticles;
                mHomeNewsAdapter.updateDataSet(newNewsArticles, true); // Must be an unused list, copy it if needed

            }
        };
        pattonvilleApplication.registerPauseableListener(homeListener);
        Log.d(TAG, "Registered home listener");

        pattonvilleApplication = PattonvilleApplication.get(getActivity());
        calendarListener = new PauseableListener<CalendarParsingUpdateData>(true) {
            @Override
            public int getIdentifier() {
                return CalendarParsingUpdateData.CALENDAR_LISTENER_ID;
            }

            @Override
            public void onReceiveData(CalendarParsingUpdateData data) {
                super.onReceiveData(data);
                Log.i(TAG, "Received new data!");

                setCalendarData(data.getCalendarData());
            }

            @Override
            public void onResume(CalendarParsingUpdateData data) {
                super.onResume(data);
                Log.i(TAG, "Received data after resume!");

                setCalendarData(data.getCalendarData());
            }

            @Override
            public void onPause(CalendarParsingUpdateData data) {
                super.onPause(data);
                Log.i(TAG, "Received data before pause!");
            }
        };
        pattonvilleApplication.registerPauseableListener(calendarListener);
    }

    public void setCalendarData(TreeSet<EventFlexibleItem> calendarData) {
        this.calendarData = calendarData;
        List<EventFlexibleItem> itemsToAdd = new ArrayList<>();

        final int numCalendarItems = preferenceValues[EVENTS_PREFERENCE_VALUES_INDEX];
        Iterator<EventFlexibleItem> calendarDataIterator = this.calendarData.iterator();
        CalendarDay today = CalendarDay.today();

        while (calendarDataIterator.hasNext() && itemsToAdd.size() < numCalendarItems) {
            EventFlexibleItem currentItem = calendarDataIterator.next();
            if (!currentItem.getCalendarDay().isBefore(today)) {
                itemsToAdd.add(currentItem);
            }
        }

        mHomeCalendarEventAdapter.updateDataSet(new ArrayList<FlexibleHasCalendarDay>(itemsToAdd), true);
        updatePinnedContent();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_home);

        preferenceValues[NEWS_PREFERENCE_VALUES_INDEX] = PreferenceUtils.getHomeNewsAmount(getContext());
        preferenceValues[EVENTS_PREFERENCE_VALUES_INDEX] = PreferenceUtils.getHomeEventsAmount(getContext());
        preferenceValues[PINNED_PREFERENCE_VALUES_INDEX] = PreferenceUtils.getHomePinnedAmount(getContext());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeListener.unattach();
        calendarListener.unattach();
        pattonvilleApplication.unregisterPauseableListener(homeListener);
        pattonvilleApplication.unregisterPauseableListener(calendarListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mHomeNewsRecyclerView = (RecyclerView) view.findViewById(R.id.home_news_recyclerView);
        mHomeCalendarEventRecyclerView = (RecyclerView) view.findViewById(R.id.home_calendar_event_recyclerView);
        mHomeCalendarPinnedRecyclerView = (RecyclerView) view.findViewById(R.id.home_calendar_pinned_recyclerView);

        mHomeNewsRecyclerView.setNestedScrollingEnabled(false);
        mHomeCalendarEventRecyclerView.setNestedScrollingEnabled(false);
        mHomeCalendarPinnedRecyclerView.setNestedScrollingEnabled(false);

        mHomeNewsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHomeCalendarEventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHomeCalendarPinnedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mHomeNewsAdapter = new NewsRecyclerViewAdapter(null);
        mHomeCalendarEventAdapter = new EventAdapter(null);
        mHomeCalendarPinnedAdapter = new EventAdapter(null);

        mHomeNewsRecyclerView.setAdapter(mHomeNewsAdapter);
        mHomeCalendarEventRecyclerView.setAdapter(mHomeCalendarEventAdapter);
        mHomeCalendarPinnedRecyclerView.setAdapter(mHomeCalendarPinnedAdapter);

        mHomeNewsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        mHomeCalendarEventRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        mHomeCalendarPinnedRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        List<HashMap<String, String>> homePinnedList = new ArrayList<>();

        carouselView = (CarouselView) view.findViewById(R.id.carouselView);
        carouselView.setPageCount(4);
        carouselView.setImageListener(imageListener);

        int homeNewsAmount = PreferenceUtils.getHomeNewsAmount(getContext());
        int homeEventsAmount = PreferenceUtils.getHomeEventsAmount(getContext());
        int homePinnedAmount = PreferenceUtils.getHomePinnedAmount(getContext());

        homeNewsHeader = (LinearLayout) view.findViewById(R.id.home_news_header_layout);
        homeEventsHeader = (LinearLayout) view.findViewById(R.id.home_events_header_layout);
        homePinnedHeader = (LinearLayout) view.findViewById(R.id.home_pinned_header_layout);
        homeNoItemsView = (TextView) view.findViewById(R.id.home_no_items_shown_textview);

        if (homeNewsAmount == 0)
            homeNewsHeader.setVisibility(View.GONE);
        if (homeEventsAmount == 0)
            homeEventsHeader.setVisibility(View.GONE);
        if (homePinnedAmount == 0)
            homePinnedHeader.setVisibility(View.GONE);
        if (homeNewsAmount == 0 && homeEventsAmount == 0 && homePinnedAmount == 0)
            homeNoItemsView.setVisibility(View.VISIBLE);

        newsSeeMoreTextView = (TextView) view.findViewById(R.id.recent_news_see_more_textview);
        upcomingSeeMoreTextView = (TextView) view.findViewById(R.id.home_upcoming_events_see_more_textview);
        pinnedSeeMoreTextView = (TextView) view.findViewById(R.id.home_pinned_events_see_more_textview);
        newsSeeMoreArrow = (ImageView) view.findViewById(R.id.home_recent_news_see_more_arrow);
        upcomingSeeMoreArrow = (ImageView) view.findViewById(R.id.home_upcoming_events_see_more_arrow);
        pinnedSeeMoreArrow = (ImageView) view.findViewById(R.id.home_pinned_events_see_more_arrow);
        mNavigationView = (NavigationView) view.findViewById(R.id.nav_view);

        newsSeeMoreTextView.setOnClickListener(this);
        upcomingSeeMoreTextView.setOnClickListener(this);
        pinnedSeeMoreTextView.setOnClickListener(this);
        newsSeeMoreArrow.setOnClickListener(this);
        upcomingSeeMoreArrow.setOnClickListener(this);
        pinnedSeeMoreArrow.setOnClickListener(this);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        Fragment seeMoreFragment = null;

        switch (v.getId()) {
            case R.id.recent_news_see_more_textview:
                seeMoreFragment = new NewsFragment();
                break;
            case R.id.home_recent_news_see_more_arrow:
                seeMoreFragment = new NewsFragment();
                break;
            case R.id.home_upcoming_events_see_more_textview:
                seeMoreFragment = new CalendarFragment();
                break;
            case R.id.home_upcoming_events_see_more_arrow:
                seeMoreFragment = new CalendarFragment();
                break;
            case R.id.home_pinned_events_see_more_textview:
                seeMoreFragment = new CalendarPinnedFragment();
                break;
            case R.id.home_pinned_events_see_more_arrow:
                seeMoreFragment = new CalendarPinnedFragment();
                break;

        }
        getFragmentManager().beginTransaction()
                .replace(R.id.content_default, seeMoreFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader: Creating loader with id " + id);
        switch (id) {
            case PINNED_EVENTS_LOADER_ID:
                return new CursorLoader(getContext(), PinnedEventsContract.PinnedEventsTable.CONTENT_URI, null, null, null, null);
            default:
                throw new IllegalArgumentException("Unsupported ID!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished");
        Set<String> uids = new HashSet<>();
        while (data.moveToNext()) {
            uids.add(data.getString(1));
        }
        Log.i(TAG, "Data: " + uids);
        setPinnedData(uids);
    }

    private void setPinnedData(Set<String> uids) {
        pinnedUIDs.clear();
        pinnedUIDs.addAll(uids);
        updatePinnedContent();
    }

    private void updatePinnedContent() {
        if (pinnedUIDs.size() == 0)
            homePinnedHeader.setVisibility(View.GONE);
        else if (preferenceValues[PINNED_PREFERENCE_VALUES_INDEX] != 0)
            homePinnedHeader.setVisibility(View.VISIBLE);
        //There is definitely stuff to display now
        List<EventFlexibleItem> items = new ArrayList<>(calendarData);

        Iterators.removeIf(items.iterator(), new Predicate<EventFlexibleItem>() {
            @Override
            public boolean apply(EventFlexibleItem input) {
                return !pinnedUIDs.contains(input.vEvent.getUid().getValue());
            }
        });

        mHomeCalendarPinnedAdapter.updateDataSet(
                new ArrayList<FlexibleHasCalendarDay>(items)
                        .subList(0, Math.min(items.size(), preferenceValues[PINNED_PREFERENCE_VALUES_INDEX])),
                true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Loader reset");
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
