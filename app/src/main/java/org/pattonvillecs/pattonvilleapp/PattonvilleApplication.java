package org.pattonvillecs.pattonvilleapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by skaggsm on 12/19/16.
 */

public class PattonvilleApplication extends MultiDexApplication implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = PattonvilleApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private List<OnSharedPreferenceKeyChangedListener> onSharedPreferenceKeyChangedListeners;

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

        PreferenceUtils.getSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
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
        Log.i(TAG, "Preference changed: " + key);

        if (keyModificationCounts.containsKey(key))
            keyModificationCounts.put(key, keyModificationCounts.get(key) + 1);
        else
            keyModificationCounts.put(key, 1);

        for (OnSharedPreferenceKeyChangedListener onSharedPreferenceKeyChangedListener : onSharedPreferenceKeyChangedListeners) {
            if (onSharedPreferenceKeyChangedListener.getListenedKeys().contains(key))
                onSharedPreferenceKeyChangedListener.keyChanged(sharedPreferences, key);
        }
    }

    public interface OnSharedPreferenceKeyChangedListener {
        Set<String> getListenedKeys();

        void keyChanged(SharedPreferences sharedPreferences, String key);
    }
}
