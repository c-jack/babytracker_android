package uk.cjack.babytracker;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import uk.cjack.babytracker.adapters.ActivityAdapter;
import uk.cjack.babytracker.db.DatabaseHelper;
import uk.cjack.babytracker.model.Activity;
import uk.cjack.babytracker.model.Baby;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class BabyActivity extends BaseActivity implements AlertDialog.OnClickListener {

    public static final String SELECTED_BABY = "uk.cjack.babytracker.selected_baby";

    private RecyclerView mActivityListView;
    private ActivityAdapter activityAdapter;
    private Baby mSelectedBaby;
    private TextView dateField;
    private TextView amountField;
    private RadioGroup selectedActivity;
    private AlertDialog mDialog;
    private List<Activity> mActivities;


    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );


        // Set initial page on load
        setContentView( R.layout.baby_screen );

        final TextView selectedBabyNameText = findViewById( R.id.selectedBabyName );
        final Baby selectedBaby = ( Baby ) getIntent().getSerializableExtra( SELECTED_BABY );
        if ( selectedBaby != null ) {
            mSelectedBaby = selectedBaby;
            selectedBabyNameText.setText( mSelectedBaby.getBabyName() );
        }


        // Get ListView
        mActivityListView = findViewById( R.id.feedListView );


        // Initialize contacts
        updateActivityList();

        activityAdapter = new ActivityAdapter( this, mActivities );
        mActivityListView.setAdapter( activityAdapter );
        mActivityListView.setLayoutManager( new LinearLayoutManager( this ) );

        // Set floating button
        final FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( final View view ) {
                createAndShowNewActivityDialog();
            }
        } );

    }

    /**
     * Creates and shows the new activity dialog
     */
    private void createAndShowNewActivityDialog() {
        final AlertDialog dialog = new AlertDialog.Builder( this )
                .setTitle( "Add a new activity" )
                .setView( R.layout.baby_add_activity )
                .setPositiveButton( "Add", this )
                .setNegativeButton( "Cancel", null )
                .create();
        dialog.show();

        mDialog = dialog;
        final Date now = new Date();

        dateField = dialog.findViewById( R.id.activityDate );
        dateField.setText( String.valueOf( now.getTime() ) );

        amountField = dialog.findViewById( R.id.activityValue );
        selectedActivity = dialog.findViewById( R.id.activitySelect );
    }

    @Override
    public void onClick( final DialogInterface dialog, final int which ) {
        switch ( which ) {
            case BUTTON_NEGATIVE:
                // int which = -2
                dialog.dismiss();
                break;
            case BUTTON_POSITIVE:
                // int which = -1
                addNewActivity();
                dialog.dismiss();
                break;
        }
    }

    private void addNewActivity() {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int checkedRadioButtonId = selectedActivity.getCheckedRadioButtonId();

        final ContentValues values = new ContentValues();
        values.put( DatabaseHelper.BabyActivityEntry.ACTIVITY_DATE_COL,
                dateField.getText().toString() );
        values.put( DatabaseHelper.BabyActivityEntry.ACTIVITY_DATA_COL,
                amountField.getText().toString() );
        values.put( DatabaseHelper.BabyActivityEntry.BABY_COL,
                mSelectedBaby.getBabyId() );

        final RadioButton button = mDialog.findViewById( checkedRadioButtonId );

        values.put( DatabaseHelper.BabyActivityEntry.ACTIVITY, button.getText().toString() );
        db.insertWithOnConflict( DatabaseHelper.BabyActivityEntry.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE );
        db.close();
        updateActivityList();
    }


    /**
     * Gets the list of activities for the selected {@link Baby}
     *
     * @return List of {@link Activity} values.
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
            final Activity.ActivityEnum activityEnum =
                    Activity.ActivityEnum.getEnum( cursor.getString( activityCol ) );
            final String feedAmount = cursor.getString( feedCol );
            final String feedDbId = cursor.getString( feedDbIdCol );

            final Date date = DatabaseHelper.millisToDate( Long.parseLong( dateMillis ) );

            activityList.add( new Activity( feedDbId, babyId, activityEnum, date, feedAmount ) );
        }

        cursor.close();
        db.close();

        // Sort by date
        Collections.sort( activityList );
        mActivities = activityList;
        if ( activityAdapter != null ) {
            activityAdapter.notify( mActivities );
        }
    }

    @Override
    public boolean onContextItemSelected( final MenuItem item ) {
        switch ( item.getTitle().toString().toLowerCase() ) {
            case "edit":
                return true;
            case "delete":
                return deleteActivity( item );
            default:
                return false;
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
                .setPositiveButton( android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick( final DialogInterface dialog, final int whichButton ) {
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
                    }
                } )
                .setNegativeButton( android.R.string.no, null ).show();
        return true;
    }

}
