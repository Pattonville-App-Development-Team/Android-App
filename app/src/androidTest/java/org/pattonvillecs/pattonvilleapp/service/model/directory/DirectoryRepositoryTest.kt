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

package org.pattonvillecs.pattonvilleapp.service.model.directory

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.pattonvillecs.pattonvilleapp.DataSource
import org.pattonvillecs.pattonvilleapp.service.model.calendar.awaitValue
import org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase
import org.pattonvillecs.pattonvilleapp.service.repository.directory.DirectoryRepository

/**
 * Tests adding and removing faculty from an in-memory database.
 *
 * @author Mitchell Skaggs
 * @since 1.3.0
 */
@Suppress("FunctionName")
@RunWith(AndroidJUnit4::class)
@MediumTest
class DirectoryRepositoryTest {
    private lateinit var appDatabase: AppDatabase
    private lateinit var directoryRepository: DirectoryRepository


    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        appDatabase = AppDatabase.init(Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)).build()
        directoryRepository = DirectoryRepository(appDatabase)
    }

    @After
    fun closeDb() {
        appDatabase.close()
    }

    @Test
    fun Given_Faculty_When_UpsertAllCalled_Then_ReturnSameFaculty() {
        val faculty = testFaculty()

        directoryRepository.upsertAll(listOf(faculty))

        val facultyMembers = directoryRepository.getFacultyFromLocations(DataSource.DISTRICT).awaitValue()

        assertThat(facultyMembers, contains(faculty))
    }

    private fun testFaculty(firstName: String = "test_first_name",
                            lastName: String = "test_last_name",
                            pcn: String = "test_pcn",
                            description: String = "test_description",
                            location: DataSource? = DataSource.DISTRICT,
                            email: String? = "test_email",
                            officeNumber1: String? = null,
                            extension1: String? = null,
                            officeNumber2: String? = null,
                            extension2: String? = null,
                            officeNumber3: String? = null,
                            extension3: String? = null): Faculty {
        return Faculty(firstName, lastName, pcn, description, location, email, officeNumber1, extension1, officeNumber2, extension2, officeNumber3, extension3)
    }
}