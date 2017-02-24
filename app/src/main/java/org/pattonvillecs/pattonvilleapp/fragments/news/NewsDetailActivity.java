package org.pattonvillecs.pattonvilleapp.fragments.news;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.news.articles.NewsArticle;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView mTextView;
    private WebView mWebView;

    private NewsArticle newsArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = (WebView) findViewById(R.id.news_detail_webview);
        mWebView.setBackgroundColor(Color.parseColor("#FAFAFA"));
        mWebView.setHorizontalScrollBarEnabled(false);

        mTextView = (TextView) findViewById(R.id.newsDetail_toolbar_date);

        newsArticle = getIntent().getParcelableExtra("NewsArticle");

        setTitle("News");

        ((TextView) findViewById(R.id.newsDetail_toolbar_title)).setText(newsArticle.getTitle());

        mTextView.setText(newsArticle.getFormattedDate());

        RequestQueue queue = Volley.newRequestQueue(this);

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
                mTextView.setText("That didn't work!");
            }
        });
        queue.add(stringRequest);
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