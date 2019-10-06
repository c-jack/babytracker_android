package uk.cjack.babytracker.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import uk.cjack.babytracker.database.BabyTrackerDatabase;
import uk.cjack.babytracker.database.dao.BabyDao;
import uk.cjack.babytracker.database.entities.Baby;

public class BabyRepository {

    private final BabyDao mBabyDao;
    private final LiveData<List<Baby>> mAllBabies;

    public BabyRepository( final Application application ) {
        final BabyTrackerDatabase db = BabyTrackerDatabase.getDatabase( application );
        mBabyDao = db.babyDao();
        mAllBabies = mBabyDao.getAllBabies();
    }

    public LiveData<List<Baby>> getAllBabies() {
        return mAllBabies;
    }

    public void insert( final Baby baby ) {
        new InsertAsyncTask( mBabyDao ).execute( baby );
    }

    public void delete( final Baby baby ) {
        new DeleteAsyncTask( mBabyDao ).execute( baby );
    }

    public void update( final Baby baby ) {
        new UpdateAsyncTask( mBabyDao ).execute( baby );
    }


    /**
     * Async task class
     */
    private static class InsertAsyncTask extends AsyncTask<Baby, Void, Void> {

        private final BabyDao mAsyncTaskDao;

        InsertAsyncTask( final BabyDao dao ) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground( final Baby... params ) {
            mAsyncTaskDao.insert( params[0] );
            return null;
        }
    }

    /**
     * Async task class
     */
    private static class DeleteAsyncTask extends AsyncTask<Baby, Void, Void> {

        private final BabyDao mAsyncTaskDao;

        DeleteAsyncTask( final BabyDao dao ) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground( final Baby... params ) {
            mAsyncTaskDao.delete( params[0] );
            return null;
        }
    }

    /**
     * Async task class
     */
    private static class UpdateAsyncTask extends AsyncTask<Baby, Void, Void> {

        private final BabyDao mAsyncTaskDao;

        UpdateAsyncTask( final BabyDao dao ) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground( final Baby... params ) {
            mAsyncTaskDao.update( params[0] );
            return null;
        }
    }
}
