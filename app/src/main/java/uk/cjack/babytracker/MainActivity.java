package uk.cjack.babytracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uk.cjack.babytracker.adapters.BabyListAdapter;
import uk.cjack.babytracker.database.entities.Baby;
import uk.cjack.babytracker.view.BabyViewModel;


public class MainActivity extends BaseActivity implements BabyListAdapter.OnItemClicked {

    private BabyListAdapter mBabyListAdapter;
    private RecyclerView mBabyNameView;
    private BabyViewModel mBabyViewModel;


    /**
     * Setup of objects when the activity is loaded
     */
    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        // Set toolbar
        final Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        mBabyListAdapter = new BabyListAdapter();
        mBabyListAdapter.setOnClick( MainActivity.this );


        mBabyViewModel = ViewModelProviders.of( this ).get( BabyViewModel.class );
        mBabyViewModel.getAllBabies().observe( this,
                babyList -> mBabyListAdapter.setBabyList( babyList ) );

        mBabyNameView = findViewById( R.id.baby_name_list_view );
        mBabyNameView.setLayoutManager( new LinearLayoutManager( this ) );
        mBabyNameView.setAdapter( mBabyListAdapter );


        // Set floating button
        final FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( view -> createNewBabyDialog() );


        registerForContextMenu( mBabyNameView );

    }

    @Override
    public void onItemClick( final int position, final Baby current ) {
        final Intent intent = new Intent( MainActivity.this, BabyActivity.class );
        intent.putExtra( BabyActivity.SELECTED_BABY, current );
        startActivity( intent );
    }

    /**
     * Toast sample
     *
     * @param msg message to display in the toast
     */
    private void makeToast( final String msg ) {
        Toast.makeText( MainActivity.this, msg, Toast.LENGTH_SHORT ).show();
    }


    /**
     * Long press on activity
     *
     * @param item menu item selected in the context menu
     * @return bool
     */
    @Override
    public boolean onContextItemSelected( final MenuItem item ) {
        switch ( item.getTitle().toString().toLowerCase() ) {
            case "edit":
                createEditBabyDialog( item );
                return true;
            case "delete":
                return createDeleteBabyDialog( item );
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
    private boolean createDeleteBabyDialog( final MenuItem menuItem ) {
        new AlertDialog.Builder( this )
                .setTitle( getString( R.string.delete_baby ) )
                .setMessage( "Are you sure you want to delete this baby?" )
                .setIcon( android.R.drawable.ic_dialog_alert )
                .setPositiveButton( android.R.string.yes, deleteBaby( menuItem ) )
                .setNegativeButton( android.R.string.no, null ).show();
        return true;
    }


    /**
     * Deletes the Baby from the DB
     * @param menuItem
     * @return
     */
    private DialogInterface.OnClickListener deleteBaby( final MenuItem menuItem ) {
        return ( dialog, whichButton ) -> {
            final Baby selectedBaby =
                    mBabyListAdapter.getBabyList().stream().filter(
                            activity -> ( menuItem.getItemId() == activity.getBabyId() ) )
                            .findAny().orElse( null );

            mBabyViewModel.delete( selectedBaby );

            Toast.makeText( MainActivity.this, "Deleted", Toast.LENGTH_SHORT ).show();

        };
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
     * Creates and displays the edit dialog for the selected baby
     *
     * @param menuItem the item being edited
     */
    private void createEditBabyDialog( final MenuItem menuItem ) {

        final Baby editedBaby =
                mBabyListAdapter.getBabyList().stream().filter( activity -> ( menuItem.getItemId() == activity.getBabyId() ) ).findAny().orElse( null );

        final EditText babyNameEditText = new EditText( this );

        if ( editedBaby != null ) {

            babyNameEditText.setText( editedBaby.getBabyName() );

            final AlertDialog dialog = new AlertDialog.Builder( this )
                    .setTitle( String.format( getString( R.string.baby_edit_msg ),
                            menuItem.getTitle() ) )
                    .setView( babyNameEditText )
                    .setPositiveButton( getString( R.string.save_button_text ),
                            saveBaby( editedBaby, babyNameEditText ) )
                    .setNegativeButton( getString( R.string.cancel_button_text ), null )
                    .create();
            dialog.show();
        }
    }

    /**
     * Processes the onClick event in the edit baby popup, and saves the amended {@link Baby} to
     * the database
     *
     * @param editedBaby       the {@link Baby} for the current selection
     * @param babyNameEditText the {@link android.widget.TextView} for the edited baby name
     * @return onClick listener to be passed back up
     */
    private DialogInterface.OnClickListener saveBaby( final Baby editedBaby,
                                                      final EditText babyNameEditText ) {
        return ( dialog1, which ) -> {
            final String babyName = String.valueOf( babyNameEditText.getText() );

            editedBaby.setBabyName( babyName );
            mBabyViewModel.update( editedBaby );

            final String msg = String.format( getString( R.string.baby_created_msg ),
                    babyName );
            makeToast( msg );
        };
    }


//    @Override
//    public void onCreateContextMenu( final ContextMenu menu, final View v,
//                                     final ContextMenu.ContextMenuInfo menuInfo ) {
//        if ( v.getId() == R.id.baby_name_list_view ) {
//            final AdapterView.AdapterContextMenuInfo adapterContextMenuInfo =
//                    ( AdapterView.AdapterContextMenuInfo ) menuInfo;
////            final String babyName =
////                    mBabyNameList.get( adapterContextMenuInfo.position ).getBabyName();
//
//            menu.setHeaderIcon( R.drawable.baby );
////            menu.setHeaderTitle( babyName );
//            final int id = Math.toIntExact( adapterContextMenuInfo.id );
//            menu.add( 0, id, 0, "Edit" );
//            menu.add( 0, id, 0, "Delete" );
//        }
//    }

    /**
     * Add new baby
     */
    private void createNewBabyDialog() {
        final EditText taskEditText = new EditText( this );
        final AlertDialog dialog = new AlertDialog.Builder( this )
                .setTitle( getString( R.string.add_new_baby ) )
                .setView( taskEditText )
                .setPositiveButton( getString( R.string.add_button_text ),
                        addNewBabyToDatabase( taskEditText ) )
                .setNegativeButton( getString( R.string.cancel_button_text ), null )
                .create();
        dialog.show();
    }

    /**
     * Add a new Baby to the Database
     *
     * @param taskEditText
     * @return
     */
    private DialogInterface.OnClickListener addNewBabyToDatabase( final EditText taskEditText ) {
        return ( dialog1, which ) -> {
            final String babyName = String.valueOf( taskEditText.getText() );
            final Baby newBaby = new Baby( babyName );

            mBabyViewModel.insert( newBaby );

            final String msg = String.format( getString( R.string.baby_created_msg ),
                    babyName );
            makeToast( msg );
        };
    }
}
