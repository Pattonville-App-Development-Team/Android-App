package org.pattonvillecs.pattonvilleapp.directory;

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
import org.pattonvillecs.pattonvilleapp.directory.detail.Faculty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by skaggsm on 2/10/17.
 */

public class DirectoryAsyncTask extends AsyncTask<Void, Void, Map<DataSource, List<Faculty>>> {
    private static final String TAG = DirectoryAsyncTask.class.getSimpleName();
    private static final long DIRECTORY_CACHE_EXPIRATION_HOURS = 24 * 7;
    private final PattonvilleApplication pattonvilleApplication;
    private final boolean skipCacheLoad;
    private Kryo kryo;
    private String directoryURL = "https://forms.psdr3.org/psdapp/directory.csv";

    public DirectoryAsyncTask(PattonvilleApplication pattonvilleApplication, boolean skipCacheLoad) {
        this.pattonvilleApplication = pattonvilleApplication;
        this.skipCacheLoad = skipCacheLoad;
    }

    @Override
    protected void onPreExecute() { // Run on UI thread
        super.onPreExecute();
        this.pattonvilleApplication.getRunningDirectoryAsyncTasks().add(this);
        this.kryo = pattonvilleApplication.borrowKryo();
    }

    @Override
    protected Map<DataSource, List<Faculty>> doInBackground(Void... params) { // Run on different thread
        //change to getting it from online

        Log.i(TAG, "Starting directory loading");

        NetworkInfo networkInfo = ((ConnectivityManager) pattonvilleApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean hasInternet = networkInfo != null && networkInfo.isConnected();

        File directoryDataCache = new File(this.pattonvilleApplication.getCacheDir(), "directory.bin");

        boolean cacheExists = directoryDataCache.exists();
        long cacheAge = System.currentTimeMillis() - directoryDataCache.lastModified();
        boolean cacheIsYoung = TimeUnit.HOURS.convert(cacheAge, TimeUnit.MILLISECONDS) < DIRECTORY_CACHE_EXPIRATION_HOURS;


        if (!skipCacheLoad && cacheExists && (cacheIsYoung || !hasInternet)) {
            //Attempt to load the cache
            boolean isCacheCorrupt;
            String cachedNewsData = null;

            Input input = null;
            try {
                input = new Input(new FileInputStream(directoryDataCache));

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

                Log.i(TAG, "Got cached directory");
                return parseDirectoryArticles(cachedNewsData);
            }
        }

        if (hasInternet) {
            RequestFuture<String> requestFuture = RequestFuture.newFuture();
            StringRequest request = new StringRequest(directoryURL, requestFuture, requestFuture);
            request.setRetryPolicy(new DefaultRetryPolicy(3000, 10, 1.3f));
            pattonvilleApplication.getRequestQueue().add(request);

            String result = null;
            boolean downloadSucceeded;
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
                    output = new Output(new FileOutputStream(directoryDataCache));

                    this.kryo.writeObject(output, result);

                    if (!directoryDataCache.setLastModified(System.currentTimeMillis()))
                        Log.e(TAG, "Failed to set last modified time!");
                } catch (FileNotFoundException e) {
                    Log.wtf(TAG, "Should never happen!", e);
                } finally {
                    if (output != null) {
                        output.close();
                    }
                }

                return parseDirectoryArticles(result);
            }
        }
        return null;
    }

    private Map<DataSource, List<Faculty>> parseDirectoryArticles(String result) {
        Map<DataSource, List<Faculty>> faculties = new HashMap<>();
        Scanner scanner = new Scanner(result);
        String directoryLine;
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            directoryLine = scanner.nextLine();
            String[] person = directoryLine.split("\\s*,\\s*", -1);
            Faculty facultyMember = new Faculty(person);
            Log.v(TAG, "Current line: " + facultyMember.getLastName() + " " + facultyMember.getRank());
            DataSource data = facultyMember.getDirectoryKey();
            if (!faculties.containsKey(data)) {
                faculties.put(data, new ArrayList<>());
            }
            faculties.get(data).add(facultyMember);
        }
        return faculties;
    }

    @Override
    protected void onPostExecute(Map<DataSource, List<Faculty>> result) { // Run on UI thread
        super.onPostExecute(result);
        pattonvilleApplication.releaseKryo(kryo);

        if (result != null) {
            for (DataSource key : result.keySet()) {
                Collections.sort(result.get(key), new Comparator<Faculty>() {
                    @Override
                    public int compare(Faculty o1, Faculty o2) {
                        if (o1.getRank() == o2.getRank()) {
                            if (o1.getLongDesc().equals(o2.getLongDesc())) {
                                if (o1.getLastName().equals(o2.getLastName())) {
                                    return o1.getFirstName().compareTo(o2.getFirstName());
                                } else {
                                    return o1.getLastName().compareTo(o2.getLastName());
                                }
                            } else {
                                return o1.getLongDesc().compareTo(o2.getLongDesc());
                            }
                        } else {
                            return o1.getRank() - o2.getRank();
                        }
                    }
                });
            }
            pattonvilleApplication.getDirectoryData().putAll(result);
        }

        this.pattonvilleApplication.getRunningDirectoryAsyncTasks().remove(this);
        this.pattonvilleApplication.updateDirectoryListeners();
    }

    @Override
    protected void onCancelled(Map<DataSource, List<Faculty>> faculties) {
        super.onCancelled(faculties);

        //TODO Handle if the application closes during
    }
}