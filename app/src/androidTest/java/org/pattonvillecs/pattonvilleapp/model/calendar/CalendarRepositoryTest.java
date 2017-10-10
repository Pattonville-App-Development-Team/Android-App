package org.pattonvillecs.pattonvilleapp.model.calendar;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.model.AppDatabase;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.pattonvillecs.pattonvilleapp.model.AppDatabase.init;
import static org.pattonvillecs.pattonvilleapp.model.calendar.LiveDataTestUtil.getValue;

/**
 * Created by Mitchell on 10/5/2017.
 */
@RunWith(AndroidJUnit4.class)
public class CalendarRepositoryTest {
    private AppDatabase appDatabase;
    private CalendarRepository calendarRepository;

    private static CalendarEvent testEvent() {
        return new CalendarEvent("test_uid", "summary", "location", new Date(), new Date());
    }

    @Before
    public void createDb() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = init(Room.inMemoryDatabaseBuilder(context, AppDatabase.class)).build();
        calendarRepository = new CalendarRepository(appDatabase);
    }

    @After
    public void closeDb() throws Exception {
        appDatabase.close();
    }

    @Test
    public void writeUnpinnedEventAndReadInList() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEvents(DataSource.DISTRICT));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, false);

        assertThat(calendarEvents.size(), is(1));
        assertThat(calendarEvents.get(0), is(expectedCalendarEvent));
    }

    @Test
    public void writePinnedEventAndReadInList() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, true, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEvents(DataSource.DISTRICT));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, true);

        assertThat(calendarEvents.size(), is(1));
        assertThat(calendarEvents.get(0), is(expectedCalendarEvent));
    }
}