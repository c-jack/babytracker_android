package uk.cjack.babytracker.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Activity implements Serializable, Comparable<Activity> {
    private String activityId;
    private int babyId;
    private ActivityEnum activityType;
    private Date activityDateTime;
    private String activityDate;
    private String activityTime;
    private String activityValue;

    private static final String DATE_FORMAT = "EEEE d MMM yyyy";
    private static final String TIME_FORMAT = "HH:mm a";

    public int getActivityId() {
        return Integer.parseInt( activityId );
    }

    public int getBabyId() {
        return babyId;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public String getActivityValue() {
        return activityValue;
    }

    public Activity( final String activityId, final int babyId, final ActivityEnum activity, final Date activityDate, final String activityValue ) {
        this.activityId = activityId;
        this.babyId = babyId;
        this.activityType = activity;
        this.activityDateTime = activityDate;
        this.activityValue = activityValue;

        setActivityDateTimeValues();
    }

    private void setActivityDateTimeValues() {

        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        this.activityDate = dateFormat.format( this.activityDateTime );

        final DateFormat timeFormat = new SimpleDateFormat( TIME_FORMAT, Locale.getDefault() );
        this.activityTime = timeFormat.format( this.activityDateTime );
    }



    public Date getActivityDateTime() {
        return activityDateTime;
    }



    @Override
    public int compareTo( final Activity o ) {
        return this.getActivityDateTime().compareTo(o.getActivityDateTime());
    }

    public ActivityEnum getActivityType() {
        return activityType;
    }

    /**
     * Enumerate for the different Activity types
     */
    public enum ActivityEnum {
        FEED("feed"),
        CHANGE("change");

        private String value;

        ActivityEnum( final String value)
        {
            this.value = value;
        }

        private String value()
        {
            return this.value;
        }

        public static ActivityEnum getEnum( final String valueToRetrieve)
        {
            for( final ActivityEnum enumVal : ActivityEnum.class.getEnumConstants() )
            {
                if( enumVal.value().equalsIgnoreCase( valueToRetrieve ))
                {
                    return enumVal;
                }
            }
            return null;
        }
    }
}
