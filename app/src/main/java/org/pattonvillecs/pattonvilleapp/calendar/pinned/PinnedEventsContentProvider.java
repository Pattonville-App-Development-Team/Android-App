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

package org.pattonvillecs.pattonvilleapp.calendar.pinned;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Mitchell Skaggs on 2/23/17.
 * <p>
 * This is a legacy Content Provider that stores users' pinned events. This is only here to allow legacy pinned events to be converted to the new Room database.
 *
 * @see <a href="http://www.grokkingandroid.com/android-tutorial-writing-your-own-content-provider/">Android Tutorial: Writing your own Content Provider | Grokking Android</a>
 * @since 1.0.0
 */

public class PinnedEventsContentProvider extends ContentProvider {
    private static final int PINNED_EVENTS_LIST = 1;
    private static final String TAG = PinnedEventsContentProvider.class.getSimpleName();
    private static UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PinnedEventsContract.AUTHORITY, PinnedEventsContract.PinnedEventsTable.CONTENT_PATH, PINNED_EVENTS_LIST);
    }

    private PinnedEventsSQLiteOpenHelper pinnedEventsSQLiteOpenHelper;
    private volatile boolean isInBatchMode = false;

    @Override
    public boolean onCreate() {
        pinnedEventsSQLiteOpenHelper = new PinnedEventsSQLiteOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.v(TAG, "Querying");
        SQLiteDatabase database = pinnedEventsSQLiteOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (URI_MATCHER.match(uri)) {
            case PINNED_EVENTS_LIST:
                queryBuilder.setTables(PinnedEventsContract.PinnedEventsTable.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Cursor cursor = queryBuilder.query(
                database,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        // if we want to be notified of any changes:
        assert getContext() != null; // Only null in constructor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case PINNED_EVENTS_LIST:
                return PinnedEventsContract.PinnedEventsTable.CONTENT_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.v(TAG, "Inserting");
        long id;
        SQLiteDatabase database = pinnedEventsSQLiteOpenHelper.getWritableDatabase();
        switch (URI_MATCHER.match(uri)) {
            case PINNED_EVENTS_LIST:
                id = database.insert(PinnedEventsContract.PinnedEventsTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Uri not supported! " + uri);
        }
        return getUriForIdAndUpdate(id, uri);
    }

    private Uri getUriForIdAndUpdate(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                // notify all listeners of changes:
                assert getContext() != null; // We are definitely calling *after* onCreate is finished, so the context should be non-null.
                getContext().getContentResolver().notifyChange(itemUri, null);
            }
            return itemUri;
        } else
            // Something went wrong inserting into the database, id is < 0
            throw new SQLException("Problem while inserting into uri: " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.v(TAG, "Deleting");
        SQLiteDatabase db = pinnedEventsSQLiteOpenHelper.getWritableDatabase();
        int delCount;
        switch (URI_MATCHER.match(uri)) {
            case PINNED_EVENTS_LIST:
                delCount = db.delete(
                        PinnedEventsContract.PinnedEventsTable.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // Notify all listeners of changes:
        if (delCount > 0 && !isInBatchMode()) {
            assert getContext() != null; // Not before onCreate, don't need check
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(TAG, "Updating");
        SQLiteDatabase database = pinnedEventsSQLiteOpenHelper.getWritableDatabase();
        int updateCount;
        switch (URI_MATCHER.match(uri)) {
            case PINNED_EVENTS_LIST:
                updateCount = database.update(
                        PinnedEventsContract.PinnedEventsTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // Notify all listeners of changes:
        if (updateCount > 0 && !isInBatchMode()) {
            assert getContext() != null; // Not before onCreate, don't need check
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    public boolean isInBatchMode() {
        return isInBatchMode;
    }
}
