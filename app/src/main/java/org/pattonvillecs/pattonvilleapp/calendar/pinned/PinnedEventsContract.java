/*
 * Copyright (C) 2017  Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, and Nathan Skelton
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

package org.pattonvillecs.pattonvilleapp.calendar.pinned;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import org.pattonvillecs.pattonvilleapp.BuildConfig;

/**
 * Created by Mitchell Skaggs on 2/23/17.
 */

public final class PinnedEventsContract {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".calendar.pinned";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + PinnedEventsTable.TABLE_NAME +
            " (" + PinnedEventsTable._ID + " INTEGER PRIMARY KEY," + PinnedEventsTable.COLUMN_NAME_UID + " INTEGER)";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PinnedEventsTable.TABLE_NAME;

    private PinnedEventsContract() {
    }

    public static class PinnedEventsTable implements BaseColumns {
        public static final String CONTENT_PATH = "events";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(PinnedEventsContract.CONTENT_URI, CONTENT_PATH);
        public static final String TABLE_NAME = "pinned_events";
        public static final String COLUMN_NAME_UID = "pinned_event_uid";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + PinnedEventsContract.AUTHORITY + "." + CONTENT_PATH; // vnd means vendor-created (Vendor == Pattonville CS) MIME type, i.e. not http, text, audio, video, etc.
    }
}
