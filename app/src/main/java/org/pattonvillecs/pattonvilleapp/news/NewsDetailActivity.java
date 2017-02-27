package org.pattonvillecs.pattonvilleapp.news;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.news.articles.NewsArticle;

public class NewsDetailActivity extends AppCompatActivity {

    private static final String TAG = NewsDetailActivity.class.getSimpleName();

    private WebView mWebView;
    private SwipeRefreshLayout mRefreshLayout;

    private NewsArticle newsArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.news_detail_refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mRefreshLayout.setRefreshing(true);

        mWebView = (WebView) findViewById(R.id.news_detail_webview);
        mWebView.setBackgroundColor(Color.parseColor("#FAFAFA"));
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                mRefreshLayout.setRefreshing(false);
                mRefreshLayout.setEnabled(false);
            }
        });

        newsArticle = getIntent().getParcelableExtra("NewsArticle");

        setTitle("News");

        ((TextView) findViewById(R.id.newsDetail_toolbar_title)).setText(newsArticle.getTitle());
        ((TextView) findViewById(R.id.newsDetail_toolbar_date)).setText(newsArticle.getFormattedDate());

        RequestQueue queue = PattonvilleApplication.get(this).getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, newsArticle.getPrivateUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        mWebView.loadData("<style>img{display: inline;height: auto;max-width: 100%;}</style>" +
                                NewsArticle.formatContent(response), "text/html", null);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Unable to load content", Toast.LENGTH_SHORT).show();
                mRefreshLayout.setRefreshing(false);
                mRefreshLayout.setEnabled(false);
            }
        });

        if (queue.getCache().get(stringRequest.getCacheKey()) != null
                && queue.getCache().get(stringRequest.getCacheKey()).data != null) {
            Log.e(TAG, "Using Cache");
            mWebView.loadData("<style>img{display: inline;height: auto;max-width: 100%;}</style>" +
                    NewsArticle.formatContent(new String(queue.getCache().get(stringRequest.getCacheKey()).data)), "text/html", null);
        } else {
            queue.add(stringRequest);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_newsdetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.newsDetail_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, newsArticle.getPublicUrl());
                startActivity(Intent.createChooser(sharingIntent, "Share Article:"));
                break;

            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
}