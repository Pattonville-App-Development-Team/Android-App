package org.pattonvillecs.pattonvilleapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.common.collect.HashMultimap;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.KryoUtil;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.RetrieveCalendarDataAsyncTask;
import org.pattonvillecs.pattonvilleapp.fragments.news.NewsParsingAsyncTask;
import org.pattonvillecs.pattonvilleapp.fragments.news.NewsParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.fragments.news.articles.NewsArticle;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListenable;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;
import org.pattonvillecs.pattonvilleapp.preferences.OnSharedPreferenceKeyChangedListener;
import org.pattonvillecs.pattonvilleapp.preferences.SchoolSelectionPreferenceListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Mitchell Skaggs on 12/19/16.
 */

public class PattonvilleApplication extends MultiDexApplication implements SharedPreferences.OnSharedPreferenceChangeListener, PauseableListenable {
    private static final String TAG = PattonvilleApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private List<OnSharedPreferenceKeyChangedListener> onSharedPreferenceKeyChangedListeners;
    private KryoPool kryoPool;

    private ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendarData;
    private Set<RetrieveCalendarDataAsyncTask> runningCalendarAsyncTasks;

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
        mRequestQueue = Volley.newRequestQueue(this);
        onSharedPreferenceKeyChangedListeners = new LinkedList<>();
        pauseableListeners = new LinkedList<>();
        keyModificationCounts = new HashMap<>();
        kryoPool = new KryoPool.Builder(new KryoUtil.KryoRegistrationFactory()).softReferences().build();

        calendarData = new ConcurrentHashMap<>();
        runningCalendarAsyncTasks = Collections.synchronizedSet(new HashSet<RetrieveCalendarDataAsyncTask>());

        newsData = new ConcurrentHashMap<>();
        runningNewsAsyncTasks = Collections.synchronizedSet(new HashSet<NewsParsingAsyncTask>());

        SharedPreferences sharedPreferences = PreferenceUtils.getSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setUpCalendarParsing();
        setUpNewsParsing();
    }

    private void setUpCalendarParsing() {
        this.registerOnPreferenceKeyChangedListener(new SchoolSelectionPreferenceListener() {
            @Override
            public void keyChanged(SharedPreferences sharedPreferences, String key) {
                Set<DataSource> newSelectedDataSources = PreferenceUtils.getSelectedSchoolsSet(sharedPreferences);

                for (DataSource dataSource : calendarData.keySet()) {
                    if (!newSelectedDataSources.contains(dataSource)) {
                        calendarData.remove(dataSource);
                    }
                }

                newSelectedDataSources.removeAll(calendarData.keySet()); //Remove DataSources that are already present

                executeCalendarDataTasks(newSelectedDataSources);
            }
        });

        //Initial download of calendar
        executeCalendarDataTasks(PreferenceUtils.getSelectedSchoolsSet(this));
    }

    private void setUpNewsParsing() {
        this.registerOnPreferenceKeyChangedListener(new SchoolSelectionPreferenceListener() {
            @Override
            public void keyChanged(SharedPreferences sharedPreferences, String key) {
                Set<DataSource> newSelectedDataSources = PreferenceUtils.getSelectedSchoolsSet(sharedPreferences);

                for (DataSource dataSource : newsData.keySet()) {
                    if (!newSelectedDataSources.contains(dataSource)) {
                        newsData.remove(dataSource);
                    }
                }

                newSelectedDataSources.removeAll(newsData.keySet()); //Remove DataSources that are already present

                executeNewsDataTasks(newSelectedDataSources);
            }
        });

        //Initial download of news
        executeNewsDataTasks(PreferenceUtils.getSelectedSchoolsSet(this));
    }

    private void executeNewsDataTasks(Set<DataSource> dataSources) {
        for (DataSource dataSource : dataSources) {
            new NewsParsingAsyncTask(PattonvilleApplication.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataSource);
        }
    }

    private void executeCalendarDataTasks(Set<DataSource> dataSources) {
        for (DataSource dataSource : dataSources) {
            new RetrieveCalendarDataAsyncTask(PattonvilleApplication.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataSource);
        }
    }

    public synchronized Kryo borrowKryo() {
        return kryoPool.borrow();
    }

    public synchronized void releaseKryo(Kryo kryo) {
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

    private void updateCalendarListeners(CalendarParsingUpdateData data) {
        Log.d(TAG, "Updating calendar listeners");
        for (PauseableListener<?> pauseableListener : pauseableListeners) {
            if (!pauseableListener.isPaused()) {
                Log.d(TAG, "Updating listener " + pauseableListener);
                if (pauseableListener.getIdentifier() == CalendarParsingUpdateData.CALENDAR_LISTENER_ID) {
                    Log.d(TAG, "Updating calendar listener " + pauseableListener);
                    //noinspection unchecked
                    ((PauseableListener<CalendarParsingUpdateData>) pauseableListener).onReceiveData(data);
                }
            } else {
                Log.d(TAG, "Skipping paused listener");
            }
        }
    }

    /**
     * Calls {@link PattonvilleApplication#updateCalendarListeners(CalendarParsingUpdateData)} with {@link PattonvilleApplication#getCurrentCalendarParsingUpdateData()} as the argument
     */
    public void updateCalendarListeners() {
        updateCalendarListeners(getCurrentCalendarParsingUpdateData());
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
            case CalendarParsingUpdateData.CALENDAR_LISTENER_ID:
                Log.i(TAG, "Calendar update listener paused!");
                ((PauseableListener<CalendarParsingUpdateData>) pauseableListener).onPause(getCurrentCalendarParsingUpdateData());
                break;
            case NewsParsingUpdateData.NEWS_LISTENER_ID:
                Log.i(TAG, "News update listener paused!");
                ((PauseableListener<NewsParsingUpdateData>) pauseableListener).onResume(getCurrentNewsParsingUpdateData());
                break;
            default:
                throw new IllegalArgumentException("Listener not known!");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void resume(PauseableListener<?> pauseableListener) {
        switch (pauseableListener.getIdentifier()) {
            case CalendarParsingUpdateData.CALENDAR_LISTENER_ID:
                Log.i(TAG, "Calendar update listener resumed!");
                ((PauseableListener<CalendarParsingUpdateData>) pauseableListener).onResume(getCurrentCalendarParsingUpdateData());
                break;
            case NewsParsingUpdateData.NEWS_LISTENER_ID:
                Log.i(TAG, "News update listener resumed!");
                ((PauseableListener<NewsParsingUpdateData>) pauseableListener).onResume(getCurrentNewsParsingUpdateData());
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

    private CalendarParsingUpdateData getCurrentCalendarParsingUpdateData() {
        return new CalendarParsingUpdateData(calendarData, runningCalendarAsyncTasks);
    }

    public ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> getCalendarData() {
        return calendarData;
    }

    public Set<RetrieveCalendarDataAsyncTask> getRunningCalendarAsyncTasks() {
        return runningCalendarAsyncTasks;
    }

    public void refreshCalendarData() {
        executeCalendarDataTasks(PreferenceUtils.getSelectedSchoolsSet(this));
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

    public void refreshNewsData() {
        executeNewsDataTasks(PreferenceUtils.getSelectedSchoolsSet(this));
    }
}
