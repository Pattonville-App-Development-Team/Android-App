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

import android.arch.persistence.db.SimpleSQLiteQuery
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.testing.MigrationTestHelper
import android.content.ContentValues
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase
import org.pattonvillecs.pattonvilleapp.service.repository.MIGRATION_1_2

/**
 * This class tests all the migrations of the app's database.
 *
 * @author Mitchell Skaggs
 * @since 1.3.0
 */

@Suppress("FunctionName")
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
        assert.that(eventCursor.isLast, equalTo(true))
        assert.that(eventCursor.getString(0), equalTo("test_uid"))
        assert.that(eventCursor.getString(1), equalTo("test_summary"))
        assert.that(eventCursor.getString(2), equalTo("test_location"))
        assert.that(eventCursor.getLong(3), equalTo(0L))
        assert.that(eventCursor.getLong(4), equalTo(1L))

        val datasourceCursor = db.query(SimpleSQLiteQuery("SELECT * FROM datasource_markers WHERE uid=?", arrayOf("test_uid")))
        datasourceCursor.moveToNext()
        assert.that(datasourceCursor.isLast, equalTo(true))
        assert.that(datasourceCursor.getString(0), equalTo("test_uid"))
        assert.that(datasourceCursor.getInt(1), equalTo(0))

        val pinnedEventMarkerCursor = db.query(SimpleSQLiteQuery("SELECT * FROM pinned_event_markers WHERE uid=?", arrayOf("test_uid")))
        pinnedEventMarkerCursor.moveToNext()
        assert.that(pinnedEventMarkerCursor.isLast, equalTo(true))
        assert.that(pinnedEventMarkerCursor.getString(0), equalTo("test_uid"))
    }

    companion object {
        private const val TEST_DB_NAME = "test-app-database"
    }
}
