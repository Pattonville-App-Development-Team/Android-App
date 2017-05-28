package org.pattonvillecs.pattonvilleapp.calendar.pinned;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mitchell Skaggs on 2/23/17.
 */

public class PinnedEventsSQLiteOpenHelper extends SQLiteOpenHelper {

    public PinnedEventsSQLiteOpenHelper(Context context) {
        super(context, "name", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PinnedEventsContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PinnedEventsContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
