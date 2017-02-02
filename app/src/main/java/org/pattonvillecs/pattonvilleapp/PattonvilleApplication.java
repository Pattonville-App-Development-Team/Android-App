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

import net.fortuna.ical4j.model.component.VEvent;

import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarMonthFragment;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.KryoUtil;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.RetrieveCalendarDataAsyncTask;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListenable;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;
import org.pattonvillecs.pattonvilleapp.listeners.calendar.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.preferences.OnSharedPreferenceKeyChangedListener;
import org.pattonvillecs.pattonvilleapp.preferences.SchoolSelectionPreferenceListener;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by skaggsm on 12/19/16.
 */

public class PattonvilleApplication extends MultiDexApplication implements SharedPreferences.OnSharedPreferenceChangeListener, PauseableListenable {
    private static final String TAG = PattonvilleApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private List<OnSharedPreferenceKeyChangedListener> onSharedPreferenceKeyChangedListeners;
    private KryoPool kryoPool;
    private Map<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> calendarData;

    /**
     * Similar to {@link java.util.AbstractList#modCount}, but for every key seen so far
     */
    private Map<String, Integer> keyModificationCounts;

    public static PattonvilleApplication get(Activity activity) {
        return (PattonvilleApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        onSharedPreferenceKeyChangedListeners = new LinkedList<>();
        keyModificationCounts = new HashMap<>();
        kryoPool = new KryoPool.Builder(new KryoUtil.KryoRegistrationFactory()).softReferences().build();
        calendarData = new EnumMap<>(DataSource.class);

        PreferenceUtils.getSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        this.registerOnPreferenceKeyChangedListener(new SchoolSelectionPreferenceListener() {
            @Override
            public void keyChanged(SharedPreferences sharedPreferences, String key) {
                Set<DataSource> dataSources = PreferenceUtils.getSelectedSchoolsSet(sharedPreferences);

                dataSources.removeAll(calendarData.keySet()); //Remove DataSources that are already present

                for (DataSource dataSource : dataSources) {
                    new RetrieveCalendarDataAsyncTask(PattonvilleApplication.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataSource);
                }

                //TODO: PLAN THE DATA FLOW!!!
            }
        });
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
        switch (pauseableListener.getID()) {
            case CalendarMonthFragment.CALENDAR_LISTENER_ID:
                Log.i(TAG, "CalendarMonthFragment listener paused!");
                ((PauseableListener<CalendarParsingUpdateData>) pauseableListener).onPause(getCurrentCalendarParsingUpdateData());
                break;
            default:
                throw new IllegalArgumentException("Listener not known!");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void resume(PauseableListener<?> pauseableListener) {
        switch (pauseableListener.getID()) {
            case CalendarMonthFragment.CALENDAR_LISTENER_ID:
                Log.i(TAG, "CalendarMonthFragment listener resumed!");
                ((PauseableListener<CalendarParsingUpdateData>) pauseableListener).onResume(getCurrentCalendarParsingUpdateData());
                break;
            default:
                throw new IllegalArgumentException("Listener not known!");
        }
    }

    private CalendarParsingUpdateData getCurrentCalendarParsingUpdateData() {
        return new CalendarParsingUpdateData();
    }
}
