package uk.cjack.babytracker.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Feed implements Serializable, Comparable<Feed> {
    private String feedId;
    private int babyId;
    private Date feedDateTime;
    private String feedDate;
    private String feedTime;
    private long feedAmount;

    private static final String DATE_FORMAT = "EEEE d MMM yyyy";
    private static final String TIME_FORMAT = "HH:mm a";

    public Feed( final String feedDbId, final int babyId, final Date feedDate, final long feedAmount ) {
        this.feedId = feedDbId;
        this.babyId = babyId;
        this.feedDateTime = feedDate;
        this.feedAmount = feedAmount;

        setFeedDateTimeValues();
    }

    private void setFeedDateTimeValues() {

        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        this.feedDate = dateFormat.format( this.feedDateTime );

        final DateFormat timeFormat = new SimpleDateFormat( TIME_FORMAT, Locale.getDefault() );
        this.feedTime = timeFormat.format( this.feedDateTime );
    }

    public int getFeedId() {
        return Integer.parseInt( feedId );
    }

    public int getBabyId() {
        return babyId;
    }

    public void setBabyId( final int babyId ) {
        this.babyId = babyId;
    }

    public String getFeedDate() {
        return feedDate;
    }

    public void setFeedDate( final String feedDate ) {
        this.feedDate = feedDate;
    }

    public String getFeedTime() {
        return feedTime;
    }

    public void setFeedTime( final String feedTime ) {
        this.feedTime = feedTime;
    }

    public long getFeedAmount() {
        return feedAmount;
    }

    public void setFeedAmount( final long feedAmount ) {
        this.feedAmount = feedAmount;
    }


    public Date getFeedDateTime() {
        return feedDateTime;
    }



    @Override
    public int compareTo( final Feed o ) {
        return this.getFeedDateTime().compareTo(o.getFeedDateTime());
    }
}
