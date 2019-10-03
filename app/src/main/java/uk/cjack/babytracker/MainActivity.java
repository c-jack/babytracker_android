package uk.cjack.babytracker;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import uk.cjack.babytracker.adapters.BabyAdapter;
import uk.cjack.babytracker.db.DatabaseHelper;
import uk.cjack.babytracker.model.Baby;


public class MainActivity extends BaseActivity {

    private BabyAdapter mBabyNameAdapter;
    private ListView mBabyNameView;
    private ArrayList<Baby> mBabyNameList;

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Set initial page on load
        setContentView( R.layout.activity_main );

        // Set toolbar
        final Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        // Get ListView
        mBabyNameView = findViewById( R.id.baby_name_list_view );
        mBabyNameView.setDivider( null );
        mBabyNameView.setOnItemClickListener( ( parent, view, position, id ) -> {
            selectBaby(mBabyNameList.get( position ) );
        } );


        // Set floating button
        final FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( view -> addNewBaby() );

        updateBabyNameListView();

        registerForContextMenu( mBabyNameView );

    }

    /**
     * Toast sample
     *
     * @param msg
     */
    private void makeToast( final String msg ) {
        Toast.makeText( MainActivity.this, msg, Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onCreateContextMenu( final ContextMenu menu, final View v,
                                     final ContextMenu.ContextMenuInfo menuInfo ) {
        if ( v.getId() == R.id.baby_name_list_view ) {
            final ListView lv = ( ListView ) v;
            final AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
                    ( AdapterView.AdapterContextMenuInfo ) menuInfo;
            final String babyName = mBabyNameList.get( adapterContextMenuInfo.position ).getBabyName();

            menu.setHeaderIcon( R.drawable.baby );
            menu.setHeaderTitle( babyName );
            final int id = Math.toIntExact( adapterContextMenuInfo.id );
            menu.add( 0, id, 0, "Edit" );
            menu.add( 0, id, 0, "Delete" );
        }
    }

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
                editBaby( item );
                return true;
            case "delete":
                return deleteBaby( item );
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
    private boolean deleteBaby( final MenuItem menuItem ) {
        new AlertDialog.Builder( this )
                .setTitle( "Delete Baby" )
                .setMessage( "Are you sure you want to delete this baby?" )
                .setIcon( android.R.drawable.ic_dialog_alert )
                .setPositiveButton( android.R.string.yes, ( dialog, whichButton ) -> {
                    final String msg;
                    final long babyId = mBabyNameList.get( menuItem.getItemId() ).getBabyId();

                    if ( mDatabaseHelper.delete( DatabaseHelper.BabyEntry.TABLE_NAME,
                            DatabaseHelper.BabyEntry._ID + " = " + babyId ) ) {
                        msg = "Baby Deleted";

                    }
                    else {
                        msg = "Baby Could Not Be Deleted";
                    }
                    Toast.makeText( MainActivity.this, msg, Toast.LENGTH_SHORT ).show();

                    updateBabyNameListView();
                } )
                .setNegativeButton( android.R.string.no, null ).show();
        return true;
    }

    /**
     * Alert sample
     */
    private void showAlert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( MainActivity.this );
        alertDialogBuilder
                .setMessage( "Test" );
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu( final Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }


    /**
     * Load the selected baby
     */
    public void selectBaby( final Baby selectedBaby ) {
        final Intent intent = new Intent( MainActivity.this, BabyActivity.class );
        intent.putExtra( BabyActivity.SELECTED_BABY, selectedBaby );
        startActivity( intent );
    }

    private void editBaby( final MenuItem item ) {

        final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Baby editedBaby = mBabyNameList.get( menuInfo.position);
        final EditText babyNameEditText = new EditText( this );
        babyNameEditText.setText( editedBaby.getBabyName() );

        final AlertDialog dialog = new AlertDialog.Builder( this )
                .setTitle( "Edit " + item.getTitle() )
                .setView( babyNameEditText )
                .setPositiveButton( "Save", ( dialog1, which ) -> {
                    final String babyName = String.valueOf( babyNameEditText.getText() );
                    final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
                    final ContentValues values = new ContentValues();

                    values.put( DatabaseHelper.BabyEntry._ID, editedBaby.getBabyId() );
                    values.put( DatabaseHelper.BabyEntry.BABY_NAME_COL, babyName );
                    db.insertWithOnConflict( DatabaseHelper.BabyEntry.TABLE_NAME,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE );
                    db.close();
                    updateBabyNameListView();

                    final String msg = String.format( getString( R.string.baby_created_msg ),
                            babyName );
                    makeToast( msg );
                } )
                .setNegativeButton( "Cancel", null )
                .create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected( final MenuItem item ) {
        switch ( item.getItemId() ) {
//            case R.id.action_add_task:
//                final EditText taskEditText = new EditText(this);
//                AlertDialog dialog = new AlertDialog.Builder(this)
//                        .setTitle("Add a new task")
//                        .setMessage("What do you want to do next?")
//                        .setView(taskEditText)
//                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String task = String.valueOf(taskEditText.getText());
//                                SQLiteDatabase db = mHelper.getWritableDatabase();
//                                ContentValues values = new ContentValues();
//                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
//                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
//                                        null,
//                                        values,
//                                        SQLiteDatabase.CONFLICT_REPLACE);
//                                db.close();
//                                updateUI();
//                            }
//                        })
//                        .setNegativeButton("Cancel", null)
//                        .create();
//                dialog.show();
//                return true;

            default:
                return super.onOptionsItemSelected( item );
        }
    }


    /**
     * Refreshes the ListView of babynames
     */
    private void updateBabyNameListView() {
        final ArrayList<Baby> babyList = new ArrayList<>();

        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        final Cursor cursor = db.query( DatabaseHelper.BabyEntry.TABLE_NAME,
                new String[]{ DatabaseHelper.BabyEntry._ID,
                        DatabaseHelper.BabyEntry.BABY_NAME_COL },
                null, null, null, null, null );
        while ( cursor.moveToNext() ) {
            final int babyNameCol = cursor.getColumnIndex( DatabaseHelper.BabyEntry.BABY_NAME_COL );
            final int babyIdCol = cursor.getColumnIndex( DatabaseHelper.BabyEntry._ID );
            babyList.add( new Baby( cursor.getLong( babyIdCol ),cursor.getString( babyNameCol ) ));
        }

        if ( mBabyNameAdapter == null ) {
            mBabyNameAdapter = new BabyAdapter( this, R.id.baby_name_list_view, babyList ); // where to get all the data

            mBabyNameView.setAdapter( mBabyNameAdapter ); // set it as the adapter of the
            // ListView instance
        }
        else {
            mBabyNameAdapter.notify( babyList );
        }
        mBabyNameList = babyList;
        cursor.close();
        db.close();
    }

    /**
     * Add new baby
     */
    private void addNewBaby() {
        final EditText taskEditText = new EditText( this );
        final AlertDialog dialog = new AlertDialog.Builder( this )
                .setTitle( "Add a new Baby" )
                .setView( taskEditText )
                .setPositiveButton( "Add", ( dialog1, which ) -> {
                    final String task = String.valueOf( taskEditText.getText() );
                    final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
                    final ContentValues values = new ContentValues();

                    values.put( DatabaseHelper.BabyEntry.BABY_NAME_COL, task );
                    db.insertWithOnConflict( DatabaseHelper.BabyEntry.TABLE_NAME,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE );
                    db.close();
                    updateBabyNameListView();

                    final String msg = String.format( getString( R.string.baby_created_msg ),
                            task );
                    makeToast( msg );
                } )
                .setNegativeButton( "Cancel", null )
                .create();
        dialog.show();
    }
}
