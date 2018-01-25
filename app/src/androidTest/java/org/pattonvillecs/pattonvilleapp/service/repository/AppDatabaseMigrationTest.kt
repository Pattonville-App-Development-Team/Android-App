/*
 * Copyright (C) 2017 - 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.service.repository

import android.arch.persistence.db.SimpleSQLiteQuery
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.testing.MigrationTestHelper
import android.content.ContentValues
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.should.shouldMatch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This class tests all the migrations of the app's database.
 *
 * @author Mitchell Skaggs
 * @since 1.3.0
 */

@Suppress("TestFunctionName")
@RunWith(AndroidJUnit4::class)
@LargeTest
class AppDatabaseMigrationTest {

    @Rule
    @JvmField
    var testHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    @Test
    fun Given_Version1_When_Migration_1_2Called_Return_CorrectData() {
        var db = testHelper.createDatabase(TEST_DB_NAME, 1)

        db.insert("events",
                OnConflictStrategy.FAIL,
                ContentValues().apply {
                    put("uid", "test_uid")
                    put("summary", "test_summary")
                    put("location", "test_location")
                    put("start_date", 0L)
                    put("end_date", 1L)
                })
        db.insert("datasource_markers",
                OnConflictStrategy.FAIL,
                ContentValues().apply {
                    put("uid", "test_uid")
                    put("datasource", 0)
                })
        db.insert("pinned_event_markers",
                OnConflictStrategy.FAIL,
                ContentValues().apply {
                    put("uid", "test_uid")
                })

        db = testHelper.runMigrationsAndValidate(TEST_DB_NAME, 2, true, MIGRATION_1_2)

        val eventCursor = db.query(SimpleSQLiteQuery("SELECT * FROM events WHERE uid=?", arrayOf("test_uid")))
        eventCursor.moveToNext()
        eventCursor.isLast shouldMatch equalTo(true)
        eventCursor.getString(0) shouldMatch equalTo("test_uid")
        eventCursor.getString(1) shouldMatch equalTo("test_summary")
        eventCursor.getString(2) shouldMatch equalTo("test_location")
        eventCursor.getLong(3) shouldMatch equalTo(0L)
        eventCursor.getLong(4) shouldMatch equalTo(1L)

        val datasourceCursor = db.query(SimpleSQLiteQuery("SELECT * FROM datasource_markers WHERE uid=?", arrayOf("test_uid")))
        datasourceCursor.moveToNext()
        datasourceCursor.isLast shouldMatch equalTo(true)
        datasourceCursor.getString(0) shouldMatch equalTo("test_uid")
        datasourceCursor.getInt(1) shouldMatch equalTo(0)

        val pinnedEventMarkerCursor = db.query(SimpleSQLiteQuery("SELECT * FROM pinned_event_markers WHERE uid=?", arrayOf("test_uid")))
        pinnedEventMarkerCursor.moveToNext()
        pinnedEventMarkerCursor.isLast shouldMatch equalTo(true)
        pinnedEventMarkerCursor.getString(0) shouldMatch equalTo("test_uid")
    }

    @Test
    fun Given_Version2_When_Migration_2_3Called_Return_CorrectData() {
        var db = testHelper.createDatabase(TEST_DB_NAME, 2)

        db.insert("events",
                OnConflictStrategy.FAIL,
                ContentValues().apply {
                    put("uid", "test_uid")
                    put("summary", "test_summary")
                    put("location", "test_location")
                    put("start_date", 0L)
                    put("end_date", 1L)
                })
        db.insert("datasource_markers",
                OnConflictStrategy.FAIL,
                ContentValues().apply {
                    put("uid", "test_uid")
                    put("datasource", 0)
                })
        db.insert("pinned_event_markers",
                OnConflictStrategy.FAIL,
                ContentValues().apply {
                    put("uid", "test_uid")
                })
        db.insert("faculty",
                OnConflictStrategy.FAIL,
                ContentValues().apply {
                    put("first_name", "test_first_name")
                    put("last_name", "test_last_name")
                    put("pcn", "test_pcn")
                    put("description", "test_description")
                    put("location", 0)
                    put("email", "test_email")
                    put("office_number_1", "test_office_number_1")
                    put("extension_1", "test_extension_1")
                    put("office_number_2", "test_office_number_2")
                    put("extension_2", "test_extension_2")
                    put("office_number_3", "test_office_number_3")
                    put("extension_3", "test_extension_3")
                })

        db = testHelper.runMigrationsAndValidate(TEST_DB_NAME, 3, true, MIGRATION_2_3)

        val eventCursor = db.query(SimpleSQLiteQuery("SELECT * FROM events WHERE uid=?", arrayOf("test_uid")))
        eventCursor.moveToNext()
        eventCursor.isLast shouldMatch equalTo(true)
        eventCursor.getString(0) shouldMatch equalTo("test_uid")
        eventCursor.getString(1) shouldMatch equalTo("test_summary")
        eventCursor.getString(2) shouldMatch equalTo("test_location")
        eventCursor.getLong(3) shouldMatch equalTo(0L)
        eventCursor.getLong(4) shouldMatch equalTo(1L)

        val datasourceCursor = db.query(SimpleSQLiteQuery("SELECT * FROM datasource_markers WHERE uid=?", arrayOf("test_uid")))
        datasourceCursor.moveToNext()
        datasourceCursor.isLast shouldMatch equalTo(true)
        datasourceCursor.getString(0) shouldMatch equalTo("test_uid")
        datasourceCursor.getInt(1) shouldMatch equalTo(0)

        val pinnedEventMarkerCursor = db.query(SimpleSQLiteQuery("SELECT * FROM pinned_event_markers WHERE uid=?", arrayOf("test_uid")))
        pinnedEventMarkerCursor.moveToNext()
        pinnedEventMarkerCursor.isLast shouldMatch equalTo(true)
        pinnedEventMarkerCursor.getString(0) shouldMatch equalTo("test_uid")

        val facultyCursor = db.query(SimpleSQLiteQuery("SELECT * FROM faculty WHERE pcn=?", arrayOf("test_pcn")))
        facultyCursor.moveToNext()
        facultyCursor.isLast shouldMatch equalTo(true)
        facultyCursor.getString(0) shouldMatch equalTo("test_first_name")
        facultyCursor.getString(1) shouldMatch equalTo("test_last_name")
        facultyCursor.getString(2) shouldMatch equalTo("test_pcn")
        facultyCursor.getString(3) shouldMatch equalTo("test_description")
        facultyCursor.getInt(4) shouldMatch equalTo(0)
        facultyCursor.getString(5) shouldMatch equalTo("test_email")
        facultyCursor.getString(6) shouldMatch equalTo("test_office_number_1")
        facultyCursor.getString(7) shouldMatch equalTo("test_extension_1")
        facultyCursor.getString(8) shouldMatch equalTo("test_office_number_2")
        facultyCursor.getString(9) shouldMatch equalTo("test_extension_2")
        facultyCursor.getString(10) shouldMatch equalTo("test_office_number_3")
        facultyCursor.getString(11) shouldMatch equalTo("test_extension_3")
    }

    companion object {
        private const val TEST_DB_NAME = "test-app-database"
    }
}
