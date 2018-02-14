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

package org.pattonvillecs.pattonvilleapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.annimon.stream.Stream;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.pattonvillecs.pattonvilleapp.calendar.data.KryoUtil;
import org.pattonvillecs.pattonvilleapp.calendar.pinned.PinnedEventsContract;
import org.pattonvillecs.pattonvilleapp.di.DaggerAppComponent;
import org.pattonvillecs.pattonvilleapp.directory.DirectoryAsyncTask;
import org.pattonvillecs.pattonvilleapp.directory.DirectoryParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.directory.detail.Faculty;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListenable;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;
import org.pattonvillecs.pattonvilleapp.news.NewsParsingAsyncTask;
import org.pattonvillecs.pattonvilleapp.news.NewsParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.news.articles.NewsArticle;
import org.pattonvillecs.pattonvilleapp.preferences.OnSharedPreferenceKeyChangedListener;
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils;
import org.pattonvillecs.pattonvilleapp.preferences.SchoolSelectionPreferenceListener;
import org.pattonvillecs.pattonvilleapp.service.model.calendar.PinnedEventMarker;
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository;
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarSyncJobService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import kotlin.Unit;
import kotlinx.coroutines.experimental.CommonPool;
import kotlinx.coroutines.experimental.CoroutineStart;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static kotlinx.coroutines.experimental.DeferredKt.async;

/**
 * The main {@link android.app.Application} class of the Pattonville App.
 *
 * @author Mitchell Skaggs
 * @since 1.0.0
 */

public class PattonvilleApplication extends DaggerApplication implements SharedPreferences.OnSharedPreferenceChangeListener, PauseableListenable {
    public static final String TOPIC_ALL_MIDDLE_SCHOOLS = "All-Middle-Schools";
    public static final String TOPIC_ALL_ELEMENTARY_SCHOOLS = "All-Elementary-Schools";
    public static final String TOPIC_TEST = "test";
    private static final String TAG = PattonvilleApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private List<OnSharedPreferenceKeyChangedListener> onSharedPreferenceKeyChangedListeners;
    private KryoPool kryoPool;

    private ConcurrentMap<DataSource, List<Faculty>> directoryData;
    private Set<DirectoryAsyncTask> runningDirectoryAsyncTasks;

    private ConcurrentMap<DataSource, List<NewsArticle>> newsData;
    private Set<NewsParsingAsyncTask> runningNewsAsyncTasks;

    /**
     * Similar to {@link java.util.AbstractList#modCount}, but for every key seen so far
     */
    private Map<String, Integer> keyModificationCounts;
    private List<PauseableListener<?>> pauseableListeners;

    public static PattonvilleApplication get(Activity activity) {
        return (PattonvilleApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        mRequestQueue = Volley.newRequestQueue(this);
        onSharedPreferenceKeyChangedListeners = new ArrayList<>();
        pauseableListeners = new ArrayList<>();
        keyModificationCounts = new HashMap<>();
        kryoPool = new KryoPool.Builder(new KryoUtil.KryoRegistrationFactory()).softReferences().build();

        directoryData = new ConcurrentHashMap<>();
        runningDirectoryAsyncTasks = Collections.synchronizedSet(new HashSet<DirectoryAsyncTask>());

        newsData = new ConcurrentHashMap<>();
        runningNewsAsyncTasks = Collections.synchronizedSet(new HashSet<NewsParsingAsyncTask>());

        SharedPreferences sharedPreferences = PreferenceUtils.getSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setupFirebaseTopics();
        setUpNewsParsing();
        setUpDirectoryParsing();
        enableHttpResponseCache();
    }

    @Inject
    protected void createCalendarSyncJob(FirebaseJobDispatcher firebaseJobDispatcher) {
        firebaseJobDispatcher.schedule(CalendarSyncJobService.getRecurringCalendarSyncJob(firebaseJobDispatcher));
    }

    /**
     * This method transfers pinned events from the Content Provider database to the new Room Database.
     *
     * @since 1.2.0
     */
    @Inject
    public void transferOldPinnedEvents(CalendarRepository calendarRepository) {
        async(CommonPool.INSTANCE, CoroutineStart.DEFAULT, (coroutineScope, continuation) -> {

            Cursor cursor = getContentResolver().query(PinnedEventsContract.PinnedEventsTable.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            Log.d(TAG, "Loaded old pinned events!");
            Set<String> uids = new HashSet<>();
            while (cursor != null && cursor.moveToNext()) {
                uids.add(cursor.getString(1));
            }
            if (cursor != null)
                cursor.close();
            Log.d(TAG, "Old pinned events: " + uids);

            calendarRepository.insertPins(Stream.of(uids).map(PinnedEventMarker::new).toList());
            Log.d(TAG, "Inserted old pins into new database!");

            Stream.of(uids).forEach(
                    uid -> {
                        Log.d(TAG, "Deleting pin \"" + uid + "\" from old database!");
                        getContentResolver().delete(
                                PinnedEventsContract.PinnedEventsTable.CONTENT_URI,
                                PinnedEventsContract.PinnedEventsTable.COLUMN_NAME_UID + "=?",
                                new String[]{uid});
                    }
            );

            return Unit.INSTANCE;
        });
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }

    /**
     * This is to create an HTTP cache that we can use to prevent constant downloads when loading articles
     */
    @Deprecated
    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.d(TAG, "HTTP response cache is unavailable.");
        }
    }

    private void setUpDirectoryParsing() {
        executeDirectoryDataTasks();
    }

    private void setupFirebaseTopics() {
        this.registerOnPreferenceKeyChangedListener(new SchoolSelectionPreferenceListener() {
            @Override
            public void keyChanged(SharedPreferences sharedPreferences, String key) {
                Set<DataSource> newSelectedDataSources = PreferenceUtils.getSelectedSchoolsSet(PattonvilleApplication.this);

                FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();

                for (DataSource dataSource : DataSource.ALL) {
                    firebaseMessaging.unsubscribeFromTopic(dataSource.topicName);
                }
                firebaseMessaging.unsubscribeFromTopic(TOPIC_ALL_MIDDLE_SCHOOLS);
                firebaseMessaging.unsubscribeFromTopic(TOPIC_ALL_ELEMENTARY_SCHOOLS);
                firebaseMessaging.unsubscribeFromTopic(TOPIC_TEST);

                for (DataSource dataSource : newSelectedDataSources) {
                    firebaseMessaging.subscribeToTopic(dataSource.topicName);
                    if (dataSource.isElementarySchool) {
                        firebaseMessaging.subscribeToTopic(TOPIC_ALL_ELEMENTARY_SCHOOLS);
                    } else if (dataSource.isMiddleSchool) {
                        firebaseMessaging.subscribeToTopic(TOPIC_ALL_MIDDLE_SCHOOLS);
                    }
                }

                if (BuildConfig.DEBUG) {
                    firebaseMessaging.subscribeToTopic(TOPIC_TEST);
                }
            }
        });
    }

    private void executeDirectoryDataTasks() {
        new DirectoryAsyncTask(PattonvilleApplication.this, false).executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    private void setUpNewsParsing() {
        this.registerOnPreferenceKeyChangedListener(new SchoolSelectionPreferenceListener() {
            @Override
            public void keyChanged(SharedPreferences sharedPreferences, String key) {
                Set<DataSource> newSelectedDataSources = PreferenceUtils.getSelectedSchoolsSet(PattonvilleApplication.this);

                Stream.of(newsData.keySet())
                        .filter(dataSource -> !newSelectedDataSources.contains(dataSource))
                        .forEach(dataSource -> newsData.remove(dataSource));

                newSelectedDataSources.removeAll(newsData.keySet()); //Remove DataSources that are already present

                executeNewsDataTasks(newSelectedDataSources, false);
            }
        });

        //Initial download of news
        executeNewsDataTasks(PreferenceUtils.getSelectedSchoolsSet(this), false);
    }

    private void executeNewsDataTasks(Set<DataSource> dataSources, boolean skipCacheLoad) {
        Stream.of(dataSources)
                .filter(dataSource -> dataSource.newsURL.isPresent())
                .forEach(dataSource -> new NewsParsingAsyncTask(PattonvilleApplication.this, skipCacheLoad).executeOnExecutor(THREAD_POOL_EXECUTOR, dataSource));
    }

    public Kryo borrowKryo() {
        return kryoPool.borrow();
    }

    public void releaseKryo(Kryo kryo) {
        kryoPool.release(kryo);
    }

    public int getPreferenceKeyModificationCount(String key) {
        if (keyModificationCounts.containsKey(key))
            return keyModificationCounts.get(key);
        else
            return 0;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void registerOnPreferenceKeyChangedListener(OnSharedPreferenceKeyChangedListener onSharedPreferenceKeyChangedListener) {
        int index = onSharedPreferenceKeyChangedListeners.indexOf(onSharedPreferenceKeyChangedListener);
        if (index > -1)
            onSharedPreferenceKeyChangedListeners.set(index, onSharedPreferenceKeyChangedListener);
        else
            onSharedPreferenceKeyChangedListeners.add(onSharedPreferenceKeyChangedListener);
    }

    public void unregisterOnPreferenceKeyChangedListener(OnSharedPreferenceKeyChangedListener onSharedPreferenceKeyChangedListener) {
        onSharedPreferenceKeyChangedListeners.remove(onSharedPreferenceKeyChangedListener);
    }

    private void updateDirectoryListeners(DirectoryParsingUpdateData data) {
        Log.d(TAG, "Updating directory listeners");

        for (PauseableListener<?> pauseableListener : pauseableListeners) {
            if (!pauseableListener.isPaused()) {
                Log.d(TAG, "Checking listener " + pauseableListener);

                if (pauseableListener.getIdentifier() == DirectoryParsingUpdateData.DIRECTORY_LISTENER_ID) {
                    Log.d(TAG, "Updating directory listener " + pauseableListener);

                    //noinspection unchecked
                    ((PauseableListener<DirectoryParsingUpdateData>) pauseableListener).onReceiveData(data);
                }
            } else {
                Log.d(TAG, "Skipping paused listener");
            }
        }
    }

    public void updateDirectoryListeners() {
        updateDirectoryListeners(getCurrentDirectoryParsingUpdateData());
    }

    private DirectoryParsingUpdateData getCurrentDirectoryParsingUpdateData() {
        return new DirectoryParsingUpdateData(directoryData);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "Preference changed: " + key + " modifications are now: " + keyModificationCounts);

        if (keyModificationCounts.containsKey(key))
            keyModificationCounts.put(key, keyModificationCounts.get(key) + 1);
        else
            keyModificationCounts.put(key, 1);

        for (OnSharedPreferenceKeyChangedListener onSharedPreferenceKeyChangedListener : onSharedPreferenceKeyChangedListeners) {
            if (onSharedPreferenceKeyChangedListener.getListenedKeys().contains(key))
                onSharedPreferenceKeyChangedListener.keyChanged(sharedPreferences, key);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void pause(PauseableListener<?> pauseableListener) {
        switch (pauseableListener.getIdentifier()) {
            case NewsParsingUpdateData.NEWS_LISTENER_ID:
                Log.i(TAG, "News update listener paused!");
                ((PauseableListener<NewsParsingUpdateData>) pauseableListener).onResume(getCurrentNewsParsingUpdateData());
                break;
            case DirectoryParsingUpdateData.DIRECTORY_LISTENER_ID:
                Log.i(TAG, "Directory update listener paused!");
                ((PauseableListener<DirectoryParsingUpdateData>) pauseableListener).onPause(getCurrentDirectoryParsingUpdateData());
                break;
            default:
                throw new IllegalArgumentException("Listener not known!");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void resume(PauseableListener<?> pauseableListener) {
        switch (pauseableListener.getIdentifier()) {
            case NewsParsingUpdateData.NEWS_LISTENER_ID:
                Log.i(TAG, "News update listener resumed!");
                ((PauseableListener<NewsParsingUpdateData>) pauseableListener).onResume(getCurrentNewsParsingUpdateData());
                break;
            case DirectoryParsingUpdateData.DIRECTORY_LISTENER_ID:
                Log.i(TAG, "Directory update listener resumed!");
                ((PauseableListener<DirectoryParsingUpdateData>) pauseableListener).onResume(getCurrentDirectoryParsingUpdateData());
                break;
            default:
                throw new IllegalArgumentException("Listener not known!");
        }
    }

    @Override
    public void registerPauseableListener(PauseableListener<?> pauseableListener) {
        pauseableListeners.add(pauseableListener);
    }

    @Override
    public void unregisterPauseableListener(PauseableListener<?> pauseableListener) {
        pauseableListeners.remove(pauseableListener);
    }

    public Set<NewsParsingAsyncTask> getRunningNewsAsyncTasks() {
        return runningNewsAsyncTasks;
    }

    public void updateNewsListeners() {
        updateNewsListeners(getCurrentNewsParsingUpdateData());
    }

    private void updateNewsListeners(NewsParsingUpdateData newsParsingUpdateData) {
        Log.d(TAG, "Updating news listeners");
        for (PauseableListener<?> pauseableListener : pauseableListeners) {
            Log.d(TAG, "Checking listener " + pauseableListener);
            if (!pauseableListener.isPaused()) {
                Log.d(TAG, "Check passed for listener " + pauseableListener);
                if (pauseableListener.getIdentifier() == NewsParsingUpdateData.NEWS_LISTENER_ID) {
                    Log.d(TAG, "Updating news listener " + pauseableListener);
                    //noinspection unchecked
                    ((PauseableListener<NewsParsingUpdateData>) pauseableListener).onReceiveData(newsParsingUpdateData);
                }
            } else {
                Log.d(TAG, "Skipping paused listener");
            }
        }
    }

    private NewsParsingUpdateData getCurrentNewsParsingUpdateData() {
        return new NewsParsingUpdateData(newsData, runningNewsAsyncTasks);
    }

    public Map<DataSource, List<NewsArticle>> getNewsData() {
        return newsData;
    }

    public void hardRefreshNewsData() {
        executeNewsDataTasks(PreferenceUtils.getSelectedSchoolsSet(this), true);
    }

    public ConcurrentMap<DataSource, List<Faculty>> getDirectoryData() {
        return directoryData;
    }

    public Set<DirectoryAsyncTask> getRunningDirectoryAsyncTasks() {
        return runningDirectoryAsyncTasks;
    }
}
