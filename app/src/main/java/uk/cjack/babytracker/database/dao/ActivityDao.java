package uk.cjack.babytracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.cjack.babytracker.database.entities.Activity;
import uk.cjack.babytracker.database.entities.Baby;

@Dao
public interface ActivityDao {

    @Insert
    void insert( Activity Activity );

    @Query( "DELETE FROM activity_table" )
    void deleteAll();

    @Query( "SELECT * FROM activity_table WHERE babyId = :babyId ORDER BY activityDateTime DESC" )
    LiveData<List<Activity>> getActivitiesForBaby( final int babyId );

    @Delete
    void delete( Activity Activity );

    @Update
    void update( Activity Activity );
}
