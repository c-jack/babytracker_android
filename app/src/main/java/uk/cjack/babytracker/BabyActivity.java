package uk.cjack.babytracker;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import uk.cjack.babytracker.adapters.ActivityAdapter;
import uk.cjack.babytracker.database.db.DatabaseHelper;
import uk.cjack.babytracker.enums.ActivityEnum;
import uk.cjack.babytracker.model.Activity;
import uk.cjack.babytracker.database.entities.Baby;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static uk.cjack.babytracker.model.Activity.getDateStringFromDate;
import static uk.cjack.babytracker.model.Activity.getTimeFromDate;
import static uk.cjack.babytracker.utils.BabyTrackerUtils.isToday;

public class BabyActivity extends BaseActivity implements AlertDialog.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    public static final String SELECTED_BABY = "uk.cjack.babytracker.selected_baby";
    public static final String TITLE = "%s's Activity";

    private RecyclerView mActivityListView;
    private ActivityAdapter activityAdapter;
    private Baby mSelectedBaby;
    private TextView dateField;
    private TextView feedDaySelect;
    private TextView timeField;
    private TextView activityValue;
    private TextView activityIdField;
    private TextView activityTitle;
    private TextView activityValueUnit;
    private ImageView activityIcon;
    private RadioGroup nappyChangeTypeGroup;
    private ActivityEnum activityToAdd;
    private AlertDialog mDialog;
    private List<Activity> mActivities;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;


    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Set initial page on load
        setContentView( R.layout.baby_screen );

        final TextView selectedBabyNameText = findViewById( R.id.selectedBabyName );
        final Baby selectedBaby = ( Baby ) getIntent().getSerializableExtra( SELECTED_BABY );
        if ( selectedBaby != null ) {
            mSelectedBaby = selectedBaby;
            selectedBabyNameText.setText( String.format( TITLE, mSelectedBaby.getBabyName() ) );
        }

        // Get ListView
        mActivityListView = findViewById( R.id.feedListView );

        // Initialize contacts
        updateActivityList();

        activityAdapter = new ActivityAdapter( mActivities );
        mActivityListView.setAdapter( activityAdapter );
        mActivityListView.setLayoutManager( new LinearLayoutManager( this ) );

        // Add SpeedView items
        final SpeedDialView speedDialView = addSpeedViewItems();
        speedDialView.setOnActionSelectedListener( speedViewOnSelect() );

    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * ACTIVITY DIALOG BUILDER
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /**
     * Creates and shows the new activity dialog
     *
     * @param item
     */
    private void createAndShowActivityDialog( final MenuItem item ) {

        // Set initial values
        final Date now = new Date();
        final int babyActivity;
        Integer activityDbId = null;
        String addOrSaveButtonText = "Add";
        long millisTimeValue = now.getTime();
        final Calendar initialTime = Calendar.getInstance();
        String amount = "";
        String activityValueUnitText = "";
        final int title;
        final int activityIconId;
        int nappyChangeTypeGroupVisibility = View.INVISIBLE;
        int activityValueVisibility = View.VISIBLE;

        if ( item != null ) {
            final Activity thisActivity =
                    mActivities.stream().filter( activity -> ( item.getItemId() == activity.getActivityId() ) ).findAny().orElse( null );

            if ( thisActivity != null ) {
                activityToAdd = thisActivity.getActivityType();
                addOrSaveButtonText = "Save";
                millisTimeValue = thisActivity.getActivityDateTime().getTime();
                amount = thisActivity.getActivityValue();
                activityDbId = thisActivity.getActivityId();
                final ActivityEnum.ActivityConfig activityConfig = activityToAdd.config();
                activityValueUnitText = activityConfig.getUnit();
            }
        }
        initialTime.setTimeInMillis( millisTimeValue );


        /*
         * Set the view based on the activity
         */
        if ( activityToAdd.equals( ActivityEnum.FEED ) ) {
            babyActivity = R.layout.baby_feed_activity;
            activityIconId = R.drawable.feed;

            if ( activityDbId != null ) {
                title = R.string.edit_feed_title;
            }
            else {
                title = R.string.add_new_feed;
            }
        }
        else if ( activityToAdd.equals( ActivityEnum.CHANGE ) )
        {
            babyActivity = R.layout.baby_feed_activity;
            activityIconId = R.drawable.nappy;
            nappyChangeTypeGroupVisibility = View.VISIBLE;
            activityValueVisibility = View.INVISIBLE;

            if ( activityDbId != null ) {
                title = R.string.edit_change;
            }
            else {
                title = R.string.add_new_change;
            }

        }
        else {
            babyActivity = R.layout.baby_add_activity;
            title = R.string.edit_feed_title;
            activityIconId = R.drawable.nappy;
        }

        /*
         * Build the dialog
         */
        final AlertDialog dialog = new AlertDialog.Builder( this )
                .setView( babyActivity )
                .setPositiveButton( addOrSaveButtonText, this )
                .setNegativeButton( "Cancel", null )
                .create();
        dialog.show();

        mDialog = dialog;

        /*
         * Set the fields in the pop-up dialog
         */
        dateField = dialog.findViewById( R.id.activityDate );
        timeField = dialog.findViewById( R.id.activityTimeSelect );
        feedDaySelect = dialog.findViewById( R.id.activityDaySelect );
        activityValue = dialog.findViewById( R.id.activityValue );
        activityIdField = dialog.findViewById( R.id.activityDbId );
        activityTitle = dialog.findViewById( R.id.activityTitle );
        activityIcon = dialog.findViewById( R.id.activityIcon );
        nappyChangeTypeGroup = dialog.findViewById( R.id.nappyChangeTypeGroup );
        activityValueUnit = dialog.findViewById( R.id.activityValueUnit );

        /*
         * Set the values based on either now, or the existing values
         */
        setActivityId( activityDbId );
        setDayField( millisTimeValue );
        setTimeField( millisTimeValue );
        dateField.setText( String.valueOf( millisTimeValue ) );
        activityValue.setText( amount );
        activityTitle.setText( getString( title ) );
        activityIcon.setImageResource( activityIconId );
        nappyChangeTypeGroup.setVisibility( nappyChangeTypeGroupVisibility );
        activityValue.setVisibility( activityValueVisibility );
        activityValueUnit.setText( activityValueUnitText );


        /*
         * Set the listeners for the Date and Time
         */
        feedDaySelect.setOnClickListener( v -> {
            datePickerDialog = DatePickerDialog.newInstance( BabyActivity.this, initialTime );
            datePickerDialog.setThemeDark( false );
            datePickerDialog.showYearPickerFirst( false );
            datePickerDialog.show( getFragmentManager(), "DatePickerDialog" );
        } );

        timeField.setOnClickListener( v -> {
            timePickerDialog = TimePickerDialog.newInstance( BabyActivity.this, false );
            timePickerDialog.setThemeDark( false );
            timePickerDialog.show( getFragmentManager(), "TimePickerDialog" );
            if ( item != null ) {
                timePickerDialog.setInitialSelection(
                        initialTime.get( Calendar.HOUR_OF_DAY ),
                        initialTime.get( Calendar.MINUTE ),
                        initialTime.get( Calendar.SECOND ) );
            }
        } );
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * ACTIVITY POPUP SETUP
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Sets the Activity ID in the dialog if it is an edit
     *
     * @param activityDbId
     */
    private void setActivityId( final Integer activityDbId ) {
        if ( activityDbId != null ) {
            final String value = String.valueOf( activityDbId );
            activityIdField.setText( value );
        }
    }


    /**
     * @param timeInMillis
     * @return
     */
    private String setDayField( final long timeInMillis ) {
        final String dateString;
        final Calendar dateSet = Calendar.getInstance();
        dateSet.setTimeInMillis( timeInMillis );

        if ( isToday( dateSet ) ) {
            dateString = "Today";
        }
        else {
            dateString = getDateStringFromDate( dateSet.getTime() );
        }
        feedDaySelect.setText( dateString );
        return dateString;
    }

    /**
     * @param timeInMillis
     * @return
     */
    private String setTimeField( final long timeInMillis ) {
        final Calendar timeSet = Calendar.getInstance();
        timeSet.setTimeInMillis( timeInMillis );

        final String time = getTimeFromDate( timeSet.getTime() );
        timeField.setText( time );
        return time;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * ACTIVITY POPUP REACTIONS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    @Override
    public void onTimeSet( final TimePickerDialog view, final int hourOfDay, final int minute,
                           final int second ) {

        // Get the date currently set in the date field
        final long setDateInMillis = Long.parseLong( dateField.getText().toString() );

        final Calendar timeSet = Calendar.getInstance();
        timeSet.setTimeInMillis( setDateInMillis );
        timeSet.set( Calendar.HOUR_OF_DAY, hourOfDay );
        timeSet.set( Calendar.MINUTE, minute );
        timeSet.set( Calendar.SECOND, second );


        final String time = setTimeField( timeSet.getTimeInMillis() );

        Toast.makeText( BabyActivity.this, time, Toast.LENGTH_LONG ).show();

        dateField.setText( String.valueOf( timeSet.getTimeInMillis() ) );
    }

    /**
     * @param view
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet( final DatePickerDialog view, final int year, final int month,
                           final int day ) {

        // Get the time currently set and shown in the time field
        final Calendar time = Calendar.getInstance();
        time.setTimeInMillis( Long.parseLong( dateField.getText().toString() ) );

        // Set the provided date with the time field values
        final Calendar dateSet = Calendar.getInstance();
        dateSet.set(
                year,
                month,
                day,
                time.get( Calendar.HOUR_OF_DAY ),
                time.get( Calendar.MINUTE )
        );

        final String dateString = setDayField( dateSet.getTimeInMillis() );

        Toast.makeText( BabyActivity.this, dateString, Toast.LENGTH_LONG ).show();

        dateField.setText( String.valueOf( dateSet.getTimeInMillis() ) );

    }


    @Override
    public void onClick( final DialogInterface dialog, final int which ) {
        switch ( which ) {
            case BUTTON_NEGATIVE:
                break;
            case BUTTON_POSITIVE:
                final TextView idField = mDialog.findViewById( R.id.activityDbId );
                saveActivity( idField.getText().toString() );
                break;
        }
        dialog.dismiss();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * ACTIVITY ACTIONS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Saves or adds the activity data
     *
     * @param idFieldValue the ID of the field being edited (null, if new).
     */
    private void saveActivity( final String idFieldValue ) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        final ContentValues values = new ContentValues();

        if ( idFieldValue != null && !idFieldValue.isEmpty() ) {
            values.put( DatabaseHelper.BabyActivityEntry._ID,
                    idFieldValue );
        }

        values.put( DatabaseHelper.BabyActivityEntry.ACTIVITY_DATE_COL,
                dateField.getText().toString() );
        values.put( DatabaseHelper.BabyActivityEntry.ACTIVITY_DATA_COL,
                activityValue.getText().toString() );
        values.put( DatabaseHelper.BabyActivityEntry.ACTIVITY_SEL_COL,
                "" );
        values.put( DatabaseHelper.BabyActivityEntry.BABY_COL,
                mSelectedBaby.getBabyId() );
        values.put( DatabaseHelper.BabyActivityEntry.ACTIVITY,
                activityToAdd.getName() );

        db.insertWithOnConflict( DatabaseHelper.BabyActivityEntry.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE );
        db.close();
        updateActivityList();
        activityAdapter.notifyDataSetChanged();
    }


    /**
     * Gets the list of activities for the selected {@link Baby} and updates it in the Recycler View
     */
    private void updateActivityList() {
        final List<Activity> activityList = new ArrayList<>();

        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        final Cursor cursor = db.query(
                DatabaseHelper.BabyActivityEntry.TABLE_NAME,
                new String[]{ DatabaseHelper.BabyActivityEntry._ID,
                        DatabaseHelper.BabyActivityEntry.BABY_COL,
                        DatabaseHelper.BabyActivityEntry.ACTIVITY,
                        DatabaseHelper.BabyActivityEntry.ACTIVITY_DATA_COL,
                        DatabaseHelper.BabyActivityEntry.ACTIVITY_DATE_COL },
                DatabaseHelper.BabyActivityEntry.BABY_COL + " = " + mSelectedBaby.getBabyId(),
                null, null, null, null, null );

        while ( cursor.moveToNext() ) {

            // Get Columns
            final int feedDbIdCol = cursor.getColumnIndex( DatabaseHelper.BabyActivityEntry._ID );
            final int babyIdCol =
                    cursor.getColumnIndex( DatabaseHelper.BabyActivityEntry.BABY_COL );
            final int activityCol =
                    cursor.getColumnIndex( DatabaseHelper.BabyActivityEntry.ACTIVITY );
            final int dateMillisCol =
                    cursor.getColumnIndex( DatabaseHelper.BabyActivityEntry.ACTIVITY_DATE_COL );
            final int feedCol =
                    cursor.getColumnIndex( DatabaseHelper.BabyActivityEntry.ACTIVITY_DATA_COL );

            // Get values
            final int babyId = cursor.getInt( babyIdCol );
            final String dateMillis = cursor.getString( dateMillisCol );
            final ActivityEnum activityEnum =
                    ActivityEnum.getEnum( cursor.getString( activityCol ) );
            final String feedAmount = cursor.getString( feedCol );
            final String feedDbId = cursor.getString( feedDbIdCol );

            final Date date = DatabaseHelper.millisToDate( Long.parseLong( dateMillis ) );

            activityList.add( new Activity( feedDbId, babyId, activityEnum, date, feedAmount ) );
        }

        cursor.close();
        db.close();

        // Sort by date
        Collections.sort( activityList );
        Collections.reverse( activityList );

        mActivities = activityList;
        if ( activityAdapter != null ) {
            activityAdapter.notify( mActivities );
        }
    }


    /**
     * Deletes the selected activity from the list
     *
     * @param menuItem a
     * @return a
     */
    private boolean deleteActivity( final MenuItem menuItem ) {
        new AlertDialog.Builder( this )
                .setTitle( "Delete Feed" )
                .setMessage( "Are you sure you want to delete this feed entry?" )
                .setIcon( android.R.drawable.ic_dialog_alert )
                .setPositiveButton( android.R.string.yes, ( dialog, whichButton ) -> {
                    final String msg;
                    if ( mDatabaseHelper.delete( DatabaseHelper.BabyActivityEntry.TABLE_NAME,
                            DatabaseHelper.BabyActivityEntry._ID + " = " + menuItem.getItemId() ) ) {
                        msg = "Feed Deleted";
                        updateActivityList();
                    }
                    else {
                        msg = "Feed Could Not Be Deleted";
                    }
                    Toast.makeText( BabyActivity.this, msg, Toast.LENGTH_SHORT ).show();
                } )
                .setNegativeButton( android.R.string.no, null ).show();
        return true;
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * CONTEXT MENU
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /**
     * Long press on activity
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected( final MenuItem item ) {
        switch ( item.getTitle().toString().toLowerCase() ) {
            case "edit":
                createAndShowActivityDialog( item );
                return true;
            case "delete":
                return deleteActivity( item );
            default:
                return false;
        }

    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * SPEED VIEW
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /**
     * Listener for the SpeedView 'OnSelect' Actions
     *
     * @return
     */
    private SpeedDialView.OnActionSelectedListener speedViewOnSelect() {
        return speedDialActionItem -> {
            switch ( speedDialActionItem.getId() ) {
                case R.id.fab_feed:
                    activityToAdd = ActivityEnum.FEED;
                    createAndShowActivityDialog( null );
                    return false;
                case R.id.fab_change:
                    activityToAdd = ActivityEnum.CHANGE;
                    createAndShowActivityDialog( null );
                default:
                    return false;
            }
        };
    }


    /**
     * Add items to the SpeedView
     *
     * @return e
     */
    private SpeedDialView addSpeedViewItems() {
        final SpeedDialView speedDialView = findViewById( R.id.speedDial );

        // Feeds
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder( R.id.fab_feed, R.drawable.feed )
                        .setLabel( "Feed" )
                        .setFabBackgroundColor( ResourcesCompat.getColor( getResources(),
                                R.color.sa_gray, getTheme() ) )
                        .create() );
        // Nappy Changes
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder( R.id.fab_change, R.drawable.nappy )
                        .setLabel( "Change" )
                        .setFabBackgroundColor( ResourcesCompat.getColor( getResources(),
                                R.color.sa_gray, getTheme() ) )
                        .create() );
//        // Temperature Checks
//        speedDialView.addActionItem(
//                new SpeedDialActionItem.Builder( R.id.fab_temp, R.drawable.temperature )
//                        .setLabel( "Temperature" )
//                        .setFabBackgroundColor( ResourcesCompat.getColor( getResources(),
//                                R.color.sa_gray, getTheme() ) )
//                        .create() );
//        // Medicine
//        speedDialView.addActionItem(
//                new SpeedDialActionItem.Builder( R.id.fab_temp, R.drawable.medicine )
//                        .setLabel( "Medicine" )
//                        .setFabBackgroundColor( ResourcesCompat.getColor( getResources(),
//                                R.color.sa_gray, getTheme() ) )
//                        .create() );
//        // Sleep
//        speedDialView.addActionItem(
//                new SpeedDialActionItem.Builder( R.id.fab_sleep, R.drawable.sleep )
//                        .setLabel( "Sleep" )
//                        .setFabBackgroundColor( ResourcesCompat.getColor( getResources(),
//                                R.color.sa_gray, getTheme() ) )
//                        .create() );
//        // Vomit
//        speedDialView.addActionItem(
//                new SpeedDialActionItem.Builder( R.id.fab_vomit, R.drawable.vomited )
//                        .setLabel( "Vomit" )
//                        .setFabBackgroundColor( ResourcesCompat.getColor( getResources(),
//                                R.color.sa_gray, getTheme() ) )
//                        .create() );
        return speedDialView;
    }
}
