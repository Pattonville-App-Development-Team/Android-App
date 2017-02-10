package org.pattonvillecs.pattonvilleapp.fragments.news.articles;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.WebView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.pattonvillecs.pattonvilleapp.DataSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewsArticle implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NewsArticle createFromParcel(Parcel in) {
            return new NewsArticle(in);
        }

        public NewsArticle[] newArray(int size) {
            return new NewsArticle[size];
        }
    };


    private static DateFormat shortDF = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    private static DateFormat longDF = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);

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
        new NewsArticle.NewsContent(webView).execute(privateUrl);
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        String[] strings = new String[]{title, content, publicUrl, privateUrl};

        parcel.writeStringArray(strings);
        parcel.writeLong(publishDate.getTime());
    }

    public static class NewsContent extends AsyncTask<String, Void, String> {

        private final WebView webView;

        public NewsContent(WebView webView) {

            Log.e("News Parsing", "Created NewsContent Async");
            this.webView = webView;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {


                Connection resultC = Jsoup.connect(strings[0]);
                Log.e("News Parsing", "Jsoup Connected");


                Document resultD = resultC.get();

                resultD.outputSettings().charset("ASCII");
                resultD.outputSettings().escapeMode(Entities.EscapeMode.extended);
                resultD.outputSettings().prettyPrint(false);

                Log.e("News Parsing", "Got Document");

                String result = resultD//.getElementsByTag("article").last()
                        .getElementsByTag("table").last()
                        //.getElementsByTag("tbody").first()
                        .getElementsByTag("tr").get(1)
                        .getElementsByTag("td").get(1)
                        .html();

                Log.e("News Parsing", "HTML Result:\n" + result);

                result = result.replaceFirst("<div.+-End-.+<\\/div>", "");
                result = result.replaceFirst("<div.+-Read-More-.+<\\/div>", "");
                result = result.replaceAll("font-size.+pt;", "font-size:13px;");
                result = result + "<br>";

                Log.e("News Parsing", "Got HTML: \n" + result);
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            Log.e("News Parsing", "Starting WebView load");
            webView.loadData("<style>img{display: inline;height: auto;max-width: 100%;}</style>" + s, "text/html", null);
            Log.e("News Parsing", "Loaded Data?");
        }
    }
}
