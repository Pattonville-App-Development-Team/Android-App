package org.pattonvillecs.pattonvilleapp.model.calendar;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pattonvillecs.pattonvilleapp.model.AppDatabase;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.pattonvillecs.pattonvilleapp.model.calendar.LiveDataTestUtil.getValue;
import static org.pattonvillecs.pattonvilleapp.model.calendar.PinnedEventMarker.pinned;

/**
 * Created by Mitchell on 10/5/2017.
 */
@RunWith(AndroidJUnit4.class)
public class CalendarDaoTest {
    private CalendarDao calendarDao;
    private AppDatabase appDatabase;

    private static CalendarEvent testEvent() {
        return new CalendarEvent("test_uid", "summary", "location", new Date(), new Date());
    }

    @Before
    public void createDb() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        calendarDao = appDatabase.calendarEventDao();
    }

    @After
    public void closeDb() throws Exception {
        appDatabase.close();
    }

    @Test
    public void writeUnpinnedEventAndReadInList() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarDao.insert(calendarEvent);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarDao.getEvents());

        assertThat(calendarEvents.size(), is(1));
        assertThat(calendarEvents.get(0).calendarEvent.summary, is("summary"));
        assertThat(calendarEvents.get(0).pinned, is(false));
    }

    @Test
    public void writePinnedEventAndReadInList() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarDao.insert(calendarEvent, pinned(calendarEvent));

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarDao.getEvents());

        assertThat(calendarEvents.size(), is(1));
        assertThat(calendarEvents.get(0).calendarEvent.summary, is("summary"));
        assertThat(calendarEvents.get(0).pinned, is(true));
    }
}