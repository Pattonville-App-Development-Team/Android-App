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

public class HomeFragment extends Fragment {

    CarouselView carouselView;

    ListView newsListView;

    ListView eventListView;

    String[] sampleHeadlines = {"Student named _____ of the Year", "Pattonville Robotics Club wins Super Universes", "Test Headline 3"};

    int[] sampleImages = {R.drawable.test_news_1, R.drawable.test_news_2, R.drawable.test_news_3, R.drawable.test_news_4};

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

        carouselView = (CarouselView) view.findViewById(R.id.carouselView);
        carouselView.setPageCount(4);
        carouselView.setImageListener(imageListener);


        newsListView = (ListView) view.findViewById(R.id.home_news_listview);

        eventListView = (ListView) view.findViewById(R.id.home_upcoming_events_listview);

        List<HashMap<String, String>> homeNewsList = new ArrayList<HashMap<String, String>>();

        List<HashMap<String, String>> homeEventsList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 3; i++) {

            HashMap<String, String> newsListItem = new HashMap<String, String>();
            newsListItem.put("image", Integer.toString(sampleImages[i]));
            newsListItem.put("headline", sampleHeadlines[i]);
            homeNewsList.add(newsListItem);
        }

        for (int i = 0; i < 2; i++) {

            HashMap<String, String> eventListItem = new HashMap<String, String>();
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

        newsListView.setOnItemClickListener((parent, view1, position, id) -> {


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
