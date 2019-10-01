package uk.cjack.babytracker;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import uk.cjack.babytracker.adapters.FeedAdapter;
import uk.cjack.babytracker.model.Baby;
import uk.cjack.babytracker.db.DatabaseHelper;
import uk.cjack.babytracker.model.Feed;

public class BabyActivity extends BaseActivity {

    public static final String SELECTED_BABY = "uk.cjack.babytracker.selected_baby";

    private RecyclerView mFeedListView;
    private FeedAdapter mFeedListAdapter;
    private Baby mSelectedBaby;
    private TextView dateField;
    private TextView amountField;


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
        mFeedListView = findViewById( R.id.feedListView );


        // Initialize contacts
        final List<Feed> feeds = getFeedList();
        Collections.sort( feeds );

        mFeedListAdapter = new FeedAdapter( this, feeds );
        mFeedListView.setAdapter( mFeedListAdapter );
        mFeedListView.setLayoutManager( new LinearLayoutManager( this ) );

        // Set floating button
        final FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( final View view ) {
                addNewFeed();
            }
        } );

//        getFeedList();

    }

    /**
     * Adds a new feed
     */
    private void addNewFeed() {
        final AlertDialog dialog = new AlertDialog.Builder( this )
                .setTitle( "Add a new feed" )
                .setView( R.layout.baby_add_feed )
                .setPositiveButton( "Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( final DialogInterface dialog, final int which ) {
                        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

                        final ContentValues values = new ContentValues();
                        values.put( DatabaseHelper.BabyFeedEntry.FEED_DATE_COL,
                                dateField.getText().toString() );
                        values.put( DatabaseHelper.BabyFeedEntry.FEED_AMOUNT_COL,
                                amountField.getText().toString() );
                        values.put( DatabaseHelper.BabyFeedEntry.BABY_COL,
                                mSelectedBaby.getBabyId() );
                        db.insertWithOnConflict( DatabaseHelper.BabyFeedEntry.TABLE_NAME,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE );
                        db.close();
                        getFeedList();
                    }
                } )
                .setNegativeButton( "Cancel", null )
                .create();
        dialog.show();

        dateField = dialog.findViewById( R.id.feedDate );
        amountField = dialog.findViewById( R.id.feedAmount );
    }


    /**
     * Update the feed list
     * @return
     */
    private List<Feed> getFeedList() {
        final List<Feed> feedList = new ArrayList<>();

        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        final Cursor cursor = db.query(
                DatabaseHelper.BabyFeedEntry.TABLE_NAME,
                new String[]{ DatabaseHelper.BabyFeedEntry._ID,
                        DatabaseHelper.BabyFeedEntry.BABY_COL,
                        DatabaseHelper.BabyFeedEntry.FEED_AMOUNT_COL,
                        DatabaseHelper.BabyFeedEntry.FEED_DATE_COL },
                DatabaseHelper.BabyFeedEntry.BABY_COL + " = " + mSelectedBaby.getBabyId(),
                null, null, null, null, null );
        while ( cursor.moveToNext() ) {

            final int babyId = cursor.getColumnIndex( DatabaseHelper.BabyFeedEntry.BABY_COL );
            final int dateMillisCol =
                    cursor.getColumnIndex( DatabaseHelper.BabyFeedEntry.FEED_DATE_COL );
            final Date date = DatabaseHelper.millisToDate( cursor.getInt( dateMillisCol ) );

            final int feedCol =
                    cursor.getColumnIndex( DatabaseHelper.BabyFeedEntry.FEED_AMOUNT_COL );
            final int feedAmount = cursor.getInt( feedCol );

            feedList.add( new Feed( babyId, date, feedAmount ) );
        }
//        mFeedListAdapter.notifyDataSetChanged();

        cursor.close();
        db.close();

        return feedList;
    }
}
