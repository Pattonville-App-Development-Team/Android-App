package org.pattonvillecs.pattonvilleapp.model;

import android.arch.lifecycle.LiveData;

import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarEvent;
import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarEventDao;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Mitchell on 10/3/2017.
 */

@Singleton
public class PinnedEventRepository {
    private final CalendarEventDao calendarEventDao;

    @Inject
    PinnedEventRepository(CalendarEventDao calendarEventDao) {
        this.calendarEventDao = calendarEventDao;
    }

    public void addPinnedEvents(CalendarEvent... calendarEvents) {
        calendarEventDao.insertAll(calendarEvents);
    }

    public void removePinnedEvent(CalendarEvent calendarEvent) {
        calendarEventDao.delete(calendarEvent);
    }

    public LiveData<Boolean> containsPinnedEvent(CalendarEvent calendarEvent) {
        return null;//Transformations.map(calendarEventDao.getPinnedEventsByUid(calendarEvent.uid), pinnedEvents -> pinnedEvents.size() != 0);
    }
}
