package org.pattonvillecs.pattonvilleapp.news;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.news.articles.NewsArticle;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Mitchell Skaggs on 2/13/17.
 */
public class NewsParsingUpdateData {
    public static final int NEWS_LISTENER_ID = 412983751;
    private final ConcurrentMap<DataSource, List<NewsArticle>> newsData;
    private final Set<NewsParsingAsyncTask> runningNewsAsyncTasks;

    public NewsParsingUpdateData(ConcurrentMap<DataSource, List<NewsArticle>> newsData, Set<NewsParsingAsyncTask> runningNewsAsyncTasks) {
        this.newsData = newsData;
        this.runningNewsAsyncTasks = runningNewsAsyncTasks;
    }

    public ConcurrentMap<DataSource, List<NewsArticle>> getNewsData() {
        return newsData;
    }

    public Set<NewsParsingAsyncTask> getRunningNewsAsyncTasks() {
        return runningNewsAsyncTasks;
    }
}
