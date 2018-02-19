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

package org.pattonvillecs.pattonvilleapp.news.articles;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.news.NewsDetailActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * Data object to handle the contents of an article. Implements IFilterable for Flexible Adapter
 * sorting, implements Parcelable to make objects passable between activities, notably between
 * NewsFragment and NewsDetailActivity. Any changes what is parsed for a NewsArticle must be
 * reflected within the Parcelable methods as well as most likely an update for filter().
 *
 * @author Nathan Skelton
 * @author Mitchell Skaggs
 * @author Jeremiah Simmons
 */
public class NewsArticle extends AbstractFlexibleItem<NewsArticle.NewsArticleViewHolder> implements Parcelable, IFilterable<String> {

    //Required CREATOR for the Parcelable implementation
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public NewsArticle createFromParcel(Parcel in) {
            return new NewsArticle(in);
        }

        @Override
        public NewsArticle[] newArray(int size) {
            return new NewsArticle[size];
        }
    };

    // Date formatting strings
    // If you wish to change the style of date display within list elements and the DetailActivity,
    // change these date codes
    private final static DateFormat shortDF = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    private final static DateFormat longDF = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);

    private static final String TAG = NewsArticle.class.getSimpleName();

    private static String todayDate = shortDF.format(Calendar.getInstance().getTime());

    private Date publishDate;
    private String title;
    private String publicUrl, privateUrl;
    private DataSource dataSource;

    public NewsArticle() {

        // Default content for an article. When parsed, it should be corrected
        publishDate = new Date(1484357778);
        title = "Title";
        publicUrl = "www.psdr3.org";
        privateUrl = "fccms.psdr3.org";
    }

    private NewsArticle(Parcel parcel) {

        String[] strings = parcel.createStringArray();
        title = strings[0];
        publicUrl = strings[1];
        privateUrl = strings[2];
        publishDate = new Date(parcel.readLong());
    }

    /**
     * Method to handle the formatting of the news article's body. In here Jsoup is used to remove
     * the web article header, as well as regex overrides for "-Read-More-" and "-End-" tags, and an
     * override to adjust text size per the users currently set text size.
     *
     * @param html Unformatted HTML String, usually straight from the parser or Volley's cache
     * @return Formatted String, ready to be placed within NewsDetailActivity's WebView, or other
     */
    public static String formatContent(String html) {

        Document resultD = Jsoup.parse(html);

        resultD.outputSettings().charset("ASCII");
        resultD.outputSettings().escapeMode(Entities.EscapeMode.extended);
        resultD.outputSettings().prettyPrint(false);

        // Select only the content, removing the web header
        String result = resultD.getElementsByTag("table").last()
                .getElementsByTag("tr").get(1)
                .getElementsByTag("td").get(1)
                .html();

        // Removing the -End- and -Read-More- tags created by fccms.psdr3.org
        result = result.replaceFirst("<div.+-End-.+<\\/div>", "");
        result = result.replaceFirst("<div.+-Read-More-.+<\\/div>", "");

        // Overriding the text size. Hard coded "15" can be changed as the scalar quantity.
        int fontScale = (int) (15 * Resources.getSystem().getConfiguration().fontScale);
        result = result.replaceAll("font-size:\\d+pt;", "font-size:" + fontScale + "px;");

        // Add an extra line to the HTML to make the content pad well at the bottom of the WebView
        result = result.concat("<br>");

        return result;
    }

    /**
     * Overridden equals method to handle object comparison. Because all news articles have a unique
     * url, this simply uses a url comparison to determine equality
     *
     * @param o Other object to be compared
     * @return boolean which returns true if objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsArticle that = (NewsArticle) o;
        return privateUrl.equals(that.privateUrl);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (publicUrl != null ? publicUrl.hashCode() : 0);
        result = 31 * result + (privateUrl != null ? privateUrl.hashCode() : 0);
        result = 31 * result + (dataSource != null ? dataSource.hashCode() : 0);
        return result;
    }

    public String getPrivateUrl() {
        return privateUrl;
    }

    public void setPrivateUrl(String privateUrl) {
        this.privateUrl = privateUrl;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String url) {
        this.publicUrl = url;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.home_news_listview_item;
    }

    /**
     * Method which provides the formatted date String according to today's date.
     *
     * @return String like "Tuesday, February 28, 2017" is given
     */
    public String getFormattedDate() {
        String articleDate = shortDF.format(getPublishDate());

        if (todayDate.equals(articleDate)) {

            return "Today, " + articleDate;

        } else {
            return longDF.format(getPublishDate());
        }
    }

    @Override
    public NewsArticleViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new NewsArticleViewHolder(view, adapter);
    }

    /**
     * Overridden method from AbstractFlexibleItem<NewsArticle.NewsArticleViewHolder>
     * If the item is modified, this method must reflect that change.
     * Secondly, the onClickListener for a NewsArticle element is set here
     */
    @Override
    public void bindViewHolder(FlexibleAdapter adapter, NewsArticleViewHolder holder, int position, List payloads) {
        holder.titleView.setText(getTitle());
        holder.schoolIDText.setText(getDataSource().initialsName);
        holder.sourceView.setColorFilter(getDataSource().calendarColor);
        holder.newsDateText.setText(getFormattedDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Opening NewsDetailActivity");

                Intent intent = new Intent(v.getContext(), NewsDetailActivity.class);
                intent.putExtra(NewsDetailActivity.KEY_NEWS_ARTICLE, NewsArticle.this);
                v.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        String[] strings = new String[]{title, publicUrl, privateUrl};

        parcel.writeStringArray(strings);
        parcel.writeLong(publishDate.getTime());
    }

    /**
     * This method returns a boolean to determine if this news article contains the current search.
     * When a search is executed, filler(String) is used to check if that specific NewsArticle
     * applies to the current search key.
     *
     * @param constraint Current search keyword/phrase
     * @return boolean which informs whether the article should be within the search
     */
    @Override
    public boolean filter(String constraint) {
        constraint = constraint.toLowerCase();

        int titleRatio = FuzzySearch.partialRatio(constraint, title.toLowerCase());
        int dataSourceRatio = FuzzySearch.partialRatio(constraint, dataSource.name.toLowerCase());
        int dateRatio = FuzzySearch.partialRatio(constraint, getFormattedDate().toLowerCase());

        return titleRatio > 80 || dataSourceRatio > 80 || dateRatio > 80;
    }

    /**
     * Class to handle the list item's views. Changes within the list item must be reflected here.
     */
    public static class NewsArticleViewHolder extends RecyclerView.ViewHolder {

        private TextView titleView;
        private ImageView sourceView;
        private TextView schoolIDText;
        private TextView newsDateText;

        public NewsArticleViewHolder(final View view, FlexibleAdapter adapter) {
            super(view);

            titleView = (TextView) itemView.findViewById(R.id.home_news_listview_item_textView);
            sourceView = (ImageView) itemView.findViewById(R.id.news_front_imageview);
            schoolIDText = (TextView) itemView.findViewById(R.id.news_circle_school_id);
            newsDateText = (TextView) itemView.findViewById(R.id.news_list_article_date_textview);

        }
    }
}
