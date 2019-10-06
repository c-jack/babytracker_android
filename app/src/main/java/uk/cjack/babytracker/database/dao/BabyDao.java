package uk.cjack.babytracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.cjack.babytracker.database.entities.Baby;

@Dao
public interface BabyDao {

    @Insert
    void insert( Baby baby);

    @Query("DELETE FROM baby_table")
    void deleteAll();

    @Query("SELECT * from baby_table ORDER BY babyName ASC")
    LiveData<List<Baby>> getAllBabies();

    @Delete
    void delete( Baby baby);

    @Update
    void update( Baby baby);
}
