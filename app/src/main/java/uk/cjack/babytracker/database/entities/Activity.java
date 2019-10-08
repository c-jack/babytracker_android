package uk.cjack.babytracker.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

import uk.cjack.babytracker.constants.DatabaseConstants;
import uk.cjack.babytracker.database.db.TimestampConverter;
import uk.cjack.babytracker.enums.ActivityEnum;

import static androidx.room.ForeignKey.CASCADE;
import static uk.cjack.babytracker.constants.DatabaseConstants.ACTIVITY_TABLE_NAME;
import static uk.cjack.babytracker.constants.DatabaseConstants.BABY_TABLE_PK;
import static uk.cjack.babytracker.utils.DateTimeUtils.getDateStringFromDate;
import static uk.cjack.babytracker.utils.DateTimeUtils.getTimeFromDate;

@Entity( tableName = ACTIVITY_TABLE_NAME,
        foreignKeys = @ForeignKey( entity = Baby.class,
                parentColumns = BABY_TABLE_PK,
                childColumns = BABY_TABLE_PK,
                onDelete = CASCADE ) )
public class Activity implements Serializable, Comparable<Activity> {

    public Activity( @NonNull final Baby baby,
                     @NonNull final ActivityEnum activity,
                     @NonNull final Date activityDateTime,
                     @NonNull final String activityValue ) {
        this.activityId = 0;
        this.baby = baby;
        this.babyId = baby.getBabyId();
        this.activityType = activity;
        this.activityTypeValue = activity.getName();
        this.activityDateTime = activityDateTime;
        this.activityValue = activityValue;

        setActivityDateTimeValues();
    }

    @PrimaryKey( autoGenerate = true )
    private int activityId;

    @Ignore
    private Baby baby;

    @ColumnInfo( index = true )
    private int babyId;

    @Ignore
    private ActivityEnum activityType;

    private String activityTypeValue;

    @TypeConverters( TimestampConverter.class )
    private Date activityDateTime;

    private String activityDate;
    private String activityTime;
    private String activityValue;
    private String activityValueDetail;


    /**
     * Default empty constructor as required by the compiler
     */
    public Activity() {
    }


    /**
     * Uses the {@code activityDateTime} value to set the individual Date and Time values
     */
    private void setActivityDateTimeValues() {

        this.activityDate = getDateStringFromDate( this.activityDateTime );
        this.activityTime = getTimeFromDate( this.activityDateTime );
    }


    /**
     * Sets the Baby value
     * @param baby {@link Baby} value to set
     */
    public void setBaby( @NonNull final Baby baby ) {
        this.baby = baby;
    }

    /**
     * Sets the BabyId value
     * @param babyId BabyId value to set
     */
    public void setBabyId( final int babyId ) {
        this.babyId = babyId;
    }

    /**
     * Sets the ActivityType value
     * @param activityTypeValue x
     */
    public void setActivityTypeValue( @NonNull final String activityTypeValue ) {
        this.activityTypeValue = activityTypeValue;
    }

    public int getActivityId() {
        return activityId;
    }

    public Baby getBaby() {
        return baby;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public String getActivityTime() {
        return activityTime;
    }

    @NonNull
    public String getActivityValue() {
        return activityValue;
    }


    @NonNull
    public Date getActivityDateTime() {
        return activityDateTime;
    }


    @Override
    public int compareTo( final Activity o ) {
        return this.getActivityDateTime().compareTo( o.getActivityDateTime() );
    }

    @NonNull
    public ActivityEnum getActivityType() {
        return activityType;
    }


    public void setActivityType( @NonNull final ActivityEnum activityType ) {
        this.activityType = activityType;
    }

    public void setActivityDateTime( @NonNull final Date activityDateTime ) {
        this.activityDateTime = activityDateTime;
    }

    public void setActivityDate( final String activityDate ) {
        this.activityDate = activityDate;
    }

    public void setActivityTime( final String activityTime ) {
        this.activityTime = activityTime;
    }

    public void setActivityValue( @NonNull final String activityValue ) {
        this.activityValue = activityValue;
    }

    public void setActivityValueDetail( final String activityValueDetail ) {
        this.activityValueDetail = activityValueDetail;
    }

    public String getActivityValueDetail() {
        return activityValueDetail;
    }

    public void setActivityId( final int activityId ) {
        this.activityId = activityId;
    }

    @NonNull
    public String getActivityTypeValue() {
        return activityTypeValue;
    }

    public int getBabyId() {
        return babyId;
    }
}
