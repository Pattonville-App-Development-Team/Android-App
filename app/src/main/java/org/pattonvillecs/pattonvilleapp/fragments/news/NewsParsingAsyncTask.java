package org.pattonvillecs.pattonvilleapp.fragments.news;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.fragments.news.articles.NewsArticle;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by skaggsm on 2/13/17.
 */

public class NewsParsingAsyncTask extends AsyncTask<DataSource, Double, List<NewsArticle>> {
    private static final String TAG = NewsParsingAsyncTask.class.getSimpleName();
    private final PattonvilleApplication pattonvilleApplication;
    private DataSource dataSource;

    public NewsParsingAsyncTask(PattonvilleApplication pattonvilleApplication) {
        this.pattonvilleApplication = pattonvilleApplication;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pattonvilleApplication.getRunningNewsAsyncTasks().add(this);
        pattonvilleApplication.updateNewsListeners();
    }

    @Override
    protected List<NewsArticle> doInBackground(DataSource... params) {
        if (params.length != 1)
            throw new IllegalArgumentException("Requires a single DataSource");
        dataSource = params[0];


        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(dataSource.newsURL, requestFuture, requestFuture);
        request.setRetryPolicy(new DefaultRetryPolicy(3000, 10, 1.3f));
        pattonvilleApplication.getRequestQueue().add(request);

        //Wait for the request
        String result = null;
        boolean downloadSucceeded;
        try {
            result = requestFuture.get(5, TimeUnit.MINUTES);
            downloadSucceeded = true;
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread interrupted!");
            downloadSucceeded = false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            downloadSucceeded = false;
        } catch (TimeoutException e) {
            Log.e(TAG, "Download timed out!");
            downloadSucceeded = false;
        }

        //Continue if the request succeeded
        if (downloadSucceeded) {
            assert result != null;

            NewsParser parser = new NewsParser(result, dataSource);
            parser.getXml();

            return parser.getItems();
        } else {
            return null;
        }
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
