package uk.cjack.babytracker.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import uk.cjack.babytracker.database.BabyTrackerDatabase;
import uk.cjack.babytracker.database.dao.ActivityDao;
import uk.cjack.babytracker.database.entities.Activity;
import uk.cjack.babytracker.database.entities.Baby;
import uk.cjack.babytracker.database.enums.DatabaseActionEnum;
import uk.cjack.babytracker.enums.ActivityEnum;
import uk.cjack.babytracker.model.DayActivitySummary;
import uk.cjack.babytracker.model.DayActivityTotals;

public class ActivityRepository {

    private final ActivityDao mActivityDao;
    private final LiveData<List<Activity>> mAllActivitiesForBaby;
    private final LiveData<List<Activity>> mAllActivitiesForBabyByDate;
    private final LiveData<List<DayActivitySummary>> mDailyFeedSummary;
    private final LiveData<List<DayActivityTotals>> mDailyFeedTotals;

    /**
     * Constructor
     *
     * @param application application
     * @param baby        the {@link Baby} to filter by in the filtered query
     */
    public ActivityRepository( final Application application, final Baby baby, final String filterDate ) {
        final BabyTrackerDatabase db = BabyTrackerDatabase.getDatabase( application );
        mActivityDao = db.activityDao();
        mAllActivitiesForBaby = mActivityDao.getActivitiesForBaby( baby.getBabyId() );
        mAllActivitiesForBabyByDate = mActivityDao.getActivitiesForBabyByDate( baby.getBabyId(), filterDate );
        mDailyFeedSummary = mActivityDao.getDailyFeedSummary( baby.getBabyId(), ActivityEnum.FEED.getName() );
        mDailyFeedTotals = mActivityDao.getDailyActivityTotals( baby.getBabyId() );
    }

    public LiveData<List<Activity>> getAllActivitiesForBaby() {
        return mAllActivitiesForBaby;
    }
    public LiveData<List<Activity>> getAllActivitiesForBabyByDate() {
        return mAllActivitiesForBabyByDate;
    }

    public LiveData<List<DayActivitySummary>> getDailyFeedSummary() {
        return mDailyFeedSummary;
    }
    public LiveData<List<DayActivityTotals>> getDailyFeedTotals() {
        return mDailyFeedTotals;
    }

    /**
     * Performs an INSERT query in a background thread
     *
     * @param activity the {@link Activity} object to insert
     */
    public void insert( final Activity activity ) {
        new BabyAsyncTask( mActivityDao, DatabaseActionEnum.INSERT ).execute( activity );
    }

    /**
     * Performs an UPDATE query in a background thread
     *
     * @param activity the {@link Baby} object to insert
     */
    public void update( final Activity activity ) {
        new BabyAsyncTask( mActivityDao, DatabaseActionEnum.UPDATE ).execute( activity );
    }

    /**
     * Performs an DELETE query in a background thread
     *
     * @param activity the {@link Activity} object to insert
     */
    public void delete( final Activity activity ) {
        new BabyAsyncTask( mActivityDao, DatabaseActionEnum.DELETE ).execute( activity );
    }
}
