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

package org.pattonvillecs.pattonvilleapp.model.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import org.jetbrains.annotations.NotNull;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.model.AppDatabase;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Collections.singletonList;
import static org.pattonvillecs.pattonvilleapp.model.calendar.DataSourceMarker.dataSource;
import static org.pattonvillecs.pattonvilleapp.model.calendar.PinnedEventMarker.pin;

/**
 * Created by Mitchell on 10/3/2017.
 */

@Singleton
public class CalendarRepository {
    @NonNull
    private final CalendarDao calendarDao;

    @Inject
    public CalendarRepository(@NonNull AppDatabase appDatabase) {
        this.calendarDao = appDatabase.calendarDao();
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsByDataSource(@NonNull List<DataSource> dataSources) {
        return calendarDao.getEventsByDataSources(dataSources);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsByDataSource(@NonNull DataSource dataSource) {
        return calendarDao.getEventsByDataSources(singletonList(dataSource));
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsByUids(@NonNull List<DataSource> dataSources, @NonNull List<String> uids) {
        return calendarDao.getEventsByUids(dataSources, uids);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsByUids(@NonNull DataSource dataSource, @NonNull List<String> uids) {
        return calendarDao.getEventsByUids(singletonList(dataSource), uids);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsByUid(@NonNull List<DataSource> dataSources, @NonNull String uid) {
        return calendarDao.getEventsByUids(dataSources, singletonList(uid));
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsByUid(@NonNull DataSource dataSource, @NonNull String uid) {
        return calendarDao.getEventsByUids(singletonList(dataSource), singletonList(uid));
    }

    @NonNull
    public LiveData<Set<DataSource>> getDataSources(@NonNull List<String> uids) {
        return Transformations.map(calendarDao.getDataSources(uids), EnumSet::copyOf);
    }

    @NonNull
    public LiveData<Set<DataSource>> getDataSources(@NonNull String uid) {
        return Transformations.map(calendarDao.getDataSources(singletonList(uid)), EnumSet::copyOf);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsBeforeDate(@NonNull List<DataSource> dataSources, @NonNull Instant lastDate) {
        return calendarDao.getEventsBeforeDate(dataSources, lastDate);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsBeforeDate(@NonNull DataSource dataSource, @NonNull Instant lastDate) {
        return calendarDao.getEventsBeforeDate(singletonList(dataSource), lastDate);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsAfterDate(@NonNull List<DataSource> dataSources, @NonNull Instant firstDate) {
        return calendarDao.getEventsAfterDate(dataSources, firstDate);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsAfterDate(@NonNull DataSource dataSource, @NonNull Instant firstDate) {
        return calendarDao.getEventsAfterDate(singletonList(dataSource), firstDate);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsBetweenDates(@NonNull List<DataSource> dataSources, @NonNull Instant firstDate, @NonNull Instant lastDate) {
        return calendarDao.getEventsBetweenDates(dataSources, firstDate, lastDate);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsBetweenDates(@NonNull DataSource dataSource, @NonNull Instant firstDate, @NonNull Instant lastDate) {
        return calendarDao.getEventsBetweenDates(singletonList(dataSource), firstDate, lastDate);
    }

    @NonNull
    public LiveData<Multiset<LocalDate>> getCountOnDays(@NonNull DataSource dataSource) {
        return getCountOnDays(singletonList(dataSource));
    }

    @NonNull
    public LiveData<Multiset<LocalDate>> getCountOnDays(@NonNull List<DataSource> dataSources) {
        return Transformations.map(
                calendarDao.getAllInstantsByDataSource(dataSources),
                input -> Stream.of(input)
                        .map(instant -> LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate())
                        .collect(Collectors.toCollection(HashMultiset::create)));
    }

    public void insertEvent(@NonNull CalendarEvent calendarEvent, boolean pinned, List<DataSource> dataSources) {
        if (pinned) {
            calendarDao.insert(calendarEvent, Stream.of(dataSources).map(dataSource -> dataSource(calendarEvent, dataSource)).toList(), pin(calendarEvent));
        } else {
            calendarDao.insert(calendarEvent, Stream.of(dataSources).map(dataSource -> dataSource(calendarEvent, dataSource)).toList());
        }
    }

    public void insertEvent(@NonNull CalendarEvent calendarEvent, boolean pinned, DataSource dataSource) {
        insertEvent(calendarEvent, pinned, singletonList(dataSource));
    }

    public void insertEvent(@NonNull CalendarEvent calendarEvent, List<DataSource> dataSources) {
        insertEvent(calendarEvent, false, dataSources);
    }

    public void insertEvent(@NonNull CalendarEvent calendarEvent, DataSource dataSource) {
        insertEvent(calendarEvent, false, dataSource);
    }

    public void pinEvent(@NonNull CalendarEvent calendarEvent) {
        calendarDao.insertPin(pin(calendarEvent));
    }

    public void unpinEvent(@NonNull CalendarEvent calendarEvent) {
        calendarDao.deletePin(pin(calendarEvent));
    }

    public void insertEventsAndDataSourceMarkers(@NotNull List<CalendarEvent> calendarEvents, @NotNull List<DataSourceMarker> dataSourceMarkers) {
        calendarDao.insertAll(calendarEvents, dataSourceMarkers);
    }
}
