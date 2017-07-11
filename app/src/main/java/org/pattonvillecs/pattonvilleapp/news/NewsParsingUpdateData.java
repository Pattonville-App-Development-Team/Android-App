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

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.news.articles.NewsArticle;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

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
