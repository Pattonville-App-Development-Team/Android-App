package org.pattonvillecs.pattonvilleapp.model.calendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.model.AppDatabase;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.pattonvillecs.pattonvilleapp.model.calendar.DataSourceMarker.dataSource;
import static org.pattonvillecs.pattonvilleapp.model.calendar.PinnedEventMarker.pinned;

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
        return calendarDao.getEventsByDataSource(dataSources);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsByDataSource(@NonNull DataSource dataSource) {
        return calendarDao.getEventsByDataSource(dataSource);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsByUid(@NonNull List<String> uids) {
        return calendarDao.getEventsByUid(uids);
    }

    @NonNull
    public LiveData<List<PinnableCalendarEvent>> getEventsByUid(@NonNull String uid) {
        return calendarDao.getEventsByUid(uid);
    }

    @NonNull
    public LiveData<Set<DataSource>> getDataSources(@NonNull List<String> uids) {
        return Transformations.map(calendarDao.getDataSources(uids), EnumSet::copyOf);
    }

    @NonNull
    public LiveData<Set<DataSource>> getDataSources(@NonNull String uid) {
        return Transformations.map(calendarDao.getDataSources(uid), EnumSet::copyOf);
    }

    public void insertEvent(@NonNull CalendarEvent calendarEvent, boolean pinned, List<DataSource> dataSources) {
        if (pinned) {
            calendarDao.insert(calendarEvent, Stream.of(dataSources).map(dataSource -> dataSource(calendarEvent, dataSource)).toList(), pinned(calendarEvent));
        } else {
            calendarDao.insert(calendarEvent, Stream.of(dataSources).map(dataSource -> dataSource(calendarEvent, dataSource)).toList());
        }
    }

    public void insertEvent(@NonNull CalendarEvent calendarEvent, boolean pinned, DataSource dataSource) {
        if (pinned) {
            calendarDao.insert(calendarEvent, dataSource(calendarEvent, dataSource), pinned(calendarEvent));
        } else {
            calendarDao.insert(calendarEvent, dataSource(calendarEvent, dataSource));
        }
    }

    public void insertEvent(@NonNull CalendarEvent calendarEvent, List<DataSource> dataSources) {
        insertEvent(calendarEvent, false, dataSources);
    }

    public void insertEvent(@NonNull CalendarEvent calendarEvent, DataSource dataSource) {
        insertEvent(calendarEvent, false, dataSource);
    }
}
