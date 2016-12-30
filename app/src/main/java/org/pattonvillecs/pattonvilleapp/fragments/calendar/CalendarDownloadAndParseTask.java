package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.lang3.time.StopWatch;
import org.pattonvillecs.pattonvilleapp.DataSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mitchell on 12/24/2016.
 * <p>
 * This class implements the asynchronous parsing of a downloaded iCal file.
 *
 * @author Mitchell Skaggs
 */

public class CalendarDownloadAndParseTask extends AsyncTask<Set<DataSource>, Double, CalendarData> {

    private static final String TAG = "CalendarDPTask";
    private final CalendarFragment calendarFragment;
    private final RequestQueue requestQueue;
    private final NetworkInfo activeNetwork;

    public CalendarDownloadAndParseTask(CalendarFragment calendarFragment, RequestQueue requestQueue) {
        this.calendarFragment = calendarFragment;
        this.requestQueue = requestQueue;
        ConnectivityManager cm = (ConnectivityManager) calendarFragment.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
    }

    private static String fixICalStrings(String iCalString) {
        return iCalString.replace("FREQ=;", "FREQ=YEARLY;");
    }

    private MultiValueMap<CalendarDay, VEvent> parseFile(String iCalFile) {
        Log.e(TAG, "Initial");
        MultiValueMap<CalendarDay, VEvent> map = MultiValueMap.multiValueMap(new HashMap<CalendarDay, HashSet<VEvent>>(), new Factory<HashSet<VEvent>>() {
            @Override
            public HashSet<VEvent> create() {
                return new HashSet<>();
            }
        });
        StringReader stringReader = new StringReader(fixICalStrings(iCalFile));
        CalendarBuilder calendarBuilder = new CalendarBuilder();
        Calendar calendar = null;
        Log.e(TAG, "Readers done");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
            calendar = calendarBuilder.build(stringReader);
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        Log.e(TAG, "Calendar built time: " + stopWatch.getTime() + "ms");
        if (calendar != null) {
            Set<VEvent> vEventSet = Stream.of(calendar.getComponents()).filter(new Predicate<CalendarComponent>() {
                @Override
                public boolean test(CalendarComponent value) {
                    return value instanceof VEvent;
                }
            }).map(new Function<CalendarComponent, VEvent>() {
                @Override
                public VEvent apply(CalendarComponent value) {
                    return (VEvent) value;
                }
            }).collect(Collectors.<VEvent>toSet());
            Log.e(TAG, "Set built");
            for (VEvent vEvent : vEventSet) {
                Date vEventDate = vEvent.getStartDate().getDate();
                map.put(CalendarDay.from(vEventDate), vEvent);
            }
            Log.e(TAG, "Map built");
        }
        return map;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e(TAG, "OnPreExecute called");
        calendarFragment.showProgressBar();
        calendarFragment.getProgressBar().setIndeterminate(false);
        calendarFragment.getProgressBar().setMax(100);
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
        Log.e(TAG, "OnProgressUpdate called: " + (float) (100 * values[0]) + "%");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            calendarFragment.getProgressBar().setProgress((int) (100 * values[0]), true);
        else
            calendarFragment.getProgressBar().setProgress((int) (100 * values[0]));

    }

    @Override
    protected void onPostExecute(CalendarData calendarData) {
        super.onPostExecute(calendarData);
        Log.e(TAG, "OnPostExecute called");
        calendarFragment.setCalendarData(calendarData);
        calendarFragment.hideProgressBar();
    }

    @Override
    protected void onCancelled(CalendarData calendarData) {
        super.onCancelled(calendarData);
        Log.e(TAG, "OnCancelled called");
        calendarFragment.hideProgressBar();
    }

    @SafeVarargs
    @Override
    protected final CalendarData doInBackground(Set<DataSource>... params) {
        if (params.length != 1)
            throw new IllegalStateException("Only one parameter expected!");
        Set<DataSource> param = params[0];

        Map<DataSource, String> downloadMap = new EnumMap<>(DataSource.class);
        try {
            downloadMap = downloadFiles(param);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (isCancelled())
            return null;

        Map<DataSource, MultiValueMap<CalendarDay, VEvent>> results = new EnumMap<>(DataSource.class);
        int i = 0;
        for (DataSource dataSource : param) {
            results.put(dataSource, parseFile(downloadMap.get(dataSource)));
            publishProgress((double) (i++ + 1) / param.size());

            //Break early
            if (isCancelled())
                return new CalendarData(results);
        }
        return new CalendarData(results);
    }

    private Map<DataSource, String> downloadFiles(Set<DataSource> dataSources) throws ExecutionException, InterruptedException {
        Cache cache = requestQueue.getCache();

        Map<DataSource, RequestFuture<String>> requests = new EnumMap<>(DataSource.class);
        Map<DataSource, String> results = new EnumMap<>(DataSource.class);

        for (DataSource dataSource : dataSources) {
            Cache.Entry entry = cache.get(dataSource.calendarURL);
            if (entry != null)
                Log.e(TAG, "Cache of " + dataSource.name + " isExpired: " + entry.isExpired() + " refreshNeeded: " + entry.refreshNeeded() + " responseHeaders: " + entry.responseHeaders);
            if (entry != null && !entry.isExpired()) { //No download needed
                results.put(dataSource, new String(entry.data));
                Log.e(TAG, "Cached " + dataSource.name);
            } else { //Needs to be downloaded
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) { //Hope for download
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(dataSource.calendarURL, future, future);
                    requestQueue.add(request);
                    requests.put(dataSource, future);
                    Log.e(TAG, "Downloading " + dataSource.name);
                } else if (entry != null) { //No hope for download; if cached but out of date, use instead
                    results.put(dataSource, new String(entry.data));
                    Log.e(TAG, "Using old cache due to no Internet connection for " + dataSource.name);
                }
            }
        }

        int i = 0;
        for (Map.Entry<DataSource, RequestFuture<String>> entry : requests.entrySet()) {
            String result = entry.getValue().get();
            if (result != null)
                results.put(entry.getKey(), result);
            else
                Log.e(TAG, "Result was null for " + entry.getKey().name);
            if (isCancelled())
                return results;
            publishProgress((double) (i++ + 1) / dataSources.size());
        }

        return results;
    }
}