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

    String[] sampleHeadlines = {"Student named _____ of the Year", "Pattonville Robotics Club wins Super Internationals", "Third Headline"};

    int[] sampleImages = {R.drawable.test_news_1, R.drawable.test_news_2, R.drawable.test_news_3, R.drawable.test_news_4};


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

        List<HashMap<String, String>> homeNewsList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 3; i++) {

            HashMap<String, String> newsListItem = new HashMap<String, String>();
            newsListItem.put("image", Integer.toString(sampleImages[i]));
            newsListItem.put("headline", sampleHeadlines[i]);
            homeNewsList.add(newsListItem);
        }

        String[] from = {"image", "headline"};

        int[] to = {R.id.home_news_listview_item_imageView, R.id.home_news_listview_item_textView};

        SimpleAdapter adapter = new SimpleAdapter(view.getContext(), homeNewsList, R.layout.home_news_listview_item, from, to);

        newsListView.setAdapter(adapter);

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
