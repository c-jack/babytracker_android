package uk.cjack.babytracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = DatabaseHelper.class.getPackage().toString();


    // BABY Table - column names
    public class BabyEntry implements BaseColumns {
        public static final String TABLE_NAME = "baby";
        public static final String BABY_NAME_COL = "babyName";
    }

    // BABY Table - column names
    public class BabyActivityEntry implements BaseColumns {
        public static final String TABLE_NAME = "baby_activity";
        public static final String BABY_COL = "baby";
        public static final String ACTIVITY = "activity";
        public static final String ACTIVITY_DATE_COL = "activityDateTime";
        public static final String ACTIVITY_DATA_COL = "activityData";
    }


    // Table Create Statements
    private static final String CREATE_TABLE_BABY =
            "CREATE TABLE " + BabyEntry.TABLE_NAME + " (" +
                    BabyEntry._ID + " INTEGER PRIMARY KEY," +
                    BabyEntry.BABY_NAME_COL + " TEXT)";

    private static final String CREATE_TABLE_ACTIVITY =
            "CREATE TABLE " + BabyActivityEntry.TABLE_NAME + " ( "
                    + BabyActivityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + BabyActivityEntry.BABY_COL + " INTEGER NOT NULL, "
                    + BabyActivityEntry.ACTIVITY + " TEXT NOT NULL, "
                    + BabyActivityEntry.ACTIVITY_DATE_COL + " TEXT NOT NULL, "
                    + BabyActivityEntry.ACTIVITY_DATA_COL + " TEXT NOT NULL, "
                    + "FOREIGN KEY(" + BabyActivityEntry.BABY_COL
                    + ") REFERENCES " + BabyEntry.TABLE_NAME + " (babyid) ); ";

    public DatabaseHelper( final Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate( final SQLiteDatabase db ) {

        // creating required tables
        db.execSQL( CREATE_TABLE_BABY );
        db.execSQL( CREATE_TABLE_ACTIVITY );
    }

    @Override
    public void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
        // on upgrade drop older tables
        db.execSQL( "DROP TABLE IF EXISTS " + BabyEntry.TABLE_NAME );
        db.execSQL( "DROP TABLE IF EXISTS " + BabyActivityEntry.TABLE_NAME );

        // create new tables
        onCreate( db );
    }

    /**
     * Delete stuff
     * @param table
     * @param where
     * @return
     */
    public boolean delete( final String table, final String where)
    {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table, where, null) > 0;
    }

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