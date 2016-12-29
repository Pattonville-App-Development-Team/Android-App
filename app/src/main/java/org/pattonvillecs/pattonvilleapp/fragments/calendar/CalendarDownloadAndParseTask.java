package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.os.AsyncTask;
import android.util.Log;

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

    public CalendarDownloadAndParseTask(CalendarFragment calendarFragment, RequestQueue requestQueue) {
        this.calendarFragment = calendarFragment;
        this.requestQueue = requestQueue;
    }

    private static String fixICalStrings(String iCalString) {
        return iCalString.replace("FREQ=;", "FREQ=YEARLY;");
    }

    private MultiValueMap<CalendarDay, VEvent> parseFile(String iCalFile) {
        MultiValueMap<CalendarDay, VEvent> map = MultiValueMap.multiValueMap(new HashMap<CalendarDay, HashSet<VEvent>>(), new Factory<HashSet<VEvent>>() {
            @Override
            public HashSet<VEvent> create() {
                return new HashSet<>();
            }
        });
        StringReader stringReader = new StringReader(fixICalStrings(iCalFile));
        CalendarBuilder calendarBuilder = new CalendarBuilder();
        Calendar calendar = null;
        try {
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
            calendar = calendarBuilder.build(stringReader);
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }
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
            for (VEvent vEvent : vEventSet) {
                Date vEventDate = vEvent.getStartDate().getDate();
                map.put(CalendarDay.from(vEventDate), vEvent);
            }
        }
        return map;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e(TAG, "OnPreExecute called");
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
        Log.e(TAG, "OnProgressUpdate called: " + (float) (100 * values[0]) + "%");
    }

    @Override
    protected void onPostExecute(CalendarData calendarData) {
        super.onPostExecute(calendarData);
        Log.e(TAG, "OnPostExecute called");
        calendarFragment.setCalendarData(calendarData);
    }

    @Override
    protected void onCancelled(CalendarData calendarData) {
        super.onCancelled(calendarData);
        Log.e(TAG, "OnCancelled called");
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
        Map<DataSource, RequestFuture<String>> requests = new EnumMap<>(DataSource.class);
        for (DataSource dataSource : dataSources) {
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(dataSource.calendarURL, future, future);
            requestQueue.add(request);
            requests.put(dataSource, future);
        }

        int i = 0;
        Map<DataSource, String> results = new EnumMap<>(DataSource.class);
        for (Map.Entry<DataSource, RequestFuture<String>> entry : requests.entrySet()) {
            results.put(entry.getKey(), entry.getValue().get());
            if (isCancelled())
                return results;
            publishProgress((double) (i++ + 1) / dataSources.size());
        }

        return results;
    }
}