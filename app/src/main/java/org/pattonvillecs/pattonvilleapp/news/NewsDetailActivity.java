/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.news.articles.NewsArticle;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Activity for displaying a news article's content
 *
 * @author Nathan Skelton
 */
public class NewsDetailActivity extends AppCompatActivity {

    public static final String KEY_NEWS_ARTICLE = "news_article";
    private static final String TAG = NewsDetailActivity.class.getSimpleName();
    private static final String CONTENT_FORMATTING_STRING = "<style>img{display: inline;height: auto;max-width: 100%;}</style>";
    private static final String DEFAULT_MIME_TYPE = "text/html; charset=utf-8";
    private static final String DEFAULT_ENCODING = "utf-8";
    private WebView mWebView;
    private SwipeRefreshLayout mRefreshLayout;

    private NewsArticle newsArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Avoid null pointer warning with null check
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.news_detail_refresh_layout);

        // Defines the colors used for the refresh icon
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mRefreshLayout.setRefreshing(true);

        mWebView = (WebView) findViewById(R.id.news_detail_webview);

        // Defines the background color to match the app's background
        mWebView.setBackgroundColor(Color.parseColor("#FAFAFA"));
        mWebView.setHorizontalScrollBarEnabled(false);

        // Add listener to close the refresh layout when content loaded
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mRefreshLayout.setRefreshing(false);
                mRefreshLayout.setEnabled(false);
            }
        });

        newsArticle = getIntent().getParcelableExtra(KEY_NEWS_ARTICLE);

        setTitle("News");

        // Set content for title and date
        ((TextView) findViewById(R.id.newsDetail_toolbar_title)).setText(newsArticle.getTitle());
        ((TextView) findViewById(R.id.newsDetail_toolbar_date)).setText(newsArticle.getFormattedDate());

        RequestQueue queue = PattonvilleApplication.get(this).getRequestQueue();

        // Article content request using Volley
        CustomStringRequest stringRequest = new CustomStringRequest(Request.Method.GET, newsArticle.getPrivateUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // When content found, load
                        response = new String(response.getBytes(Charset.defaultCharset()), Charset.defaultCharset());
                        mWebView.loadDataWithBaseURL(null, CONTENT_FORMATTING_STRING + NewsArticle.formatContent(response), DEFAULT_MIME_TYPE, DEFAULT_ENCODING, null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // When unable to get content, inform the user
                        Toast.makeText(getApplicationContext(), "Unable to download news article content", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.setRefreshing(false);
                        mRefreshLayout.setEnabled(false);
                    }
                });

        // If the cache has the link's content save, parse and display
        // Otherwise, pull the data
        Cache.Entry cacheEntry = queue.getCache().get(stringRequest.getCacheKey());
        if (cacheEntry != null && cacheEntry.data != null) {
            Log.d(TAG, "Using cached article data");
            String cachedData;
            try {
                cachedData = new String(cacheEntry.data, DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException e) {
                cachedData = new String(cacheEntry.data, Charset.defaultCharset());
            }
            mWebView.loadDataWithBaseURL(null, CONTENT_FORMATTING_STRING + NewsArticle.formatContent(cachedData), DEFAULT_MIME_TYPE, DEFAULT_ENCODING, null);
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

                // Creation of a Share Intent, as shown by android developer guides
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, newsArticle.getPublicUrl());
                startActivity(Intent.createChooser(sharingIntent, "Share Article:"));
                break;

            case android.R.id.home:

                // If back pressed, finish activity
                finish();
                break;
        }
        return true;
    }

    private static final class CustomStringRequest extends StringRequest {
        public CustomStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        public CustomStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            String parsed;
            try {
                parsed = new String(response.data, DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data, Charset.defaultCharset());
            }
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        }
    }
}