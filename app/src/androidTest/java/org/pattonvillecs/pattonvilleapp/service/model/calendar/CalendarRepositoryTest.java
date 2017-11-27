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

package org.pattonvillecs.pattonvilleapp.service.model.calendar;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.Multiset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.CalendarEvent;
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.PinnableCalendarEvent;
import org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase;
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.pattonvillecs.pattonvilleapp.service.model.calendar.LiveDataTestUtil.getValue;
import static org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase.init;

/**
 * Created by Mitchell on 10/5/2017.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class CalendarRepositoryTest {
    private AppDatabase appDatabase;
    private CalendarRepository calendarRepository;

    /**
     * Gets a test event.
     *
     * @return A test event occurring at 10,000ms UTC and ending at 20,000ms UTC
     */
    private static CalendarEvent testEvent() {
        return testEvent(Instant.ofEpochMilli(10000));
    }

    /**
     * Gets a test event.
     *
     * @param startDate the start date
     * @return A test event occurring at the given date and ending 10,000ms later
     */
    private static CalendarEvent testEvent(@NonNull Instant startDate) {
        return testEvent(startDate, "test_uid");
    }

    /**
     * Gets a test event
     *
     * @param startDate the start date
     * @param uid       the uid
     * @return A test event occurring at the given date and ending 10,000ms later, with the given UID
     */
    private static CalendarEvent testEvent(@NonNull Instant startDate, @NonNull String uid) {
        return new CalendarEvent(uid, "summary", "location", startDate, startDate.plusMillis(10000));
    }

    /**
     * Gets a test event
     *
     * @param uid the uid
     * @return A test event with the given UID
     */
    private static CalendarEvent testEvent(@NonNull String uid) {
        return testEvent(Instant.ofEpochMilli(10000), uid);
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
    public void Given_UnpinnedCalendarEvent_When_GetEventsByDataSourceCalled_Then_ReturnSameUnpinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByDataSource(DataSource.DISTRICT));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, false);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_PinnedCalendarEvent_When_GetEventsByDataSourceCalled_Then_ReturnSamePinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, true, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByDataSource(DataSource.DISTRICT));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, true);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_GetEventsByUidCalled_Then_ReturnSameUnpinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByUid(DataSource.DISTRICT, calendarEvent.getUid()));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, false);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_UnpinnedDuplicateCalendarEvent_When_GetEventsByUidCalled_Then_ReturnSameUnpinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);
        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByUid(DataSource.DISTRICT, calendarEvent.getUid()));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, false);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_PinnedCalendarEvent_When_GetEventsByUidCalled_Then_ReturnSamePinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, true, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByUid(DataSource.DISTRICT, calendarEvent.getUid()));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, true);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_PinnedDuplicateCalendarEvent_When_GetEventsByUidCalled_Then_ReturnSamePinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, true, DataSource.DISTRICT);
        calendarRepository.insertEvent(calendarEvent, true, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByUid(DataSource.DISTRICT, calendarEvent.getUid()));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, true);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_GetDataSourcesCalled_Then_ReturnSameDataSource() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        Set<DataSource> dataSources = getValue(calendarRepository.getDataSources(calendarEvent.getUid()));

        assertThat(dataSources, containsInAnyOrder(DataSource.DISTRICT));
    }

    @Test
    public void Given_UnpinnedCalendarEventMultipleDataSources_When_GetDataSourcesCalled_Then_ReturnSameDataSources() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, Arrays.asList(DataSource.DISTRICT, DataSource.HIGH_SCHOOL));

        Set<DataSource> dataSources = getValue(calendarRepository.getDataSources(calendarEvent.getUid()));

        assertThat(dataSources, containsInAnyOrder(DataSource.DISTRICT, DataSource.HIGH_SCHOOL));
    }

    @Test
    public void Given_UnpinnedDuplicateCalendarEventMultipleDataSources_When_GetDataSourcesCalled_Then_ReturnSameDataSources() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);
        calendarRepository.insertEvent(calendarEvent, DataSource.HIGH_SCHOOL);

        Set<DataSource> dataSources = getValue(calendarRepository.getDataSources(calendarEvent.getUid()));

        assertThat(dataSources, containsInAnyOrder(DataSource.DISTRICT, DataSource.HIGH_SCHOOL));
    }

    @Test
    public void Given_UnpinnedCalendarEventDuplicateDataSource_When_GetDataSourcesCalled_Then_ReturnSameDataSource() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);
        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        Set<DataSource> dataSources = getValue(calendarRepository.getDataSources(calendarEvent.getUid()));

        assertThat(dataSources, containsInAnyOrder(DataSource.DISTRICT));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_PinEventCalled_And_GetEventsByDataSourceCalled_Then_ReturnPinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);
        calendarRepository.pinEvent(calendarEvent);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByDataSource(DataSource.DISTRICT));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, true);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_PinnedCalendarEvent_When_UnpinEventCalled_And_GetEventsByDataSourceCalled_Then_ReturnUnpinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, true, DataSource.DISTRICT);
        calendarRepository.unpinEvent(calendarEvent);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByDataSource(DataSource.DISTRICT));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, false);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_GetEventsBeforeDateCalled_And_EventIsBeforeDate_Then_ReturnSameEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsBeforeDate(DataSource.DISTRICT, Instant.ofEpochMilli(25000)));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, false);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_GetEventsBeforeDateCalled_And_EventIsNotBeforeDate_Then_ReturnNothing() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsBeforeDate(DataSource.DISTRICT, Instant.ofEpochMilli(5000)));

        assertThat(calendarEvents, is(empty()));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_GetEventsAfterDateCalled_And_EventIsAfterDate_Then_ReturnSameEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsAfterDate(DataSource.DISTRICT, Instant.ofEpochMilli(5000)));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, false);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_GetEventsAfterDateCalled_And_EventIsNotAfterDate_Then_ReturnNothing() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsAfterDate(DataSource.DISTRICT, Instant.ofEpochMilli(25000)));

        assertThat(calendarEvents, is(empty()));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_GetEventsBetweenDatesCalled_And_EventIsBetweenDates_Then_ReturnSameEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsBetweenDates(DataSource.DISTRICT, Instant.ofEpochMilli(5000), Instant.ofEpochMilli(25000)));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, false);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_GetEventsBetweenDatesCalled_And_EventIsNotBetweenDates_Then_ReturnNothing() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsBetweenDates(DataSource.DISTRICT, Instant.ofEpochMilli(25000), Instant.ofEpochMilli(45000)));

        assertThat(calendarEvents, is(empty()));
    }

    @Test
    public void Given_TwoUnpinnedCalendarEvents_When_GetEventsBetweenDatesCalled_And_EventsAreBetweenDates_Then_ReturnBothSorted() throws Exception {
        CalendarEvent calendarEventFirst = testEvent("test_uid_1");
        CalendarEvent calendarEventSecond = testEvent(Instant.ofEpochMilli(30000), "test_uid_2");

        calendarRepository.insertEvent(calendarEventSecond, DataSource.DISTRICT);
        calendarRepository.insertEvent(calendarEventFirst, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsBetweenDates(DataSource.DISTRICT, Instant.ofEpochMilli(10000), Instant.ofEpochMilli(40000)));

        PinnableCalendarEvent pinnableCalendarEventFirst = new PinnableCalendarEvent(calendarEventFirst, false);
        PinnableCalendarEvent pinnableCalendarEventSecond = new PinnableCalendarEvent(calendarEventSecond, false);

        assertThat(calendarEvents, hasSize(2));
        assertThat(calendarEvents, contains(pinnableCalendarEventFirst, pinnableCalendarEventSecond));
    }

    @Test
    public void Given_CalendarEvent_When_GetCountOnDaysCalled_Then_OneEntry() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        Multiset<LocalDate> dates = getValue(calendarRepository.getCountOnDays(DataSource.DISTRICT));

        assertThat(dates.entrySet(), is(1)); // One unique item added
        assertThat(dates, hasSize(1)); // One item added
    }
}