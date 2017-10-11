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
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
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
    public void Given_UnpinnedCalendarEvent_When_GetEventsByDataSourceCalled_Return_SameUnpinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByDataSource(DataSource.DISTRICT));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, false);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_PinnedCalendarEvent_When_GetEventsByDataSourceCalled_Return_SamePinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, true, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByDataSource(DataSource.DISTRICT));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, true);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_GetEventsByUidCalled_Return_SameUnpinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByUid("test_uid"));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, false);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_PinnedCalendarEvent_When_GetEventsByUidCalled_Return_SamePinnedEvent() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, true, DataSource.DISTRICT);

        List<PinnableCalendarEvent> calendarEvents = getValue(calendarRepository.getEventsByUid("test_uid"));

        PinnableCalendarEvent expectedCalendarEvent = new PinnableCalendarEvent(calendarEvent, true);

        assertThat(calendarEvents, contains(expectedCalendarEvent));
    }

    @Test
    public void Given_UnpinnedCalendarEvent_When_GetDataSourcesCalled_Return_SameDataSources() throws Exception {
        CalendarEvent calendarEvent = testEvent();

        calendarRepository.insertEvent(calendarEvent, DataSource.DISTRICT);

        Set<DataSource> dataSources = getValue(calendarRepository.getDataSources("test_uid"));

        assertThat(dataSources, containsInAnyOrder(DataSource.DISTRICT));
    }
}