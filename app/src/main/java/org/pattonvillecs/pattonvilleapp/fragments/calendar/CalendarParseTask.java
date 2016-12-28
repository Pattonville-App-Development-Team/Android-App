package org.pattonvillecs.pattonvilleapp.fragments.calendar;

import android.os.AsyncTask;

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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.pattonvillecs.pattonvilleapp.fragments.calendar.SingleDayEventAdapter.fixICalStrings;

/**
 * Created by Mitchell on 12/24/2016.
 * <p>
 * This class implements the asynchronous parsing of a downloaded iCal file.
 *
 * @author Mitchell Skaggs
 */

public class CalendarParseTask extends AsyncTask<String, Double, List<MultiValueMap<CalendarDay, VEvent>>> {

    public CalendarParseTask() {
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
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<MultiValueMap<CalendarDay, VEvent>> multiValueMaps) {
        super.onPostExecute(multiValueMaps);
    }

    @Override
    protected void onCancelled(List<MultiValueMap<CalendarDay, VEvent>> multiValueMaps) {
        super.onCancelled(multiValueMaps);
    }

    @Override
    protected List<MultiValueMap<CalendarDay, VEvent>> doInBackground(String... params) {
        List<MultiValueMap<CalendarDay, VEvent>> results = new ArrayList<>(params.length);
        for (int i = 0; i < params.length; i++) {
            results.add(parseFile(params[i]));
            publishProgress((double) (i + 1) / params.length);

            //Break early
            if (isCancelled())
                return results;
        }
        return results;
    }
}