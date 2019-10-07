package uk.cjack.babytracker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import uk.cjack.babytracker.database.dao.ActivityDao;
import uk.cjack.babytracker.database.dao.BabyDao;
import uk.cjack.babytracker.database.entities.Activity;
import uk.cjack.babytracker.database.entities.Baby;

@Database(entities = { Baby.class, Activity.class }, version = 1)
public abstract class BabyTrackerDatabase  extends RoomDatabase {

    public abstract BabyDao babyDao();
    public abstract ActivityDao activityDao();

    private static volatile BabyTrackerDatabase INSTANCE;

    public static BabyTrackerDatabase getDatabase( final Context context ) {
        if (INSTANCE == null) {
            synchronized (BabyTrackerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BabyTrackerDatabase.class, "baby_tracker_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}