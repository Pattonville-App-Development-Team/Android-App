package org.pattonvillecs.pattonvilleapp.fragments.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity().getApplicationContext()
        ));
    }

    private void updateList() {
        RequestQueue queue = Volley.newRequestQueue(NewsFragment.this.getActivity());

        newsArticles.clear();

        final Set<DataSource> selectedSchools = PreferenceUtils.getSelectedSchoolsSet(getContext());

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
            ImageView sourceView;
            TextView schoolIDText;
            TextView newsDateText;

            DateFormat df = new SimpleDateFormat("MM/dd/yy");
            String todayDate = df.format(Calendar.getInstance().getTime());


            NewsArticleViewHolder(View view) {
                super(view);

                titleView = (TextView) itemView.findViewById(R.id.home_news_listview_item_textView);
                sourceView = (ImageView) itemView.findViewById(R.id.news_front_imageview);
                schoolIDText = (TextView) itemView.findViewById(R.id.news_circle_school_id);
                newsDateText = (TextView) itemView.findViewById(R.id.news_list_article_date_textview);

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
                switch (item.getSourceColor()) {

                    case -16745933:
                        schoolIDText.setText(R.string.DistrictInitial);
                        break;

                    case -16744320:
                        schoolIDText.setText(R.string.HighSchoolInitial);
                        break;

                    case -65536:
                        schoolIDText.setText(R.string.HeightsInitial);
                        break;

                    case -29696:
                        schoolIDText.setText(R.string.HolmanInitial);
                        break;

                    case -4419697:
                        schoolIDText.setText(R.string.ParkwoodInitial);
                        break;

                    case -11861886:
                        schoolIDText.setText(R.string.DrummondInitial);
                        break;

                    case -1146130:
                        schoolIDText.setText(R.string.BridgewayInitial);
                        break;

                    case -10496:
                        schoolIDText.setText(R.string.RemmingtonInitial);
                        break;

                    case -8355840:
                        schoolIDText.setText(R.string.WillowBrookInitial);
                        break;

                    case -16777011:
                        schoolIDText.setText(R.string.RoseAcresInitial);
                        break;


                }
                sourceView.setColorFilter(item.getSourceColor());
                //Log.e("News Item Title + Color", item.getTitle().substring(0,5) + " " + item.getSourceColor());

                String articleDate = (new SimpleDateFormat("MM/dd/yy", Locale.US)).format(item.getPublishDate());

                if (todayDate.equals(articleDate)) {

                    newsDateText.setText("Today");

                } else {
                    newsDateText.setText(articleDate);
                }
                mArticle = item;
            }
        }
    }

    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.news_recycler_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}

