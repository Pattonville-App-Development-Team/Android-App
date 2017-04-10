package org.pattonvillecs.pattonvilleapp.news;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.news.articles.NewsArticle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NewsParsingAsyncTask extends AsyncTask<DataSource, Double, List<NewsArticle>> {
    private static final String TAG = NewsParsingAsyncTask.class.getSimpleName();
    private static final long NEWS_CACHE_EXPIRATION_HOURS = 24;
    private final PattonvilleApplication pattonvilleApplication;
    private DataSource dataSource;
    private Kryo kryo;

    public NewsParsingAsyncTask(PattonvilleApplication pattonvilleApplication) {
        this.pattonvilleApplication = pattonvilleApplication;
    }

    private static ArrayList<NewsArticle> parseNewsArticles(String result, DataSource dataSource) {
        NewsParser parser = new NewsParser(result, dataSource);
        parser.getXml();

        return parser.getItems();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.kryo = pattonvilleApplication.borrowKryo();
        pattonvilleApplication.getRunningNewsAsyncTasks().add(this);
        pattonvilleApplication.updateNewsListeners();
    }

    @Override
    protected List<NewsArticle> doInBackground(DataSource... params) {
        if (params.length != 1)
            throw new IllegalArgumentException("Requires a single DataSource");
        dataSource = params[0];
        Log.i(TAG, "Starting news loading for " + dataSource);

        NetworkInfo networkInfo = ((ConnectivityManager) pattonvilleApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean hasInternet = networkInfo != null && networkInfo.isConnected();

        File newsDataCache = new File(this.pattonvilleApplication.getCacheDir(), dataSource.shortName + "_news.bin");

        boolean cacheExists = newsDataCache.exists();
        long cacheAge = System.currentTimeMillis() - newsDataCache.lastModified();
        boolean cacheIsYoung = TimeUnit.HOURS.convert(cacheAge, TimeUnit.MILLISECONDS) < NEWS_CACHE_EXPIRATION_HOURS;


        if (cacheExists && (cacheIsYoung || !hasInternet)) {
            //Attempt to load the cache
            boolean isCacheCorrupt;
            String cachedNewsData = null;

            Input input = null;
            try {
                input = new Input(new FileInputStream(newsDataCache));

                //noinspection unchecked
                cachedNewsData = this.kryo.readObject(input, String.class);
                isCacheCorrupt = false;
            } catch (FileNotFoundException e) {
                Log.wtf(TAG, "This should never happen. The file should already be checked to exist before opening.");
                isCacheCorrupt = true;
            } catch (Exception e) {
                Log.e(TAG, "Other error thrown! Needs investigation!");
                isCacheCorrupt = true;
                e.printStackTrace();
            } finally {
                if (input != null) {
                    //noinspection ThrowFromFinallyBlock
                    input.close();
                }
            }

            if (!isCacheCorrupt) {
                assert cachedNewsData != null;

                Log.i(TAG, "Got cached news for " + dataSource.shortName);
                return parseNewsArticles(cachedNewsData, dataSource);
            }
        }

        boolean downloadSucceeded = false;
        String result = null;
        if (hasInternet) {
            RequestFuture<String> requestFuture = RequestFuture.newFuture();
            StringRequest request = new StringRequest(dataSource.newsURL, requestFuture, requestFuture);
            request.setRetryPolicy(new DefaultRetryPolicy(3000, 10, 1.3f));
            pattonvilleApplication.getRequestQueue().add(request);

            //Wait for the request
            try {
                result = requestFuture.get(5, TimeUnit.MINUTES);
                downloadSucceeded = true;
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted!");
                downloadSucceeded = false;
            } catch (ExecutionException e) {
                Log.e(TAG, "Execution exception!", e);
                downloadSucceeded = false;
            } catch (TimeoutException e) {
                Log.e(TAG, "Download timed out!");
                downloadSucceeded = false;
            }

            //Continue if the request succeeded
            if (downloadSucceeded) {
                assert result != null;

                Output output = null;
                try {
                    output = new Output(new FileOutputStream(newsDataCache));

                    this.kryo.writeObject(output, result);

                    if (!newsDataCache.setLastModified(System.currentTimeMillis()))
                        Log.e(TAG, "Failed to set last modified time!");
                } catch (FileNotFoundException e) {
                    Log.wtf(TAG, "Should never happen!", e);
                } finally {
                    if (output != null) {
                        output.close();
                    }
                }

                return parseNewsArticles(result, dataSource);
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<NewsArticle> result) {
        super.onPostExecute(result);
        if (result != null) {
            pattonvilleApplication.getNewsData().put(dataSource, result);
        }
        removeAndUpdate();
    }

    private void removeAndUpdate() {
        Log.i(TAG, "Removing from size: " + pattonvilleApplication.getRunningNewsAsyncTasks().size());
        pattonvilleApplication.getRunningNewsAsyncTasks().remove(this);
        Log.i(TAG, "Now size: " + pattonvilleApplication.getRunningNewsAsyncTasks().size());
        pattonvilleApplication.updateNewsListeners();
    }

    @Override
    protected void onCancelled(List<NewsArticle> newsArticles) {
        super.onCancelled(newsArticles);
        removeAndUpdate();
    }
}
