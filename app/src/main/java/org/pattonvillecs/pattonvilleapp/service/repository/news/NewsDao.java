/*
 * Copyright (C) 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.service.model.news.ArticleSummary;
import org.pattonvillecs.pattonvilleapp.service.model.news.DataSourceMarker;
import org.pattonvillecs.pattonvilleapp.service.model.news.SourcedArticleSummary;

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
    private static final String WHERE_DATASOURCE_MARKER_EXISTS = "(EXISTS (SELECT * FROM news_datasource_markers WHERE news_datasource_markers.guid = news_articles.guid AND news_datasource_markers.datasource IN (:dataSources) LIMIT 1))";
    private static final String ORDER_BY_DEFAULT = "news_articles.pub_date ASC";

    @Transaction
    @Query("SELECT " + SELECT_NEWS_ARTICLES
            + " FROM news_articles"
            + " WHERE " + WHERE_DATASOURCE_MARKER_EXISTS
            + " ORDER BY " + ORDER_BY_DEFAULT)
    abstract LiveData<List<SourcedArticleSummary>> getArticlesByDataSources(@NonNull List<DataSource> dataSources);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insertAllIgnore(@NonNull List<ArticleSummary> articleSummaries, @NonNull List<DataSourceMarker> dataSourceMarkers);

    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract void updateAllIgnore(@NonNull List<ArticleSummary> articleSummaries, @NonNull List<DataSourceMarker> dataSourceMarkers);

    /**
     * Needed because {@link OnConflictStrategy#REPLACE} deletes first, which causes pinned events to be cascade deleted.
     *
     * @see <a href="https://en.wiktionary.org/wiki/upsert">Upsert</a>
     */
    @Transaction
    void upsertAll(@NonNull List<ArticleSummary> articleSummaries, @NonNull List<DataSourceMarker> dataSourceMarkers) {
        updateAllIgnore(articleSummaries, dataSourceMarkers);
        insertAllIgnore(articleSummaries, dataSourceMarkers);
    }
}
