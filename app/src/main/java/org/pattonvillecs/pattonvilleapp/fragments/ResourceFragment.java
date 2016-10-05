package org.pattonvillecs.pattonvilleapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResourceFragment extends Fragment {

    public static final String FRAGMENT_TAG = "RESOURCE_FRAGMENT";
    private static final String TAG = "ResourceFragment";

    private Map<String, Object> data;

    public ResourceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = new HashMap<>(); //TODO Load shared data from disk?

        setRetainInstance(true);

        Log.e(TAG, "Finished onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new View(getActivity());
    }

    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object getOrDefaultAndPut(String key, Object defaultValue) {
        Object value = data.get(key);
        if (value != null)
            return value;
        else {
            data.put(key, defaultValue);
            return defaultValue;
        }
    }

    public Object getOrDefault(String key, Object defaultValue) {
        Object value = data.get(key);
        if (value != null)
            return value;
        else {
            return defaultValue;
        }
    }
}
