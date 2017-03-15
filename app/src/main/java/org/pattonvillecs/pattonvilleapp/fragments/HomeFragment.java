package org.pattonvillecs.pattonvilleapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.google.common.collect.HashMultimap;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.PreferenceUtils;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarFragment;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarPinnedFragment;

import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventAdapter;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventFlexibleItem;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.FlexibleHasCalendarDay;
import org.pattonvillecs.pattonvilleapp.fragments.news.NewsFragment;
import org.pattonvillecs.pattonvilleapp.fragments.news.NewsParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.fragments.news.articles.NewsArticle;
import org.pattonvillecs.pattonvilleapp.fragments.news.articles.NewsRecyclerViewAdapter;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import org.pattonvillecs.pattonvilleapp.news.NewsFragment;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eu.davidea.flexibleadapter.common.DividerItemDecoration;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    public static String[] sampleHeadlines = {"Example Headline 1",
            "Example Headline 2", "Example Headline 3"};

    public static int[] sampleImages = {R.drawable.test_news_1, R.drawable.test_news_2,
            R.drawable.test_news_3, R.drawable.test_news_4, R.drawable.test_news_1,
            R.drawable.test_news_2, R.drawable.test_news_3, R.drawable.test_news_4,
            R.drawable.test_news_1, R.drawable.test_news_2, R.drawable.test_news_3,
            R.drawable.test_news_4};

    public static String[] samplePinnedEvents = {"Pinned Event 1", "Pinned Event 2", "Pinned Event 3"};

    CarouselView carouselView;
    //ListView eventListView;
    ListView pinnedListView;
    TextView newsSeeMoreTextView;
    TextView upcomingSeeMoreTextView;
    TextView pinnedSeeMoreTextView;
    ImageView newsSeeMoreArrow;
    ImageView upcomingSeeMoreArrow;
    ImageView pinnedSeeMoreArrow;
    NavigationView mNavigationView;
    String[] sampleEvents = {"Example Event 1", "Example Event 2", "Example Event 3"};
    RecyclerView mHomeNewsRecyclerView;
    RecyclerView mHomeCalendarRecyclerView;
    NewsRecyclerViewAdapter mHomeNewsAdapter;
    PattonvilleApplication pattonvilleApplication;
    List<NewsArticle> mNewsArticles;
    PauseableListener<CalendarParsingUpdateData> calendarListener;
    EventAdapter mHomeCalendarAdapter;
    ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendarData = new ConcurrentHashMap<>();
    TextView homeNewsLoadingTextView;
    TextView homeCalendarLoadingTextView;

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };
    private PauseableListener<NewsParsingUpdateData> homeListener;
    private OnFragmentInteractionListener mListener;

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
                        .collect((Collectors.<NewsArticle>toList()));

                if (newNewsArticles.size() > 4) {
                    newNewsArticles = newNewsArticles.subList(0, 3);
                }

                Log.i(TAG, "Loaded news articles from " + data.getNewsData().keySet() + " " + newNewsArticles.size());
                mNewsArticles = newNewsArticles;
                mHomeNewsAdapter.updateDataSet(newNewsArticles, true); // Must be an unused list, copy it if needed


                homeNewsLoadingTextView.setVisibility(View.GONE);
                Log.e(TAG, "Removed Loading Text");
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

    public void setCalendarData(ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendarData) {
        this.calendarData = calendarData;
        List<EventFlexibleItem> items = Stream.of(calendarData.entrySet())
                .flatMap(new Function<Map.Entry<DataSource, HashMultimap<CalendarDay, VEvent>>, Stream<Pair<DataSource, VEvent>>>() {
                    @Override
                    public Stream<Pair<DataSource, VEvent>> apply(final Map.Entry<DataSource, HashMultimap<CalendarDay, VEvent>> dataSourceHashMultimapEntry) {
                        return Stream.of(dataSourceHashMultimapEntry.getValue().entries()).map(new Function<Map.Entry<CalendarDay, VEvent>, Pair<DataSource, VEvent>>() {
                            @Override
                            public Pair<DataSource, VEvent> apply(Map.Entry<CalendarDay, VEvent> calendarDayVEventEntry) {
                                return new ImmutablePair<>(dataSourceHashMultimapEntry.getKey(), calendarDayVEventEntry.getValue());
                            }
                        });
                    }
                })
                .sorted(new Comparator<Pair<DataSource, VEvent>>() {
                    @Override
                    public int compare(Pair<DataSource, VEvent> o1, Pair<DataSource, VEvent> o2) {
                        //This is Google Calendar style scrolling: future events to the bottom
                        return o1.getValue().getStartDate().getDate().compareTo(o2.getValue().getStartDate().getDate());
                    }
                })
                .map(new Function<Pair<DataSource, VEvent>, EventFlexibleItem>() {
                    @Override
                    public EventFlexibleItem apply(Pair<DataSource, VEvent> dataSourceVEventPair) {
                        return new EventFlexibleItem(dataSourceVEventPair.getKey(), dataSourceVEventPair.getValue());
                    }
                })
                .collect(Collectors.<EventFlexibleItem>toList());

        if (items.size() > 4) {
            items = items.subList(0, 3);
        }

        mHomeCalendarAdapter.updateDataSet(new ArrayList<FlexibleHasCalendarDay>(items), true);


        homeCalendarLoadingTextView.setVisibility(View.GONE);
        Log.e(TAG, "Removed Calendar Loading ");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_home);
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
        mHomeCalendarRecyclerView = (RecyclerView) view.findViewById(R.id.home_calendar_recyclerView);
        homeNewsLoadingTextView = (TextView) view.findViewById(R.id.home_news_loading_textview);
        homeCalendarLoadingTextView = (TextView) view.findViewById(R.id.home_calendar_loading_textview);
        mHomeNewsRecyclerView.setNestedScrollingEnabled(false);
        mHomeCalendarRecyclerView.setNestedScrollingEnabled(false);
        mHomeNewsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mHomeCalendarRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mHomeNewsAdapter = new NewsRecyclerViewAdapter(null);
        mHomeCalendarAdapter = new EventAdapter(null);
        mHomeNewsRecyclerView.setAdapter(mHomeNewsAdapter);
        mHomeCalendarRecyclerView.setAdapter(mHomeCalendarAdapter);
        mHomeNewsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        List<HashMap<String, String>> homeEventsList = new ArrayList<>();
        List<HashMap<String, String>> homePinnedList = new ArrayList<>();

        carouselView = (CarouselView) view.findViewById(R.id.carouselView);
        carouselView.setPageCount(4);
        carouselView.setImageListener(imageListener);

        int homeNewsAmount = PreferenceUtils.getHomeNewsAmount(getContext());
        int homeEventsAmount = PreferenceUtils.getHomeEventsAmount(getContext());
        int homePinnedAmount = PreferenceUtils.getHomePinnedAmount(getContext());

        if (homeNewsAmount == 0) {
            (view.findViewById(R.id.home_news_header_layout)).setVisibility(View.GONE);
        }
        if (homeEventsAmount == 0) {
            (view.findViewById(R.id.home_events_header_layout)).setVisibility(View.GONE);
        }
        if (homePinnedAmount == 0) {
            (view.findViewById(R.id.home_pinned_header_layout)).setVisibility(View.GONE);
        }
        if (homeNewsAmount == 0 && homeEventsAmount == 0 && homePinnedAmount == 0) {
            (view.findViewById(R.id.home_no_items_shown_textview)).setVisibility(View.VISIBLE);
        }


        for (int i = 0; i < homeEventsAmount; i++) {
            HashMap<String, String> eventListItem = new HashMap<>();
            eventListItem.put("event", sampleEvents[i % 3]);
            homeEventsList.add(eventListItem);
        }

        for (int i = 0; i < homePinnedAmount; i++) {

            HashMap<String, String> pinnedListItem = new HashMap<>();
            pinnedListItem.put("pin", samplePinnedEvents[i % 3]);
            homePinnedList.add(pinnedListItem);
        }

        //eventListView = (ListView) view.findViewById(R.id.home_upcoming_events_listview);
        pinnedListView = (ListView) view.findViewById(R.id.home_pinned_events_listview);
        newsSeeMoreTextView = (TextView) view.findViewById(R.id.recent_news_see_more_textview);
        upcomingSeeMoreTextView = (TextView) view.findViewById(R.id.home_upcoming_events_see_more_textview);
        pinnedSeeMoreTextView = (TextView) view.findViewById(R.id.home_pinned_events_see_more_textview);
        newsSeeMoreArrow = (ImageView) view.findViewById(R.id.home_recent_news_see_more_arrow);
        upcomingSeeMoreArrow = (ImageView) view.findViewById(R.id.home_upcoming_events_see_more_arrow);
        pinnedSeeMoreArrow = (ImageView) view.findViewById(R.id.home_pinned_events_see_more_arrow);
        mNavigationView = (NavigationView) view.findViewById(R.id.nav_view);

       /* ViewGroup.LayoutParams eventParam = eventListView.getLayoutParams();
        eventParam.height = 136 * homeEventsAmount;
        eventListView.setLayoutParams(eventParam);
        eventListView.requestLayout();
        */

        ViewGroup.LayoutParams pinnedParam = pinnedListView.getLayoutParams();
        pinnedParam.height = 136 * homePinnedAmount;
        pinnedListView.setLayoutParams(pinnedParam);
        pinnedListView.requestLayout();


        String[] homeEventListFrom = {"event"};
        String[] homePinnedListFrom = {"pin"};

        int[] homeEventListTo = {R.id.home_upcoming_events_listview_textview};
        int[] homePinnedEventTo = {R.id.home_pinned_events_listview_textview};

        SimpleAdapter eventListSimpleAdapter = new SimpleAdapter(view.getContext(), homeEventsList, R.layout.home_upcoming_events_listview_item, homeEventListFrom, homeEventListTo);
        SimpleAdapter pinnedListSimpleAdapter = new SimpleAdapter(view.getContext(), homePinnedList, R.layout.home_pinned_events_listview_item, homePinnedListFrom, homePinnedEventTo);

        //eventListView.setAdapter(eventListSimpleAdapter);
        pinnedListView.setAdapter(pinnedListSimpleAdapter);


        newsSeeMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_default, NewsFragment.newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

            }


        });
        upcomingSeeMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_default, CalendarFragment.newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });
        pinnedSeeMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_default, CalendarPinnedFragment.newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        newsSeeMoreArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_default, NewsFragment.newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();


            }
        });
        upcomingSeeMoreArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_default, CalendarFragment.newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });
        pinnedSeeMoreArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_default, CalendarPinnedFragment.newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
