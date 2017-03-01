package org.pattonvillecs.pattonvilleapp.news.articles;

import android.support.annotation.Nullable;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class NewsRecyclerViewAdapter extends FlexibleAdapter<NewsArticle> {

    public NewsRecyclerViewAdapter(@Nullable List<NewsArticle> items) {
        super(items);
    }

    public NewsRecyclerViewAdapter(@Nullable List<NewsArticle> items, @Nullable Object listeners) {
        super(items, listeners);
    }

    public NewsRecyclerViewAdapter(@Nullable List<NewsArticle> items, @Nullable Object listeners, boolean stableIds) {
        super(items, listeners, stableIds);
    }
}
