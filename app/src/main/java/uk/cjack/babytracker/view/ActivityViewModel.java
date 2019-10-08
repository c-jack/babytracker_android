package uk.cjack.babytracker.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import uk.cjack.babytracker.database.entities.Activity;
import uk.cjack.babytracker.database.entities.Baby;
import uk.cjack.babytracker.database.repository.ActivityRepository;
import uk.cjack.babytracker.model.ActivitySummary;

public class ActivityViewModel extends AndroidViewModel {

    private final ActivityRepository mActivityRepository;
    private final LiveData<List<Activity>> mAllActivitiesForBaby;
    private final LiveData<List<ActivitySummary>> mDailyFeedTotals;


    public ActivityViewModel( final Application application, final Baby baby ) {
        super( application );
        mActivityRepository = new ActivityRepository( application, baby );
        mAllActivitiesForBaby = mActivityRepository.getAllActivitiesForBaby();
        mDailyFeedTotals = mActivityRepository.getDailyFeedTotals();
    }

    public LiveData<List<Activity>> getAllActivitiesForBaby() {
        return mAllActivitiesForBaby;
    }

    public LiveData<List<ActivitySummary>> getDailyFeedTotals()
    {
        return mDailyFeedTotals;
    }

    public void insert( final Activity activity ) {
        mActivityRepository.insert( activity );
    }

    public void delete( final Activity activity ) {
        mActivityRepository.delete( activity );
    }

    public void update( final Activity activity ) {
        mActivityRepository.update( activity );
    }
}
