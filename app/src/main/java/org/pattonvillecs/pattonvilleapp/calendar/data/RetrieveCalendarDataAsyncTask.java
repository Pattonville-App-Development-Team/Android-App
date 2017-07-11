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

package org.pattonvillecs.pattonvilleapp.calendar.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentFactoryImpl;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.lang3.time.StopWatch;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.calendar.events.EventFlexibleItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mitchell Skaggs on 1/25/2017.
 */

public class RetrieveCalendarDataAsyncTask extends AsyncTask<DataSource, Double, List<VEvent>> {

    private static final String TAG = RetrieveCalendarDataAsyncTask.class.getSimpleName();
    private static final long CALENDAR_CACHE_EXPIRATION_HOURS = 24 * 7;
    private final boolean skipCacheLoad;
    private PattonvilleApplication pattonvilleApplication;
    private Kryo kryo;
    private DataSource dataSource;

    public RetrieveCalendarDataAsyncTask(PattonvilleApplication pattonvilleApplication, boolean skipCacheLoad) {
        this.pattonvilleApplication = pattonvilleApplication;
        this.skipCacheLoad = skipCacheLoad;
    }

    private static String fixICalStrings(@NonNull String iCalString) {
        return iCalString.replace("FREQ=;", "FREQ=YEARLY;").replace("DTSTART;VALUE=DATE-TIME:", "DTSTART;TZID=US/Central:").replace("DTEND;VALUE=DATE-TIME:", "DTEND;TZID=US/Central:");
    }

    private static ArrayList<VEvent> parseFile(String iCalFile, DataSource dataSource) {
        Log.d(TAG, "Initial");
        ArrayList<VEvent> vEvents = new ArrayList<>();
        StringReader stringReader = new StringReader(iCalFile);
        CalendarBuilder calendarBuilder = new CalendarBuilder();
        Calendar calendar = null;
        Log.d(TAG, "Readers done");
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        try {
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
            synchronized (ComponentFactoryImpl.getInstance()) { //Needed because the static instance is accessed from multiple threads inside the build method
                calendar = calendarBuilder.build(stringReader);
            }
        } catch (IOException | ParserException e) {
            Log.e(TAG, "Failed to parse calendar for " + dataSource + "!!!", e);
        }
        stopWatch.stop();

        Log.d(TAG, "Calendar build time: " + stopWatch.getTime() + "ms");
        Log.v(TAG, Runtime.getRuntime().availableProcessors() + " possible simultaneous tasks with " + Runtime.getRuntime().maxMemory() + " bytes of memory max and " + Runtime.getRuntime().freeMemory() + " bytes of memory free");

        if (calendar != null) {
            Log.i(TAG, "VTIMEZONEs of " + dataSource + ": " + calendar.getComponents(CalendarComponent.VTIMEZONE));
            //VTimeZone vTimeZone = (VTimeZone) calendar.getComponent(CalendarComponent.VTIMEZONE);
            //final TimeZone timeZone = new TimeZone(vTimeZone);
            //Log.i(TAG, "parseFile: " + vTimeZone);
            vEvents = Stream.of(calendar.getComponents())
                    .filter(value -> value instanceof VEvent)
                    .map(value -> (VEvent) value)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return vEvents;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.kryo = pattonvilleApplication.borrowKryo();
        pattonvilleApplication.getRunningCalendarAsyncTasks().add(this);
        pattonvilleApplication.updateCalendarListeners();
    }

    @Override
    protected List<VEvent> doInBackground(DataSource... params) {
        this.dataSource = params[0];

        Log.i(TAG, "Getting calendar for " + dataSource.shortName);

        NetworkInfo networkInfo = ((ConnectivityManager) pattonvilleApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean hasInternet = networkInfo != null && networkInfo.isConnected();

        // Begin caching operations.

        File calendarDataCache = new File(this.pattonvilleApplication.getCacheDir(), dataSource.shortName + "_calendar.bin");

        boolean cacheExists = calendarDataCache.exists();
        long cacheAge = System.currentTimeMillis() - calendarDataCache.lastModified();
        boolean cacheIsYoung = TimeUnit.HOURS.convert(cacheAge, TimeUnit.MILLISECONDS) < CALENDAR_CACHE_EXPIRATION_HOURS; // One week expiration

        if (!skipCacheLoad && cacheExists && (cacheIsYoung || !hasInternet)) {
            //Attempt to load the cache
            boolean isCacheCorrupt;
            ArrayList<VEvent> cachedCalendarData = null;

            Input input = null;
            try {
                input = new Input(new FileInputStream(calendarDataCache));

                //noinspection unchecked
                cachedCalendarData = this.kryo.readObject(input, ArrayList.class);
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
                assert cachedCalendarData != null;

                Log.i(TAG, "Got cached calendar for " + dataSource.shortName + " of size " + cachedCalendarData.size());
                return cachedCalendarData;
            }
        }

        //End caching operations. Resort to redownloading and parsing calendars if connected.

        if (hasInternet) {
            //Add a request
            RequestFuture<String> requestFuture = RequestFuture.newFuture();
            StringRequest request = new StringRequest(
                    dataSource.calendarURL.orElseThrow(() -> new IllegalStateException("calendarURL must be present to download calendar! Did you filter by DataSources that have calendars?")),
                    requestFuture,
                    requestFuture);
            request.setRetryPolicy(new DefaultRetryPolicy(3000, 10, 1.3f));
            pattonvilleApplication.getRequestQueue().add(request);

            //Wait for the request
            String result = null;
            boolean downloadSucceeded;
            try {
                result = requestFuture.get(5, TimeUnit.MINUTES);
                downloadSucceeded = true;
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted!", e);
                downloadSucceeded = false;
            } catch (ExecutionException e) {
                Log.e(TAG, "Execution exception!", e);
                downloadSucceeded = false;
            } catch (TimeoutException e) {
                Log.e(TAG, "Download timed out!", e);
                downloadSucceeded = false;
            }

            //Continue if the request succeeded
            if (downloadSucceeded) {
                assert result != null;

                Log.d(TAG, "Starting iCal fix for " + dataSource);
                //Apply fix for iCal format
                String processedResult = fixICalStrings(result);
                Log.d(TAG, "Finished iCal fix for " + dataSource);

                ArrayList<VEvent> downloadedCalendarData = parseFile(processedResult, dataSource);

                Output output = null;
                try {
                    output = new Output(new FileOutputStream(calendarDataCache));

                    this.kryo.writeObject(output, downloadedCalendarData);

                    if (!calendarDataCache.setLastModified(System.currentTimeMillis()))
                        Log.e(TAG, "Failed to set last modified time!");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (output != null) {
                        output.close();
                    }
                }

                Log.i(TAG, "Got downloaded calendar for " + dataSource.shortName + " of size " + downloadedCalendarData.size());
                return downloadedCalendarData;
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Double... values) {
    }

    @Override
    protected void onPostExecute(List<VEvent> result) {
        releaseKryo();
        if (result != null) {
            TreeSet<EventFlexibleItem> existingEvents = pattonvilleApplication.getCalendarEvents();
            TreeSet<EventFlexibleItem> newEvents = new TreeSet<>();
            DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

            newVEventLoop:
            for (VEvent newVEvent : result) {
                for (EventFlexibleItem existingEvent : existingEvents) {
                    if (newVEvent.getUid().getValue().equals(existingEvent.vEvent.getUid().getValue())) {

                        if (!newVEvent.getStartDate().getDate().equals(existingEvent.vEvent.getStartDate().getDate()))
                            Log.e(TAG, "Dates not equal for " + newVEvent + " and " + existingEvent.vEvent);

                        existingEvent.dataSources.add(dataSource);

                        Log.v(TAG, "Merged event from " + dataSource + " to " + existingEvent.dataSources + ": \"" + newVEvent.getSummary().getValue() + "\"on " + dateFormat.format(newVEvent.getStartDate().getDate()) + " " + newVEvent.getUid().getValue());

                        continue newVEventLoop; //Continue to the next vEvent, after finding a match.
                    }
                }
                Log.v(TAG, "New event from " + dataSource + ": \"" + newVEvent.getSummary().getValue() + "\" on " + dateFormat.format(newVEvent.getStartDate().getDate()) + " " + newVEvent.getUid().getValue());
                newEvents.add(new EventFlexibleItem(dataSource, newVEvent)); //Reached if no match was found
            }

            Log.i(TAG, "Num. events|new events from " + dataSource + ": " + result.size() + "|" + newEvents.size());
            for (EventFlexibleItem newEventItem : newEvents) {
                boolean changed = existingEvents.add(newEventItem);
                if (!changed) {
                    String date = dateFormat.format(newEventItem.vEvent.getStartDate().getDate());
                    Log.e(TAG, "DUPLICATED event (\"" + newEventItem.vEvent.getSummary().getValue() + "\") on " + date + " for " + dataSource + " has UID " + newEventItem.vEvent.getUid().getValue());
                }
            }

            pattonvilleApplication.getLoadedCalendarDataSources().add(this.dataSource);
        }
        Log.i(TAG, "Removing from size: " + pattonvilleApplication.getRunningCalendarAsyncTasks().size());
        pattonvilleApplication.getRunningCalendarAsyncTasks().remove(this);

        Log.i(TAG, "Now size: " + pattonvilleApplication.getRunningCalendarAsyncTasks().size());
        pattonvilleApplication.updateCalendarListeners();
    }

    @Override
    protected void onCancelled(List<VEvent> result) {
        releaseKryo();
        pattonvilleApplication.getRunningCalendarAsyncTasks().remove(this);
        pattonvilleApplication.updateCalendarListeners();
    }

    private void releaseKryo() {
        pattonvilleApplication.releaseKryo(this.kryo);
    }
}
