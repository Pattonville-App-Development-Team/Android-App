/*
 * Copyright (C) 2017 - 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.service.repository.news;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import org.pattonvillecs.pattonvilleapp.service.model.DataSource;
import org.pattonvillecs.pattonvilleapp.service.model.news.ArticleSummary;

import java.util.List;

/**
 * Created by Mitchell Skaggs on 1/15/2018.
 *
 * @since 1.4.0
 */

@SuppressWarnings("NullableProblems")
//Due to nullable annotations not being overridden in "_Impl" classes
@Dao
public abstract class NewsDao {

    private static final String SELECT_NEWS_ARTICLES = "news_articles.*";
    private static final String WHERE_DATASOURCE_MARKER_MATCHES = "news_articles.datasource IN (:dataSources)";
    private static final String ORDER_BY_DEFAULT = "news_articles.pub_date DESC";

    @Transaction
    @Query("SELECT " + SELECT_NEWS_ARTICLES
            + " FROM news_articles"
            + " WHERE " + WHERE_DATASOURCE_MARKER_MATCHES
            + " ORDER BY " + ORDER_BY_DEFAULT)
    abstract LiveData<List<ArticleSummary>> getArticlesByDataSources(@NonNull List<DataSource> dataSources);

    @Transaction
    @Query("SELECT " + SELECT_NEWS_ARTICLES
            + " FROM news_articles"
            + " WHERE " + WHERE_DATASOURCE_MARKER_MATCHES
            + " ORDER BY " + ORDER_BY_DEFAULT
            + " LIMIT (:limit)")
    abstract LiveData<List<ArticleSummary>> getArticlesByDataSourcesWithLimit(@NonNull List<DataSource> dataSources, int limit);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insertAllIgnore(@NonNull List<ArticleSummary> articleSummaries);

    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract void updateAllIgnore(@NonNull List<ArticleSummary> articleSummaries);

    /**
     * Needed because {@link OnConflictStrategy#REPLACE} deletes first.
     *
     * @see <a href="https://en.wiktionary.org/wiki/upsert">Upsert</a>
     */
    @Transaction
    void upsertAll(@NonNull List<ArticleSummary> articleSummaries) {
        updateAllIgnore(articleSummaries);
        insertAllIgnore(articleSummaries);
    }
}
