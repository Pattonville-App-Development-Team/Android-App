package org.pattonvillecs.pattonvilleapp.fragments.calendar.data;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.google.common.collect.HashMultimap;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.events.EventFlexibleItem;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Mitchell Skaggs on 2/2/17.
 */
public class CalendarParsingUpdateData {
    public static final int CALENDAR_LISTENER_ID = 1203481279;

    private final ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendarData;
    private final Set<RetrieveCalendarDataAsyncTask> runningCalendarAsyncTasks;

    public CalendarParsingUpdateData(ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendarData, Set<RetrieveCalendarDataAsyncTask> runningCalendarAsyncTasks) {
        this.calendarData = calendarData;
        this.runningCalendarAsyncTasks = runningCalendarAsyncTasks;
    }

    public static List<EventFlexibleItem> getAllEvents(ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> calendarData) {
        return Stream.of(calendarData.entrySet())
                .flatMap(new Function<Map.Entry<DataSource, HashMultimap<CalendarDay, VEvent>>, Stream<Pair<DataSource, VEvent>>>() {
                    @Override
                    public Stream<Pair<DataSource, VEvent>> apply(final Map.Entry<DataSource, HashMultimap<CalendarDay, VEvent>> dataSourceHashMultimapEntry) {
                        return Stream.of(dataSourceHashMultimapEntry.getValue().entries()).map(new Function<Map.Entry<CalendarDay, VEvent>, Pair<DataSource, VEvent>>() {
                            @Override
                            public Pair<DataSource, VEvent> apply(Map.Entry<CalendarDay, VEvent> calendarDayVEventEntry) {
                                return new ImmutablePair<>(dataSourceHashMultimapEntry.getKey(), calendarDayVEventEntry.getValue());
                            }
                        });
                    }
                })
                .sorted(new Comparator<Pair<DataSource, VEvent>>() {
                    @Override
                    public int compare(Pair<DataSource, VEvent> o1, Pair<DataSource, VEvent> o2) {
                        //This is Google Calendar style scrolling: future events to the bottom
                        return o1.getValue().getStartDate().getDate().compareTo(o2.getValue().getStartDate().getDate());
                    }
                })
                .map(new Function<Pair<DataSource, VEvent>, EventFlexibleItem>() {
                    @Override
                    public EventFlexibleItem apply(Pair<DataSource, VEvent> dataSourceVEventPair) {
                        return new EventFlexibleItem(dataSourceVEventPair.getKey(), dataSourceVEventPair.getValue());
                    }
                })
                .collect(Collectors.<EventFlexibleItem>toList());
    }

    public ConcurrentMap<DataSource, HashMultimap<CalendarDay, VEvent>> getCalendarData() {
        return calendarData;
    }

    public Set<RetrieveCalendarDataAsyncTask> getRunningCalendarAsyncTasks() {
        return runningCalendarAsyncTasks;
    }
}
