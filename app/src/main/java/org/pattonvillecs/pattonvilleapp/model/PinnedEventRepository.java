package org.pattonvillecs.pattonvilleapp.model;

import org.pattonvillecs.pattonvilleapp.model.calendar.CalendarDao;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Mitchell on 10/3/2017.
 */

@Singleton
public class PinnedEventRepository {
    private final CalendarDao calendarDao;

    @Inject
    PinnedEventRepository(CalendarDao calendarDao) {
        this.calendarDao = calendarDao;
    }
}
