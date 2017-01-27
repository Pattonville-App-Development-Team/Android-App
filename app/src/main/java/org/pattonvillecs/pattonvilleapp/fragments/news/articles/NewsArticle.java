package org.pattonvillecs.pattonvilleapp.fragments.news.articles;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.WebView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Date;

public class NewsArticle implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NewsArticle createFromParcel(Parcel in) {
            return new NewsArticle(in);
        }

        public NewsArticle[] newArray(int size) {
            return new NewsArticle[size];
        }
    };
    private Date publishDate;
    private String title;
    private String content;
    private String url;
    private int sourceColor;

    public NewsArticle() {

        publishDate = new Date(1484357778);
        title = "Some Title";
        url = "www.psdr3.org";

    }
    public NewsArticle(Parcel parcel) {

        String[] strings = parcel.createStringArray();
        title = strings[0];
        content = strings[1];
        url = strings[2];
        publishDate = new Date(parcel.readLong());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSourceColor() {
        return sourceColor;
    }

    public void setSourceColor(int sourceColor) {
        this.sourceColor = sourceColor;
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
        new NewsArticle.NewsContent(webView).execute(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        String[] strings = new String[]{title, content, url};

        parcel.writeStringArray(strings);
        parcel.writeLong(publishDate.getTime());
    }

    public static class NewsContent extends AsyncTask<String, Void, String> {

        private final WebView webView;

        public NewsContent(WebView webView) {

            Log.e("News Parsing", "Created NewsContent Aysnc");
            this.webView = webView;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {


                Connection resultC = Jsoup.connect(strings[0]);
                Log.e("News Parsing", "Jsoup Connected");

                Document resultD = resultC.get();

                Log.e("News Parsing", "Got Document");

                String result = resultD.getElementsByTag("article").last()
                        .getElementsByTag("table").last()
                        .getElementsByTag("tbody").first()
                        .getElementsByTag("tr").get(1)
                        .getElementsByTag("td").get(1)
                        .html();

                Log.e("News Parsing", "Got HTML");
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            Log.e("News Parsing", "Starting WebView load");
            webView.loadData(s, "text/html", null);
            Log.e("News Parsing", "Loaded Data?");
        }
    }
}
