package uk.cjack.babytracker.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Date;

public class DatabaseHelper {

    /**
     * Convert milliseconds to a Date
     *
     * @param millis
     * @return
     */
    public static Date millisToDate( final long millis ) {
        return new Date( millis );
    }
}