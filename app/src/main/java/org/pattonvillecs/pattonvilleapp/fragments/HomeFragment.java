package org.pattonvillecs.pattonvilleapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.pattonvillecs.pattonvilleapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class HomeFragment extends Fragment {

    public static String[] sampleHeadlines = {"Pattonville App in development",
            "Pattonville Robotics Club wins recent match!",
            "PHS to host basketball celebration on Jan. 20",
            "Students, staff recognized at November board meeting",
            "Pattonville ranked No. 6 in Missouri, tops in U.S. on annual \"best of\" list",
            "Headline Six", "Headline Seven", "Headline Eight", "Headline Nine", "Headline Ten",
            "Headline Eleven", "Headline Twelve"};

    public static int[] sampleImages = {R.drawable.test_news_1, R.drawable.test_news_2,
            R.drawable.test_news_3, R.drawable.test_news_4, R.drawable.test_news_1,
            R.drawable.test_news_2, R.drawable.test_news_3, R.drawable.test_news_4,
            R.drawable.test_news_1, R.drawable.test_news_2, R.drawable.test_news_3,
            R.drawable.test_news_4};

    CarouselView carouselView;
    ListView newsListView;
    ListView eventListView;

    String[] sampleEvents = {"Board Meeting", "Pattonville Official App Release", "Independence Day"};

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };
    private OnFragmentInteractionListener mListener;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        List<HashMap<String, String>> homeNewsList = new ArrayList<>();
        List<HashMap<String, String>> homeEventsList = new ArrayList<>();

        if (getResources().getConfiguration().orientation != ORIENTATION_LANDSCAPE) {
            carouselView = (CarouselView) view.findViewById(R.id.carouselView);
            carouselView.setPageCount(4);
            carouselView.setImageListener(imageListener);

            for (int i = 0; i < 3; i++) {
                HashMap<String, String> newsListItem = new HashMap<>();
                newsListItem.put("image", Integer.toString(sampleImages[i]));
                newsListItem.put("headline", sampleHeadlines[i]);
                homeNewsList.add(newsListItem);
            }
        } else {

            for (int i = 0; i < 12; i++) {
                HashMap<String, String> newsListItem = new HashMap<>();
                newsListItem.put("image", Integer.toString(sampleImages[i]));
                newsListItem.put("headline", sampleHeadlines[i]);
                homeNewsList.add(newsListItem);
            }

        }

        newsListView = (ListView) view.findViewById(R.id.home_news_listview);
        eventListView = (ListView) view.findViewById(R.id.home_upcoming_events_listview);


        for (int i = 0; i < 2; i++) {
            HashMap<String, String> eventListItem = new HashMap<>();
            eventListItem.put("event", sampleEvents[i]);
            homeEventsList.add(eventListItem);
        }

        String[] homeNewsListFrom = {"image", "headline"};
        String[] homeEventListFrom = {"event"};

        int[] homeNewsListTo = {R.id.home_news_listview_item_imageView, R.id.home_news_listview_item_textView};
        int[] homeEventListTo = {R.id.home_upcoming_events_listview_textview};

        SimpleAdapter newsListSimpleAdapter = new SimpleAdapter(view.getContext(), homeNewsList, R.layout.home_news_listview_item, homeNewsListFrom, homeNewsListTo);
        SimpleAdapter eventListSimpleAdapter = new SimpleAdapter(view.getContext(), homeEventsList, R.layout.home_upcoming_events_listview_item, homeEventListFrom, homeEventListTo);

        newsListView.setAdapter(newsListSimpleAdapter);
        eventListView.setAdapter(eventListSimpleAdapter);

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
