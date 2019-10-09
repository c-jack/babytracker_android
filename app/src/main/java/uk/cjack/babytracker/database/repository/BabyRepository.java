package uk.cjack.babytracker.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import uk.cjack.babytracker.database.BabyTrackerDatabase;
import uk.cjack.babytracker.database.dao.BabyDao;
import uk.cjack.babytracker.database.entities.Baby;
import uk.cjack.babytracker.database.enums.DatabaseActionEnum;

public class BabyRepository {

    private final BabyDao mBabyDao;
    private final LiveData<List<Baby>> mAllBabies;

    /**
     * Constructor
     *
     * @param application application
     */
    public BabyRepository( final Application application ) {
        final BabyTrackerDatabase db = BabyTrackerDatabase.getDatabase( application );
        mBabyDao = db.babyDao();
        mAllBabies = mBabyDao.getAllBabies();
    }

    /*
     * Returns a specific baby
     */
    public Baby getBaby( final int babyId) {
        try {
            return (Baby) new BabyAsyncTask( mBabyDao, DatabaseActionEnum.GET ).execute( babyId ).get();
        }
        catch ( ExecutionException | InterruptedException e ) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return a list of all Babies in the DB
     */
    public LiveData<List<Baby>> getAllBabies() {
        return mAllBabies;
    }

    /**
     * Performs an INSERT query in a background thread
     *
     * @param baby the {@link Baby} object to insert
     */
    public void insert( final Baby baby ) {
        new BabyAsyncTask( mBabyDao, DatabaseActionEnum.INSERT ).execute( baby );
    }

    /**
     * Performs an UPDATE query in a background thread
     *
     * @param baby the {@link Baby} object to insert
     */
    public void update( final Baby baby ) {
        new BabyAsyncTask( mBabyDao, DatabaseActionEnum.UPDATE ).execute( baby );
    }

    /**
     * Performs an DELETE query in a background thread
     *
     * @param baby the {@link Baby} object to insert
     */
    public void delete( final Baby baby ) {
        new BabyAsyncTask( mBabyDao, DatabaseActionEnum.DELETE ).execute( baby );
    }
}
