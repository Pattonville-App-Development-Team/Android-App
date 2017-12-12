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

package org.pattonvillecs.pattonvilleapp.service.model.calendar

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.CalendarEvent
import org.pattonvillecs.pattonvilleapp.service.model.calendar.event.PinnableCalendarEvent
import org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase
import org.pattonvillecs.pattonvilleapp.service.repository.calendar.CalendarRepository
import org.threeten.bp.Instant

/**
 * Tests adding and removing calendar events from an in-memory database.
 *
 * @author Mitchell Skaggs
 * @since 1.2.0
 */
@Suppress("FunctionName")
@RunWith(AndroidJUnit4::class)
@MediumTest
class CalendarRepositoryTest {
    private lateinit var appDatabase: AppDatabase
    private lateinit var calendarRepository: CalendarRepository

    /**
     * Gets a test event
     *
     * @param startEpochMilli the start date
     * @param uid       the uid
     * @return A test event occurring at the given date and ending 10,000ms later, with the given UID
     */
    private fun testEvent(startEpochMilli: Long = 10000, endEpochMilli: Long = startEpochMilli + 10000, uid: String = "test_uid", pinned: Boolean = false, vararg dataSources: DataSource = arrayOf(DataSource.DISTRICT)): PinnableCalendarEvent {
        return PinnableCalendarEvent(
                CalendarEvent(uid, "summary", "location", Instant.ofEpochMilli(startEpochMilli), Instant.ofEpochMilli(endEpochMilli)),
                pinned,
                dataSources.map { DataSourceMarker(uid, it) }.toSet())
    }

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        appDatabase = AppDatabase.init(Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)).build()
        calendarRepository = CalendarRepository(appDatabase)
    }

    @After
    fun closeDb() {
        appDatabase.close()
    }

    @Test
    fun Given_UnpinnedCalendarEvent_When_GetEventsByDataSourceCalled_Then_ReturnSameUnpinnedEvent() {
        val calendarEvent = testEvent(dataSources = *arrayOf(DataSource.DISTRICT))

        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsByDataSource(DataSource.DISTRICT).awaitValue()

        assertThat(calendarEvents, contains(calendarEvent))
    }

    @Test
    fun Given_PinnedCalendarEvent_When_GetEventsByDataSourceCalled_Then_ReturnSamePinnedEvent() {
        val calendarEvent = testEvent(pinned = true, dataSources = *arrayOf(DataSource.DISTRICT))

        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsByDataSource(DataSource.DISTRICT).awaitValue()

        assertThat(calendarEvents, contains(calendarEvent))
    }

    @Test
    fun Given_UnpinnedCalendarEvent_When_GetEventsByUidCalled_Then_ReturnSameUnpinnedEvent() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsByUid(DataSource.DISTRICT, calendarEvent.uid).awaitValue()

        assertThat(calendarEvents, contains(calendarEvent))
    }

    @Test
    fun Given_UnpinnedDuplicateCalendarEvent_When_GetEventsByUidCalled_Then_ReturnSameUnpinnedEvent() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)
        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsByUid(DataSource.DISTRICT, calendarEvent.uid).awaitValue()

        assertThat(calendarEvents, contains(calendarEvent))
    }

    @Test
    fun Given_PinnedCalendarEvent_When_GetEventsByUidCalled_Then_ReturnSamePinnedEvent() {
        val calendarEvent = testEvent(pinned = true)

        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsByUid(DataSource.DISTRICT, calendarEvent.uid).awaitValue()

        assertThat(calendarEvents, contains(calendarEvent))
    }

    @Test
    fun Given_PinnedDuplicateCalendarEvent_When_GetEventsByUidCalled_Then_ReturnSamePinnedEvent() {
        val calendarEvent = testEvent(pinned = true)

        calendarRepository.insertEvent(calendarEvent)
        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsByUid(DataSource.DISTRICT, calendarEvent.uid).awaitValue()

        assertThat(calendarEvents, contains(calendarEvent))
    }

    @Test
    fun Given_UnpinnedCalendarEvent_When_GetDataSourcesCalled_Then_ReturnSameDataSource() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)

        val dataSources = calendarRepository.getDataSources(calendarEvent.uid).awaitValue()

        assertThat(dataSources, containsInAnyOrder(DataSource.DISTRICT))
    }

    @Test
    fun Given_UnpinnedCalendarEventMultipleDataSources_When_GetDataSourcesCalled_Then_ReturnSameDataSources() {
        val calendarEvent = testEvent(dataSources = *arrayOf(DataSource.DISTRICT, DataSource.HIGH_SCHOOL))

        calendarRepository.insertEvent(calendarEvent)

        val dataSources = calendarRepository.getDataSources(calendarEvent.uid).awaitValue()

        assertThat(dataSources, containsInAnyOrder(DataSource.DISTRICT, DataSource.HIGH_SCHOOL))
    }

    @Test
    fun Given_UnpinnedDuplicateCalendarEventMultipleDataSources_When_GetDataSourcesCalled_Then_ReturnSameDataSources() {
        val calendarEvent1 = testEvent()
        val calendarEvent2 = testEvent(dataSources = *arrayOf(DataSource.HIGH_SCHOOL))

        calendarRepository.insertEvent(calendarEvent1)
        calendarRepository.insertEvent(calendarEvent2)

        val dataSources = calendarRepository.getDataSources(calendarEvent1.uid).awaitValue()

        assertThat(dataSources, containsInAnyOrder(DataSource.DISTRICT, DataSource.HIGH_SCHOOL))
    }

    @Test
    fun Given_UnpinnedCalendarEventDuplicateDataSource_When_GetDataSourcesCalled_Then_ReturnSameDataSource() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)
        calendarRepository.insertEvent(calendarEvent)

        val dataSources = calendarRepository.getDataSources(calendarEvent.uid).awaitValue()

        assertThat(dataSources, containsInAnyOrder(DataSource.DISTRICT))
    }

    @Test
    fun Given_UnpinnedCalendarEvent_When_PinEventCalled_And_GetEventsByDataSourceCalled_Then_ReturnPinnedEvent() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)
        calendarRepository.pinEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsByDataSource(DataSource.DISTRICT).awaitValue()

        val expectedCalendarEvent = testEvent(pinned = true)

        assertThat(calendarEvents, contains(expectedCalendarEvent))
    }

    @Test
    fun Given_PinnedCalendarEvent_When_UnpinEventCalled_And_GetEventsByDataSourceCalled_Then_ReturnUnpinnedEvent() {
        val calendarEvent = testEvent(pinned = true)

        calendarRepository.insertEvent(calendarEvent)
        calendarRepository.unpinEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsByDataSource(DataSource.DISTRICT).awaitValue()

        val expectedCalendarEvent = testEvent(pinned = false)

        assertThat(calendarEvents, contains(expectedCalendarEvent))
    }

    @Test
    fun Given_UnpinnedCalendarEvent_When_GetEventsBeforeDateCalled_And_EventIsBeforeDate_Then_ReturnSameEvent() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsBeforeDate(DataSource.DISTRICT, Instant.ofEpochMilli(25000)).awaitValue()

        assertThat(calendarEvents, contains(calendarEvent))
    }

    @Test
    fun Given_UnpinnedCalendarEvent_When_GetEventsBeforeDateCalled_And_EventIsNotBeforeDate_Then_ReturnNothing() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsBeforeDate(DataSource.DISTRICT, Instant.ofEpochMilli(5000)).awaitValue()

        assertThat(calendarEvents, `is`(empty()))
    }

    @Test
    fun Given_UnpinnedCalendarEvent_When_GetEventsAfterDateCalled_And_EventIsAfterDate_Then_ReturnSameEvent() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsAfterDate(DataSource.DISTRICT, Instant.ofEpochMilli(5000)).awaitValue()

        assertThat(calendarEvents, contains(calendarEvent))
    }

    @Test
    fun Given_UnpinnedCalendarEvent_When_GetEventsAfterDateCalled_And_EventIsNotAfterDate_Then_ReturnNothing() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsAfterDate(DataSource.DISTRICT, Instant.ofEpochMilli(25000)).awaitValue()

        assertThat(calendarEvents, `is`(empty()))
    }

    @Test
    fun Given_UnpinnedCalendarEvent_When_GetEventsBetweenDatesCalled_And_EventIsBetweenDates_Then_ReturnSameEvent() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsBetweenDates(DataSource.DISTRICT, Instant.ofEpochMilli(5000), Instant.ofEpochMilli(25000)).awaitValue()

        assertThat(calendarEvents, contains(calendarEvent))
    }

    @Test
    fun Given_UnpinnedCalendarEvent_When_GetEventsBetweenDatesCalled_And_EventIsNotBetweenDates_Then_ReturnNothing() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)

        val calendarEvents = calendarRepository.getEventsBetweenDates(DataSource.DISTRICT, Instant.ofEpochMilli(25000), Instant.ofEpochMilli(45000)).awaitValue()

        assertThat(calendarEvents, `is`(empty()))
    }

    @Test
    fun Given_TwoUnpinnedCalendarEvents_When_GetEventsBetweenDatesCalled_And_EventsAreBetweenDates_Then_ReturnBothSorted() {
        val calendarEventFirst = testEvent(uid = "test_uid_1")
        val calendarEventSecond = testEvent(startEpochMilli = 30000, uid = "test_uid_2")

        calendarRepository.insertEvent(calendarEventSecond)
        calendarRepository.insertEvent(calendarEventFirst)

        val calendarEvents = calendarRepository.getEventsBetweenDates(DataSource.DISTRICT, Instant.ofEpochMilli(10000), Instant.ofEpochMilli(40000)).awaitValue()

        assertThat(calendarEvents, hasSize(2))
        assertThat(calendarEvents, contains(calendarEventFirst, calendarEventSecond))
    }

    @Test
    fun Given_CalendarEvent_When_GetCountOnDaysCalled_Then_OneEntry() {
        val calendarEvent = testEvent()

        calendarRepository.insertEvent(calendarEvent)

        val dates = calendarRepository.getCountOnDays(DataSource.DISTRICT).awaitValue()

        assertThat(dates.entrySet(), hasSize(1)) // One unique item added
        assertThat(dates, hasSize(1)) // One item added total
    }
}