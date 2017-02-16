package org.pattonvillecs.pattonvilleapp.fragments.news.articles;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.news.NewsDetailActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

public class NewsArticle extends AbstractFlexibleItem<NewsArticle.NewsArticleViewHolder> implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NewsArticle createFromParcel(Parcel in) {
            return new NewsArticle(in);
        }

        public NewsArticle[] newArray(int size) {
            return new NewsArticle[size];
        }
    };

    private final static DateFormat shortDF = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    private final static DateFormat longDF = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
    private static final String TAG = NewsArticle.class.getSimpleName();

    private static String todayDate = shortDF.format(Calendar.getInstance().getTime());

    private Date publishDate;
    private String title;
    private String content;
    private String publicUrl, privateUrl;
    private DataSource dataSource;

    public NewsArticle() {

        publishDate = new Date(1484357778);
        title = "Some Title";
        publicUrl = "www.psdr3.org";
        privateUrl = "fccms.psdr3.org";

    }

    public NewsArticle(Parcel parcel) {

        String[] strings = parcel.createStringArray();
        title = strings[0];
        content = strings[1];
        publicUrl = strings[2];
        privateUrl = strings[3];
        publishDate = new Date(parcel.readLong());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsArticle that = (NewsArticle) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (publicUrl != null ? !publicUrl.equals(that.publicUrl) : that.publicUrl != null)
            return false;
        if (privateUrl != null ? !privateUrl.equals(that.privateUrl) : that.privateUrl != null)
            return false;
        return dataSource == that.dataSource;

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

    public String getContent() {
        return "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void loadContent(WebView webView) {
        new NewsContentAsyncTask(webView).execute(privateUrl);
    }


    @Override
    public int getLayoutRes() {
        return R.layout.home_news_listview_item;
    }

    public String getFormattedDate() {
        String articleDate = shortDF.format(getPublishDate());

        if (todayDate.equals(articleDate)) {

            return "Today, " + articleDate;

        } else {
            return longDF.format(getPublishDate());
        }
    }

    @Override
    public NewsArticleViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new NewsArticleViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

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
                intent.putExtra("NewsArticle", NewsArticle.this);
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
        String[] strings = new String[]{title, content, publicUrl, privateUrl};

        parcel.writeStringArray(strings);
        parcel.writeLong(publishDate.getTime());
    }

    public static class NewsContentAsyncTask extends AsyncTask<String, Void, String> {

        private final WebView webView;

        public NewsContentAsyncTask(WebView webView) {
            this.webView = webView;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection resultC = Jsoup.connect(strings[0]);
                Log.i("News Parsing", "JSoup Connected");


                Document resultD = resultC.get();

                resultD.outputSettings().charset("ASCII");
                resultD.outputSettings().escapeMode(Entities.EscapeMode.extended);
                resultD.outputSettings().prettyPrint(false);

                Log.i("News Parsing", "Got Document");

                String result = resultD//.getElementsByTag("article").last()
                        .getElementsByTag("table").last()
                        //.getElementsByTag("tbody").first()
                        .getElementsByTag("tr").get(1)
                        .getElementsByTag("td").get(1)
                        .html();

                Log.i("News Parsing", "HTML Result:\n" + result);

                result = result.replaceFirst("<div.+-End-.+<\\/div>", "");
                result = result.replaceFirst("<div.+-Read-More-.+<\\/div>", "");
                result = result.replaceAll("font-size.+pt;", "font-size:13px;");
                result = result + "<br>";

                Log.i("News Parsing", "Got HTML: \n" + result);
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            Log.i("News Parsing", "Starting WebView load");
            webView.loadData("<style>img{display: inline;height: auto;max-width: 100%;}</style>" + s, "text/html", null);
            Log.i("News Parsing", "Loaded Data?");
        }

    }

    /**
     * Created by Mitchell Skaggs on 2/14/17.
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
