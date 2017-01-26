package org.pattonvillecs.pattonvilleapp.fragments.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.List;
import java.util.Set;

public class NewsFragment extends Fragment {

    private static int x = 0;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
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

        newsArticles = new ArrayList<>();

        mRecyclerView = (RecyclerView) root.findViewById(R.id.news_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.news_refreshLayout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
            }
        });

        mRefreshLayout.setRefreshing(true);
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

        mAdapter = new NewsRecyclerViewAdapter(newsArticles);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateList() {
        RequestQueue queue = Volley.newRequestQueue(NewsFragment.this.getActivity());

        newsArticles.clear();

        final Set<DataSource> selectedSchools = PreferenceUtils.getSelectedSchoolsSet(getContext());
        selectedSchools.add(DataSource.DISTRICT);

        for (final Object school : selectedSchools.toArray()) {

            final String url = "http://fccms.psdr3.org/" + ((DataSource) school).newsName + "/news/?plugin=xml&leaves";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("NewsFragment", "Response Receivied: " + (System.currentTimeMillis() - time));
                            NewsParser parser = new NewsParser(response, (DataSource) school);
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

        mRefreshLayout.setRefreshing(false);

    }

    class NewsRecyclerViewAdapter extends RecyclerView.Adapter<org.pattonvillecs.pattonvilleapp.fragments.news.NewsFragment.NewsRecyclerViewAdapter.NewsArticleViewHolder> {

        private ArrayList<NewsArticle> newsArticles;

        NewsRecyclerViewAdapter(ArrayList<NewsArticle> NewsArticles) {
            this.newsArticles = NewsArticles;
        }

        @Override
        public org.pattonvillecs.pattonvilleapp.fragments.news.NewsFragment.NewsRecyclerViewAdapter.NewsArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_news_listview_item, parent, false);
            return new org.pattonvillecs.pattonvilleapp.fragments.news.NewsFragment.NewsRecyclerViewAdapter.NewsArticleViewHolder(v);
        }

        @Override
        public void onBindViewHolder(org.pattonvillecs.pattonvilleapp.fragments.news.NewsFragment.NewsRecyclerViewAdapter.NewsArticleViewHolder holder, int position) {

            NewsArticle item = newsArticles.get(position);
            holder.bind(item);
        }

        @Override
        public void onBindViewHolder(org.pattonvillecs.pattonvilleapp.fragments.news.NewsFragment.NewsRecyclerViewAdapter.NewsArticleViewHolder holder, int position, List<Object> payloads) {

            NewsArticle item = newsArticles.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return newsArticles.size();
        }

        class NewsArticleViewHolder extends RecyclerView.ViewHolder {

            NewsArticle mArticle;
            TextView titleView;
            ImageView imageView, sourceView;

            NewsArticleViewHolder(View view) {
                super(view);

                titleView = (TextView) itemView.findViewById(R.id.home_news_listview_item_textView);
                imageView = (ImageView) itemView.findViewById(R.id.home_news_listview_item_imageView);
                sourceView = (ImageView) itemView.findViewById(R.id.home_news_listview_item_color);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("NewsFragment", "Opening NewsDetailActivity");

                        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
                        intent.putExtra("NewsArticle", mArticle);
                        startActivity(intent);

                    }
                });
            }

            void bind(NewsArticle item) {
                titleView.setText(item.getTitle());
                imageView.setImageResource(R.drawable.test_news_1);
                sourceView.setBackgroundColor(item.getSourceColor());
                mArticle = item;
            }
        }
    }
}
