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

@file:Suppress("ClassName")

package org.pattonvillecs.pattonvilleapp.service.repository

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object MIGRATION_1_2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE faculty ("
                + "first_name TEXT NOT NULL, "
                + "last_name TEXT NOT NULL, "
                + "pcn TEXT NOT NULL, "
                + "description TEXT NOT NULL, "
                + "location INTEGER, "
                + "email TEXT, "
                + "office_number_1 TEXT, "
                + "extension_1 TEXT, "
                + "office_number_2 TEXT, "
                + "extension_2 TEXT, "
                + "office_number_3 TEXT, "
                + "extension_3 TEXT, "
                + "PRIMARY KEY (first_name, last_name, pcn)"
                + ")")
        db.execSQL("CREATE INDEX index_faculty_location "
                + "ON faculty "
                + "(location)")
    }
}