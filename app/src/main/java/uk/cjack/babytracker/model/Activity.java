package uk.cjack.babytracker.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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

    public Activity( final String activityId, final int babyId, final ActivityEnum activity,
                     final Date activityDate, final String activityValue ) {
        this.activityId = activityId;
        this.babyId = babyId;
        this.activityType = activity;
        this.activityDateTime = activityDate;
        this.activityValue = activityValue;

        setActivityDateTimeValues();
    }

    private void setActivityDateTimeValues() {

        this.activityDate = getDateStringFromDate( this.activityDateTime );
        this.activityTime = getTimeFromDate( this.activityDateTime );
    }

    /**
     * @param dateToConvert
     * @return
     */
    public static String getTimeFromDate( final Date dateToConvert ) {
        final DateFormat timeFormat = new SimpleDateFormat( TIME_FORMAT, Locale.getDefault() );
        return timeFormat.format( dateToConvert );
    }

    /**
     * @param dateToConvert
     * @return
     */
    public static String getDateStringFromDate( final Date dateToConvert ) {
        final DateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT, Locale.getDefault() );
        return dateFormat.format( dateToConvert );
    }


    public Date getActivityDateTime() {
        return activityDateTime;
    }


    @Override
    public int compareTo( final Activity o ) {
        return this.getActivityDateTime().compareTo( o.getActivityDateTime() );
    }

    public ActivityEnum getActivityType() {
        return activityType;
    }

    /**
     * Enumerate for the different Activity types
     */
    public enum ActivityEnum {
        FEED( new ActivityConfig("feed", "ml") ),
        CHANGE( new ActivityConfig("change", null) );

        private final ActivityConfig config;

        ActivityEnum( final ActivityConfig value ) {
            this.config = value;
        }

        public String getName() {
            return this.config.getName();
        }
        public String getUnit() {
            return this.config.getUnit();
        }
        public ActivityConfig config() {
            return this.config;
        }

        public static ActivityEnum getEnum( final String valueToRetrieve ) {
            for ( final ActivityEnum enumVal :
                    Objects.requireNonNull( ActivityEnum.class.getEnumConstants() ) ) {
                if ( enumVal.getName().equalsIgnoreCase( valueToRetrieve ) ) {
                    return enumVal;
                }
            }
            return null;
        }
    }

    /**
     *
     */
    private static class ActivityConfig {
        private final String name;
        private final String unit;

        ActivityConfig( final String name, final String unit )
        {
            this.name = name;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public String getUnit() {
            return unit;
        }
    }
}
