package org.pattonvillecs.pattonvilleapp.fragments.calendar.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.HashMultimap;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.lang3.time.StopWatch;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarFragment;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.fix.SerializableCalendarDay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mitchell on 12/24/2016.
 * <p>
 * This class implements the asynchronous parsing of a downloaded iCal file.
 *
 * @author Mitchell Skaggs
 */

@Deprecated
public class CalendarDownloadAndParseTask extends AsyncTask<Set<DataSource>, Double, CalendarData> {

    private static final String TAG = "CalendarDPTask";
    private static final String FILENAME = "calendar_data.bin";
    private final CalendarFragment calendarFragment;
    private final RequestQueue requestQueue;
    private final NetworkInfo activeNetwork;
    private final Kryo kryo;

    public CalendarDownloadAndParseTask(CalendarFragment calendarFragment, RequestQueue requestQueue) {
        this.calendarFragment = calendarFragment;
        this.requestQueue = requestQueue;
        ConnectivityManager cm = (ConnectivityManager) calendarFragment.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        this.kryo = PattonvilleApplication.get(calendarFragment.getActivity()).borrowKryo();
    }

    public static String fixICalStrings(@NonNull String iCalString) {
        return iCalString.replace("FREQ=;", "FREQ=YEARLY;");
    }

    public static HashMultimap<SerializableCalendarDay, VEvent> parseFile(String iCalFile) {
        Log.d(TAG, "Initial");
        HashMultimap<SerializableCalendarDay, VEvent> map = HashMultimap.create();//HashMultimap.multiValueMap(new HashMap<SerializableCalendarDay, HashSet<VEvent>>(), new SetFactories.HashSetVEventFactory());
        StringReader stringReader = new StringReader(fixICalStrings(iCalFile));
        CalendarBuilder calendarBuilder = new CalendarBuilder();
        Calendar calendar = null;
        Log.d(TAG, "Readers done");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
            calendar = calendarBuilder.build(stringReader);
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        Log.d(TAG, "Calendar build time: " + stopWatch.getTime() + "ms");
        Log.d(TAG, Runtime.getRuntime().availableProcessors() + " possible simultaneous tasks with " + Runtime.getRuntime().maxMemory() + " bytes of memory max and " + Runtime.getRuntime().freeMemory() + " bytes of memory free");
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
            Log.d(TAG, "Set built");
            for (VEvent vEvent : vEventSet) {
                Date vEventDate = vEvent.getStartDate().getDate();
                map.put(SerializableCalendarDay.of(CalendarDay.from(vEventDate)), vEvent);
            }
            Log.d(TAG, "Map built");
        }
        return map;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "OnPreExecute called");
        calendarFragment.showProgressBar();
        calendarFragment.getProgressBar().setIndeterminate(false);
        calendarFragment.getProgressBar().setMax(100);
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "OnProgressUpdate called: " + (float) (100 * values[0]) + "%");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            calendarFragment.getProgressBar().setProgress((int) (100 * values[0]), true);
        else
            calendarFragment.getProgressBar().setProgress((int) (100 * values[0]));

    }

    @Override
    protected void onPostExecute(CalendarData calendarData) {
        super.onPostExecute(calendarData);
        Log.d(TAG, "OnPostExecute called");
        calendarFragment.setCalendarData(calendarData);
        calendarFragment.hideProgressBar();
        PattonvilleApplication.get(calendarFragment.getActivity()).releaseKryo(this.kryo);
    }

    @Override
    protected void onCancelled(CalendarData calendarData) {
        super.onCancelled(calendarData);
        Log.d(TAG, "OnCancelled called");
        calendarFragment.hideProgressBar();
        PattonvilleApplication.get(calendarFragment.getActivity()).releaseKryo(this.kryo);
    }

    @SafeVarargs
    @Override
    protected final CalendarData doInBackground(Set<DataSource>... params) {
        if (params.length != 1)
            throw new IllegalStateException("Only one parameter expected!");
        Set<DataSource> param = params[0];
        EnumMap<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> results = new EnumMap<>(DataSource.class);

        File calendarDataCache = new File(calendarFragment.getActivity().getCacheDir(), FILENAME);
        if (calendarDataCache.exists()
                && TimeUnit.HOURS.convert(System.currentTimeMillis() - calendarDataCache.lastModified(), TimeUnit.MILLISECONDS) < 48 //Time before cache refresh
                ) {
            Log.i(TAG, "Loading serialized calendar cache, " + (calendarDataCache.length() / 1024) + " KB, " + (System.currentTimeMillis() - calendarDataCache.lastModified()) + "ms old");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //A cache exists
            FileInputStream fileInputStream = null;
            Input input = null;
            try {
                fileInputStream = new FileInputStream(calendarDataCache);
                input = new Input(fileInputStream);
                CalendarData cachedCalendarData = kryo.readObject(input, CalendarData.class);

                for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : cachedCalendarData.getCalendars().entrySet())
                    if (param.contains(entry.getKey()) && entry.getValue().size() != 0)
                        results.put(entry.getKey(), entry.getValue());

            } catch (FileNotFoundException e) {
                //Proceed to the download if failure
                e.printStackTrace();
            } catch (KryoException | IndexOutOfBoundsException e) {
                //If Kryo can't load the file
                if (calendarDataCache.delete()) {
                    Log.d(TAG, "Corrupted cache file deleted");
                    e.printStackTrace();
                } else {
                    Log.d(TAG, "Failed to delete corrupt cache!");
                    e.printStackTrace();
                }
            } finally { //Prevent any resource leaks
                if (input != null) {
                    try {
                        input.close();
                    } catch (KryoException e) {
                        e.printStackTrace();
                    }
                }
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            stopWatch.stop();
            Log.i(TAG, "Loaded calendar data in " + stopWatch.getTime() + "ms");
        } else {
            Log.i(TAG, "Calendar cache does not exist or is expired, redownloading");
        }

        //Remove duplicate DataSources
        int initialParamSize = param.size(); //Keep the initial value to start the progress bar from
        for (Map.Entry<DataSource, HashMultimap<SerializableCalendarDay, VEvent>> entry : results.entrySet())
            param.remove(entry.getKey());
        int removed = initialParamSize - param.size(); //How many were reused

        //The data that is not cached
        Map<DataSource, String> downloadMap = new EnumMap<>(DataSource.class);
        try {
            downloadMap = downloadFiles(param);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (isCancelled())
            return null;

        int i = removed; //Start mid-way through downloads
        for (DataSource dataSource : param) {
            results.put(dataSource, parseFile(downloadMap.get(dataSource)));
            publishProgress((double) (i++ + 1) / initialParamSize);

            //Break early if cancelled
            if (isCancelled())
                return new CalendarData(results);
        }
        CalendarData calendarData = new CalendarData(results);
        FileOutputStream fileOutputStream = null;
        Output output = null;
        try {
            fileOutputStream = new FileOutputStream(calendarDataCache);
            output = new Output(fileOutputStream);
            kryo.writeObject(output, calendarData);

            if (!calendarDataCache.setLastModified(System.currentTimeMillis()))
                Log.e(TAG, "Failed to set last modified time!");
        } catch (IOException e) {
            Log.e(TAG, "Error writing file!!");
            e.printStackTrace();
        } finally { //Prevent any resource leaks
            if (output != null) {
                try {
                    output.close();
                } catch (KryoException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return calendarData;
    }

    private Map<DataSource, String> downloadFiles(Set<DataSource> dataSources) throws ExecutionException, InterruptedException {
        Cache cache = requestQueue.getCache();

        Map<DataSource, RequestFuture<String>> requests = new EnumMap<>(DataSource.class);
        Map<DataSource, String> results = new EnumMap<>(DataSource.class);

        for (DataSource dataSource : dataSources) {
            Cache.Entry entry = cache.get(dataSource.calendarURL);
            if (entry != null)
                Log.d(TAG, "Cache of " + dataSource.name + " isExpired: " + entry.isExpired() + " refreshNeeded: " + entry.refreshNeeded() + " responseHeaders: " + entry.responseHeaders);
            if (entry != null && !entry.isExpired()) { //No download needed
                results.put(dataSource, new String(entry.data));
                Log.d(TAG, "Cached " + dataSource.name);
            } else { //Needs to be downloaded
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) { //Hope for download
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(dataSource.calendarURL, future, future);
                    requestQueue.add(request);
                    requests.put(dataSource, future);
                    Log.d(TAG, "Downloading " + dataSource.name);
                } else if (entry != null) { //No hope for download; if cached but out of date, use instead
                    results.put(dataSource, new String(entry.data));
                    Log.i(TAG, "Using old cache due to no Internet connection for " + dataSource.name);
                }
            }
        }

        int i = 0;
        for (Map.Entry<DataSource, RequestFuture<String>> entry : requests.entrySet()) {
            String result = entry.getValue().get();
            if (result != null)
                results.put(entry.getKey(), result);
            else
                Log.i(TAG, "Result was null for " + entry.getKey().name);
            if (isCancelled())
                return results;
            publishProgress((double) (i++ + 1) / dataSources.size());
        }

        return results;
    }

}