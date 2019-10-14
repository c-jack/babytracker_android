package uk.cjack.babytracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.cjack.babytracker.database.entities.Activity;
import uk.cjack.babytracker.model.DayActivitySummary;
import uk.cjack.babytracker.model.DayActivityTotals;

@Dao
public interface ActivityDao {

    @Insert
    void insert( Activity Activity );

    @Query( "DELETE " +
            "FROM activity_table" )
    void deleteAll();

    @Query( "SELECT " +
            "* " +
            "FROM activity_table " +
            "WHERE babyId = :babyId " +
            "ORDER BY activityDateTime DESC" )
    LiveData<List<Activity>> getActivitiesForBaby( final int babyId );

    @Query( "SELECT " +
            "* " +
            "FROM activity_table " +
            "WHERE babyId = :babyId " +
            "AND activityDate = :activityDate " +
            "ORDER BY activityDateTime DESC" )
    LiveData<List<Activity>> getActivitiesForBabyByDate( final int babyId, final String activityDate );

    @Query( "SELECT " +
            "activityDate 'groupValue', " +
            "SUM(activityValue) 'resultValue' " +
            "FROM activity_table " +
            "WHERE babyId = :babyId " +
            "AND activityTypeValue = :feedValue " +
            "GROUP BY activityDate")
    LiveData<List<DayActivitySummary>> getDailyFeedSummary( final int babyId, final String feedValue );

    @Query( "SELECT " +
            "activityDate 'date', " +
            "babyId, " +
            "SUM(CASE WHEN activityTypeValue = \"feed\" " +
                "THEN activityValue ELSE 0 END) 'feedQty', " +
            "SUM(CASE WHEN activityTypeValue = \"change\" AND activityValue = \"soiled\" " +
                "THEN 1 ELSE 0 END) 'soiledQty', " +
            "SUM(CASE WHEN activityTypeValue = \"change\" AND activityValue = \"wet\" " +
                "THEN 1 ELSE 0 END) 'wetQty'" +
            " FROM activity_table " +
            "WHERE babyId = :babyId " +
            "GROUP BY activityDate " +
            "ORDER BY activityDateTime DESC")
    LiveData<List<DayActivityTotals>> getDailyActivityTotals( final int babyId );

    @Delete
    void delete( Activity Activity );

    @Update
    void update( Activity Activity );
}
