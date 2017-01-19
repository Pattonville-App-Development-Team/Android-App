package org.pattonvillecs.pattonvilleapp.fragments.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PreferenceUtils;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.news.articles.NewsArticle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class NewsFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private NewsRecyclerViewAdapter mAdapter;
    private ArrayList<NewsArticle> newsArticles;

    private long time;

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

        mRecyclerView = (RecyclerView) root.findViewById(R.id.news_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        newsArticles = new ArrayList<>();
        updateList();

        return root;
    }

    private void updateUI() {

        Collections.sort(newsArticles, new Comparator<NewsArticle>() {
            @Override
            public int compare(NewsArticle newsArticle, NewsArticle t1) {

                return t1.getPublishDate().compareTo(newsArticle.getPublishDate());
            }
        });

        mAdapter = new NewsRecyclerViewAdapter(newsArticles, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateList() {
        RequestQueue queue = Volley.newRequestQueue(NewsFragment.this.getActivity());

        newsArticles.clear();

        Set<DataSource> selectedSchools = PreferenceUtils.getSelectedSchoolsSet(getContext());
        selectedSchools.add(DataSource.DISTRICT);

        for (final Object school : selectedSchools.toArray()) {

            final String url = "http://fccms.psdr3.org/" + ((DataSource) school).newsName + "/news/?plugin=xml&leaves";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("NewsFragment", "Response Receivied: " + (System.currentTimeMillis() - time));
                            NewsParser parser = new NewsParser(response, ((DataSource) school).calendarColor);
                            parser.getXml();
                            newsArticles.addAll(parser.getItems());
                            updateUI();
                        }

                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("News Fragment", "Error");
                }
            });

            time = System.currentTimeMillis();
            queue.add(stringRequest);
        }

    }
}
