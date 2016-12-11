package org.pattonvillecs.pattonvilleapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.pattonvillecs.pattonvilleapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsFragment extends Fragment {

    private ListView listView;

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_news);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        listView = (ListView) root.findViewById(R.id.news_listview);

        List<HashMap<String, String>> homeNewsList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 12; i++) {

            HashMap<String, String> newsListItem = new HashMap<String, String>();
            newsListItem.put("image", Integer.toString(HomeFragment.sampleImages[i]));
            newsListItem.put("headline", HomeFragment.sampleHeadlines[i]);
            homeNewsList.add(newsListItem);
        }

        String[] homeNewsListFrom = {"image", "headline"};
        int[] homeNewsListTo = {R.id.home_news_listview_item_imageView, R.id.home_news_listview_item_textView};

        SimpleAdapter newsListSimpleAdapter = new SimpleAdapter(root.getContext(), homeNewsList, R.layout.home_news_listview_item, homeNewsListFrom, homeNewsListTo);
        listView.setAdapter(newsListSimpleAdapter);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
