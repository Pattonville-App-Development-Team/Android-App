package org.pattonvillecs.pattonvilleapp.fragments.news;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.news.articles.NewsArticle;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsDetailActivityNoImage extends AppCompatActivity {

    private TextView mTextView;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail_content);

        mWebView = (WebView) findViewById(R.id.newsDetail_webView);
        mWebView.setBackgroundColor(Color.parseColor("#FAFAFA"));

        mTextView = (TextView) findViewById(R.id.newsDetail_dateView);

        NewsArticle newsArticle = getIntent().getParcelableExtra("NewsArticle");

        setTitle(newsArticle.getTitle());

        mTextView.setText((new SimpleDateFormat("h:mm a',' MM/dd/yy", Locale.US)).format(newsArticle.getPublishDate()));

        mWebView.loadData(newsArticle.getContent(), "text/html", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_newsdetail, menu);
        MenuItem item = menu.findItem(R.id.newsDetail_share);
        item.setIntent(Intent.createChooser(new Intent(android.content.Intent.ACTION_SEND).setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, "http://www.psdr3.org"), "Share Link:"));
        return true;
    }
}