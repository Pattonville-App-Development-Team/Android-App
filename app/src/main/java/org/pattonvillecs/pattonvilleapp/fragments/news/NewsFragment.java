package org.pattonvillecs.pattonvilleapp.fragments.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.HomeFragment;
import org.pattonvillecs.pattonvilleapp.fragments.news.articles.NewsArticle;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        listView = (ListView) root.findViewById(R.id.news_listview);

        List<HashMap<String, String>> newsList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 12; i++) {

            HashMap<String, String> newsListItem = new HashMap<String, String>();
            newsListItem.put("image", Integer.toString(HomeFragment.sampleImages[i]));
            newsListItem.put("headline", HomeFragment.sampleHeadlines[i]);
            newsList.add(newsListItem);
        }

        String[] newsListFrom = {"image", "headline"};
        int[] newsListTo = {R.id.home_news_listview_item_imageView, R.id.home_news_listview_item_textView};

        SimpleAdapter newsListSimpleAdapter = new SimpleAdapter(root.getContext(), newsList,
                R.layout.home_news_listview_item, newsListFrom, newsListTo);
        listView.setAdapter(newsListSimpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("NewsFragment", "Opening NewsDetailActivityNoImage");

                if (i % 2 == 0) {
                    Intent intent = new Intent(getContext(), NewsDetailActivityNoImage.class);
                    intent.putExtra("Position", i);
                    intent.putExtra("NewsArticle", new NewsArticle());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), NewsDetailActivity.class);
                    intent.putExtra("Position", i);
                    intent.putExtra("NewsArticle", new NewsArticle());
                    startActivity(intent);
                }
            }
        });

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
