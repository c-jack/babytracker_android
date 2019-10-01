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
    public class BabyFeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "baby_feeds";
        public static final String BABY_COL = "baby";
        public static final String FEED_DATE_COL = "feedDateTime";
        public static final String FEED_AMOUNT_COL = "feedAmount";
    }


    // Table Create Statements
    private static final String CREATE_TABLE_BABY =
            "CREATE TABLE " + BabyEntry.TABLE_NAME + " (" +
                    BabyEntry._ID + " INTEGER PRIMARY KEY," +
                    BabyEntry.BABY_NAME_COL + " TEXT)";

    private static final String CREATE_TABLE_FEEDS =
            "CREATE TABLE " + BabyFeedEntry.TABLE_NAME + " ( "
                    + BabyFeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + BabyFeedEntry.BABY_COL + " INTEGER NOT NULL, "
                    + BabyFeedEntry.FEED_DATE_COL + " INTEGER NOT NULL, "
                    + BabyFeedEntry.FEED_AMOUNT_COL + " INTEGER NOT NULL, "
                    + "FOREIGN KEY(" + BabyFeedEntry.BABY_COL
                    + ") REFERENCES " + BabyEntry.TABLE_NAME + " (babyid) ); ";

    public DatabaseHelper( final Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate( final SQLiteDatabase db ) {

        // creating required tables
        db.execSQL( CREATE_TABLE_BABY );
        db.execSQL( CREATE_TABLE_FEEDS );
    }

    @Override
    public void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
        // on upgrade drop older tables
        db.execSQL( "DROP TABLE IF EXISTS " + BabyEntry.TABLE_NAME );
        db.execSQL( "DROP TABLE IF EXISTS " + BabyFeedEntry.TABLE_NAME );

        // create new tables
        onCreate( db );
    }

    /**
     * Convert milliseconds to a Date
     *
     * @param millis
     * @return
     */
    public static Date millisToDate( int millis ) {
        return new Date( millis );
    }
}