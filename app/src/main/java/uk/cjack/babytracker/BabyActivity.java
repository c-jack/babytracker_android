package uk.cjack.babytracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import uk.cjack.babytracker.adapters.ActivityAdapter;
import uk.cjack.babytracker.database.entities.Activity;
import uk.cjack.babytracker.database.entities.Baby;
import uk.cjack.babytracker.enums.ActivityEnum;
import uk.cjack.babytracker.enums.ChangeTypeEnum;
import uk.cjack.babytracker.model.ActivityViewModelFactory;
import uk.cjack.babytracker.view.ActivityViewModel;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static uk.cjack.babytracker.database.entities.Activity.getDateStringFromDate;
import static uk.cjack.babytracker.database.entities.Activity.getTimeFromDate;
import static uk.cjack.babytracker.utils.BabyTrackerUtils.isToday;

public class BabyActivity extends BaseActivity implements AlertDialog.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    public static final String SELECTED_BABY = "uk.cjack.babytracker.selected_baby";
    public static final String TITLE = "%s's Activity";

    private RecyclerView mActivityListView;
    private ActivityAdapter mActivityAdapter;
    private Baby mSelectedBaby;
    private TextView dateField;
    private TextView feedDaySelect;
    private TextView timeField;
    private TextView activityValueField;
    private TextView activityIdField;
    private TextView activityTitleField;
    private TextView activityValueUnitField;
    private ImageView activityIcon;
    private RadioGroup nappyChangeTypeGroup;
    private ActivityEnum activityToAdd;
    private AlertDialog mDialog;
    private ActivityViewModel mActivityViewModel;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;


    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.baby_screen );

        // Get ListView
        mActivityListView = findViewById( R.id.feedListView );

        final TextView selectedBabyNameText = findViewById( R.id.selectedBabyName );

        final Baby selectedBaby = ( Baby ) getIntent().getSerializableExtra( SELECTED_BABY );
        if ( selectedBaby != null ) {
            mSelectedBaby = selectedBaby;
            selectedBabyNameText.setText( String.format( TITLE, mSelectedBaby.getBabyName() ) );
        }

        mActivityViewModel = ViewModelProviders.of( this,
                new ActivityViewModelFactory( this.getApplication(), selectedBaby ) ).get( ActivityViewModel.class );
        mActivityViewModel.getAllActivitiesForBaby().observe( this,
                activityList -> mActivityAdapter.setActivityList( activityList ) );

        mActivityAdapter = new ActivityAdapter( this );
        mActivityListView.setAdapter( mActivityAdapter );
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
     * @param item menuItem
     */
    private void createAndShowActivityDialog( final MenuItem item ) {

        // Set initial values
        final Date now = new Date();
        final int babyActivity;
        Integer activityDbId = null;
        String addOrSaveButtonText = "Add";
        long millisTimeValue = now.getTime();
        final Calendar initialTime = Calendar.getInstance();
        final String activityValueUnitText;
        final int title;
        final int activityIconId;
        int nappyChangeTypeGroupVisibility = View.INVISIBLE;
        int activityValueVisibility = View.VISIBLE;
        String activityValue = null;
        ChangeTypeEnum changeTypeEnum = null;

        if ( item != null ) {
            final Activity thisActivity =
                    mActivityAdapter.getActivityList().stream().filter( activity -> ( item.getItemId() == activity.getActivityId() ) ).findAny().orElse( null );

            if ( thisActivity != null ) {
                activityToAdd = ActivityEnum.getEnum( thisActivity.getActivityTypeValue() );
                addOrSaveButtonText = getString( R.string.save_button_text );
                millisTimeValue = thisActivity.getActivityDateTime().getTime();
                activityValue = thisActivity.getActivityValue();
                activityDbId = thisActivity.getActivityId();
            }
        }
        initialTime.setTimeInMillis( millisTimeValue );

        if ( activityToAdd != null ) {
            /*
             * Add the Unit value to the text field
             */
            final ActivityEnum.ActivityConfig activityConfig = activityToAdd.config();
            activityValueUnitText = activityConfig.getUnit();

            /*
             * Set the view based on the activity
             */
            if ( activityToAdd.equals( ActivityEnum.FEED ) ) {
                babyActivity = R.layout.add_activity;
                activityIconId = R.drawable.feed;

                if ( activityDbId != null ) {
                    title = R.string.edit_feed_title;
                }
                else {
                    title = R.string.add_new_feed;
                }
            }
            else if ( activityToAdd.equals( ActivityEnum.CHANGE ) ) {

                babyActivity = R.layout.add_activity;
                activityIconId = R.drawable.nappy;
                nappyChangeTypeGroupVisibility = View.VISIBLE;
                activityValueVisibility = View.INVISIBLE;

                if ( activityDbId != null ) {
                    title = R.string.edit_change;
                    changeTypeEnum = ChangeTypeEnum.getEnum( activityValue );
                }
                else {
                    title = R.string.add_new_change;
                }
            }
            else {
                babyActivity = R.layout.add_activity;
                title = R.string.edit_feed_title;
                activityIconId = R.drawable.nappy;
            }

            /*
             * Build the dialog
             */
            final AlertDialog dialog = new AlertDialog.Builder( this )
                    .setView( babyActivity )
                    .setPositiveButton( addOrSaveButtonText, this )
                    .setNegativeButton( getString( R.string.cancel_button_text), null )
                    .create();
            dialog.show();

            mDialog = dialog;

            /*
             * Set the fields in the pop-up dialog
             */
            dateField = dialog.findViewById( R.id.activityDate );
            timeField = dialog.findViewById( R.id.activityTimeSelect );
            feedDaySelect = dialog.findViewById( R.id.activityDaySelect );
            activityValueField = dialog.findViewById( R.id.activityValue );
            activityIdField = dialog.findViewById( R.id.activityDbId );
            activityTitleField = dialog.findViewById( R.id.activityTitle );
            activityIcon = dialog.findViewById( R.id.activityIcon );
            nappyChangeTypeGroup = dialog.findViewById( R.id.nappyChangeTypeGroup );
            activityValueUnitField = dialog.findViewById( R.id.activityValueUnit );

            /*
             * Set the values based on either now, or the existing values
             */
            setActivityId( activityDbId );
            setDayField( millisTimeValue );
            setTimeField( millisTimeValue );
            dateField.setText( String.valueOf( millisTimeValue ) );
            activityValueField.setText( activityValue );
            activityTitleField.setText( getString( title ) );
            activityIcon.setImageResource( activityIconId );
            nappyChangeTypeGroup.setVisibility( nappyChangeTypeGroupVisibility );
            activityValueField.setVisibility( activityValueVisibility );
            activityValueUnitField.setText( activityValueUnitText );
            if ( changeTypeEnum != null ) {
                nappyChangeTypeGroup.clearCheck();
                final ChangeTypeEnum.ChangeConfig config = changeTypeEnum.getConfig();
                final RadioButton selectedRadioButton = dialog.findViewById( config.getRadioButtonId() );
                selectedRadioButton.setChecked( true );
            }

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
            dateString = getString( R.string.today );
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
                final String idText = idField.getText().toString();
                if ( idText.length() > 0 ) {
                    saveActivity( Long.parseLong( idText ) );
                }
                else {
                    saveActivity( 0 );
                }
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
     * @param activityId the ID of the activity being edited (null, if new).
     */
    private void saveActivity( final long activityId ) {

        final Activity editedActivity =
                mActivityAdapter.getActivityList().stream().filter(
                        activity -> ( activityId == activity.getActivityId() ) )
                        .findAny().orElse( null );

        final String activityDate = dateField.getText().toString();
        final LocalDateTime localDateTime =
                LocalDateTime.ofInstant( Instant.ofEpochMilli( Long.parseLong( activityDate ) ),
                        TimeZone.getDefault().toZoneId() );
        final Date activityDateTime =
                Date.from( localDateTime.atZone( ZoneId.systemDefault() ).toInstant() );

        final int selectedNappyChangeType = nappyChangeTypeGroup.getCheckedRadioButtonId();
        final RadioButton selectedType = mDialog.findViewById( selectedNappyChangeType );

        final String activityValue;

        if ( activityToAdd == ActivityEnum.CHANGE ) {
            activityValue = selectedType.getTag().toString();
        }
        else {
            activityValue = activityValueField.getText().toString();

        }

        if ( editedActivity != null ) {

            boolean modified = false;

            if ( !editedActivity.getActivityDate().equals( activityDate ) ) {
                editedActivity.setActivityDateTime( activityDateTime );
                modified = true;
            }
            if ( activityToAdd == ActivityEnum.FEED ) {
                if ( !editedActivity.getActivityValue().equals( activityValue ) ) {
                    editedActivity.setActivityValue( activityValue );
                    modified = true;
                }
            }
            else if ( activityToAdd == ActivityEnum.CHANGE ) {

                if ( !editedActivity.getActivityValue().equals( activityValue ) ) {
                    editedActivity.setActivityValue( activityValue );
                    modified = true;
                }

            }
            if ( modified ) {
                mActivityViewModel.update( editedActivity );
            }
        }
        else {
            final Activity newActivity = new Activity( mSelectedBaby, activityToAdd,
                    activityDateTime, activityValue );

            mActivityViewModel.insert( newActivity );
            // create new one
        }
    }


    /**
     * Deletes the selected activity from the list
     *
     * @param menuItem a
     * @return a
     */
    private boolean createDeleteActivityDialog( final MenuItem menuItem ) {
        new AlertDialog.Builder( this )
                .setTitle( "Delete Feed" )
                .setMessage( "Are you sure you want to delete this feed entry?" )
                .setIcon( android.R.drawable.ic_dialog_alert )
                .setPositiveButton( android.R.string.yes, deleteActivity( menuItem ) )
                .setNegativeButton( android.R.string.no, null ).show();
        return true;
    }

    /**
     * @param menuItem
     * @return
     */
    private DialogInterface.OnClickListener deleteActivity( final MenuItem menuItem ) {
        // Delete the activity object if found
        return ( dialog, whichButton ) -> mActivityAdapter.getActivityList().stream().filter(
                activity -> ( menuItem.getItemId() == activity.getActivityId() ) )
                .findAny().ifPresent( activityToDelete -> mActivityViewModel.delete( activityToDelete ) );
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
                return createDeleteActivityDialog( item );
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
