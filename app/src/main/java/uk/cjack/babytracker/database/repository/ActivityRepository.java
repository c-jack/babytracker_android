package uk.cjack.babytracker.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import uk.cjack.babytracker.database.BabyTrackerDatabase;
import uk.cjack.babytracker.database.dao.ActivityDao;
import uk.cjack.babytracker.database.entities.Activity;
import uk.cjack.babytracker.database.entities.Baby;
import uk.cjack.babytracker.enums.ActivityEnum;
import uk.cjack.babytracker.model.ActivitySummary;

public class ActivityRepository {

    private final ActivityDao mActivityDao;
    private final LiveData<List<Activity>> mAllActivitiesForBaby;
    private final LiveData<List<ActivitySummary>> mDailyFeedTotals;

    public ActivityRepository( final Application application, final Baby baby ) {
        final BabyTrackerDatabase db = BabyTrackerDatabase.getDatabase( application );
        mActivityDao = db.activityDao();
        mAllActivitiesForBaby = mActivityDao.getActivitiesForBaby( baby.getBabyId() );

        mDailyFeedTotals = mActivityDao.getDailyFeedTotals( ActivityEnum.FEED.getName() );
    }

    public LiveData<List<Activity>> getAllActivitiesForBaby() {
        return mAllActivitiesForBaby;
    }

    public LiveData<List<ActivitySummary>> getDailyFeedTotals() {
        return mDailyFeedTotals;
    }

    public void insert( final Activity activity ) {
        new InsertAsyncTask( mActivityDao ).execute( activity );
    }

    public void delete( final Activity activity ) {
        new DeleteAsyncTask( mActivityDao ).execute( activity );
    }

    public void update( final Activity activity ) {
        new UpdateAsyncTask( mActivityDao ).execute( activity );
    }


    /**
     * Async task class
     */
    private static class InsertAsyncTask extends AsyncTask<Activity, Void, Void> {

        private final ActivityDao mAsyncTaskDao;

        InsertAsyncTask( final ActivityDao dao ) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground( final Activity... params ) {
            mAsyncTaskDao.insert( params[0] );
            return null;
        }
    }

    /**
     * Async task class
     */
    private static class DeleteAsyncTask extends AsyncTask<Activity, Void, Void> {

        private final ActivityDao mAsyncTaskDao;

        DeleteAsyncTask( final ActivityDao dao ) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground( final Activity... params ) {
            mAsyncTaskDao.delete( params[0] );
            return null;
        }
    }

    /**
     * Async task class
     */
    private static class UpdateAsyncTask extends AsyncTask<Activity, Void, Void> {

        private final ActivityDao mAsyncTaskDao;

        UpdateAsyncTask( final ActivityDao dao ) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground( final Activity... params ) {
            mAsyncTaskDao.update( params[0] );
            return null;
        }
    }
}
