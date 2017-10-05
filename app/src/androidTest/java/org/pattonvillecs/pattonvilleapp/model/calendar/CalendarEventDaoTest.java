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

/**
 * Created by Mitchell on 10/5/2017.
 */
@RunWith(AndroidJUnit4.class)
public class CalendarEventDaoTest {
    private CalendarEventDao calendarEventDao;
    private AppDatabase appDatabase;

    @Before
    public void createDb() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        calendarEventDao = appDatabase.calendarEventDao();

    }

    @After
    public void closeDb() throws Exception {
        appDatabase.close();
    }

    @Test
    public void writeEventAndReadInList() throws Exception {
        CalendarEvent calendarEvent = new CalendarEvent("test_uid", "summary", "location", new Date(), new Date());

        calendarEventDao.insertAll(calendarEvent);

        List<CalendarEvent> calendarEvents = getValue(calendarEventDao.getEventByUid("test_uid"));

        assertThat(calendarEvents.size(), is(1));
        assertThat(calendarEvents.get(0).summary, is("summary"));
    }

    @Test
    public void writeUnpinnedEventAndReadInList() throws Exception {
        CalendarEvent calendarEvent = new CalendarEvent("test_uid", "summary", "location", new Date(), new Date());

        calendarEventDao.insertAll(calendarEvent);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarEventDao.getEvents());

        assertThat(calendarEvents.size(), is(1));
        assertThat(calendarEvents.get(0).calendarEvent.summary, is("summary"));
        assertThat(calendarEvents.get(0).pinned, is(false));
    }

    @Test
    public void writePinnedEventAndReadInList() throws Exception {
        CalendarEvent calendarEvent = new CalendarEvent("test_uid", "summary", "location", new Date(), new Date());

        calendarEventDao.insertAll(calendarEvent);
        calendarEventDao.pinEvent(new PinnedEventMarker("test_uid"));

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarEventDao.getEvents());

        assertThat(calendarEvents.size(), is(1));
        assertThat(calendarEvents.get(0).calendarEvent.summary, is("summary"));
        assertThat(calendarEvents.get(0).pinned, is(true));
    }
}