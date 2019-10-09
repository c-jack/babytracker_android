package uk.cjack.babytracker.model;

import androidx.room.ColumnInfo;

import java.io.Serializable;

public class DayActivityTotals implements Serializable {
    @ColumnInfo( name = "date" )
    private String activityDate;

    @ColumnInfo( name = "babyId" )
    private int babyId;

    @ColumnInfo( name = "feedQty" )
    private String feedTotal;

    @ColumnInfo( name = "soiledQty" )
    private String soiledNappies;

    @ColumnInfo( name = "wetQty" )
    private String wetNappies;


    public String getActivityDate() {
        return activityDate;
    }

    public String getFeedTotal() {
        return feedTotal;
    }

    public String getSoiledNappies() {
        return soiledNappies;
    }

    public String getWetNappies() {
        return wetNappies;
    }

    public void setActivityDate( final String activityDate ) {
        this.activityDate = activityDate;
    }

    public void setFeedTotal( final String feedTotal ) {
        this.feedTotal = feedTotal;
    }

    public void setSoiledNappies( final String soiledNappies ) {
        this.soiledNappies = soiledNappies;
    }

    public void setWetNappies( final String wetNappies ) {
        this.wetNappies = wetNappies;
    }

    public int getBabyId() {
        return babyId;
    }

    public void setBabyId( final int babyId ) {
        this.babyId = babyId;
    }
}

