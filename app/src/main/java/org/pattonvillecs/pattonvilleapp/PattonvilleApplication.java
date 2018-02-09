/*
 * Copyright (C) 2017 - 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

import com.annimon.stream.Stream;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;
import com.thefinestartist.Base;

import org.pattonvillecs.pattonvilleapp.calendar.pinned.PinnedEventsContract;
import org.pattonvillecs.pattonvilleapp.di.DaggerAppComponent;
import org.pattonvillecs.pattonvilleapp.preferences.OnSharedPreferenceKeyChangedListener;
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils;
import org.pattonvillecs.pattonvilleapp.service.model.calendar.PinnedEventMarker;
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository;
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarSyncJobService;
import org.pattonvillecs.pattonvilleapp.service.repository.directory.DirectorySyncJobService;
import org.pattonvillecs.pattonvilleapp.service.repository.news.NewsSyncJobService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import kotlin.Unit;
import kotlinx.coroutines.experimental.CommonPool;
import kotlinx.coroutines.experimental.CoroutineStart;

import static kotlinx.coroutines.experimental.DeferredKt.async;

/**
 * The main {@link android.app.Application} class of the Pattonville App.
 *
 * @author Mitchell Skaggs
 * @since 1.0.0
 */

public class PattonvilleApplication extends DaggerApplication implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TOPIC_ALL_MIDDLE_SCHOOLS = "All-Middle-Schools";
    public static final String TOPIC_ALL_ELEMENTARY_SCHOOLS = "All-Elementary-Schools";
    public static final String TOPIC_TEST = "test";
    private static final String TAG = PattonvilleApplication.class.getSimpleName();

    private List<OnSharedPreferenceKeyChangedListener> onSharedPreferenceKeyChangedListeners;
    private Map<String, Integer> keyModificationCounts;

    public static PattonvilleApplication get(Activity activity) {
        return (PattonvilleApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        AndroidThreeTen.init(this);
        Base.initialize(this);

        onSharedPreferenceKeyChangedListeners = new ArrayList<>();
        keyModificationCounts = new HashMap<>();

        SharedPreferences sharedPreferences = PreferenceUtils.getSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setupFirebaseTopics();
    }

    @Inject
    protected void createCalendarSyncJob(FirebaseJobDispatcher firebaseJobDispatcher) {
        firebaseJobDispatcher.schedule(CalendarSyncJobService.getRecurringCalendarSyncJob(firebaseJobDispatcher));
    }

    @Inject
    protected void createDirectorySyncJob(FirebaseJobDispatcher firebaseJobDispatcher) {
        firebaseJobDispatcher.schedule(DirectorySyncJobService.getRecurringDirectorySyncJob(firebaseJobDispatcher));
    }

    @Inject
    protected void createNewsSyncJob(FirebaseJobDispatcher firebaseJobDispatcher) {
        firebaseJobDispatcher.schedule(NewsSyncJobService.getRecurringNewsSyncJob(firebaseJobDispatcher));
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

    private void setupFirebaseTopics() {
        PreferenceUtils.getSelectedSchoolsLiveData(this).observeForever(dataSources -> {
            if (dataSources != null) {
                FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();

                for (DataSource dataSource : DataSource.ALL)
                    firebaseMessaging.unsubscribeFromTopic(dataSource.topicName);
                firebaseMessaging.unsubscribeFromTopic(TOPIC_ALL_MIDDLE_SCHOOLS);
                firebaseMessaging.unsubscribeFromTopic(TOPIC_ALL_ELEMENTARY_SCHOOLS);
                firebaseMessaging.unsubscribeFromTopic(TOPIC_TEST);

                for (DataSource dataSource : dataSources) {
                    firebaseMessaging.subscribeToTopic(dataSource.topicName);
                    if (dataSource.isElementarySchool)
                        firebaseMessaging.subscribeToTopic(TOPIC_ALL_ELEMENTARY_SCHOOLS);
                    else if (dataSource.isMiddleSchool)
                        firebaseMessaging.subscribeToTopic(TOPIC_ALL_MIDDLE_SCHOOLS);
                }

                if (BuildConfig.DEBUG) firebaseMessaging.subscribeToTopic(TOPIC_TEST);
            }
        });
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

    @Override
    @Deprecated
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
}
