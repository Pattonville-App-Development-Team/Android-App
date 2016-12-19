package org.pattonvillecs.pattonvilleapp;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by skaggsm on 12/19/16.
 */

public class PattonvilleApplication extends MultiDexApplication {
    private RequestQueue mRequestQueue;

    public static PattonvilleApplication get(Activity activity) {
        return (PattonvilleApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
