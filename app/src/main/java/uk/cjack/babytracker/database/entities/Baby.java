package uk.cjack.babytracker.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity( tableName = "baby_table" )
public class Baby implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int babyId;

    @NonNull
    private String babyName;

    public Baby( @NonNull final String babyName) {
        this.babyId = 0;
        this.babyName = babyName;
    }


    public int getBabyId() {
        return babyId;
    }
    public void setBabyId( final int babyId) {
        this.babyId = babyId;
    }


    @NonNull
    public String getBabyName() {
        return babyName;
    }

    public void setBabyName( @NonNull final String babyName) {
        this.babyName = babyName;
    }
}
