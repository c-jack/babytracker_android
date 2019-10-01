package uk.cjack.babytracker;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import uk.cjack.babytracker.model.Baby;
import uk.cjack.babytracker.db.DatabaseHelper;


public class MainActivity extends BaseActivity {

    private ArrayAdapter<String> mBabyNameAdapter;
    private ListView mBabyNameView;

    @Override
    protected void onCreate( final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set initial page on load
        setContentView(R.layout.activity_main);

        // Set toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get ListView
        mBabyNameView = findViewById(R.id.baby_name_list_view);
        mBabyNameView.setDivider(null);
        mBabyNameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String babyName = parent.getItemAtPosition(position).toString();

                selectBaby( new Baby( id, babyName) );
            }
        });


        // Set floating button
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewBaby();
            }
        });

        updateBabyNameListView();

    }

    /**
     * Toast sample
     * @param msg
     */
    private void makeToast( final String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Alert sample
     */
    private void showAlert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setMessage("Test");
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu( final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Load the selected baby
     */
    public void selectBaby(final Baby selectedBaby) {
        final Intent intent = new Intent(MainActivity.this, BabyActivity.class);
        intent.putExtra(BabyActivity.SELECTED_BABY, selectedBaby );
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected( final MenuItem item) {
        switch (item.getItemId()) {
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
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Refreshes the ListView of babynames
     */
    private void updateBabyNameListView() {
        final ArrayList<String> taskList = new ArrayList<>();

        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        final Cursor cursor = db.query(DatabaseHelper.BabyEntry.TABLE_NAME,
                new String[]{DatabaseHelper.BabyEntry._ID, DatabaseHelper.BabyEntry.BABY_NAME_COL},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            final int idx = cursor.getColumnIndex(DatabaseHelper.BabyEntry.BABY_NAME_COL);
            taskList.add(cursor.getString(idx));
        }

        if (mBabyNameAdapter == null) {
            mBabyNameAdapter = new ArrayAdapter<>(this,
                    R.layout.baby_name_item, // what view to use for the items
                    R.id.babyName, // where to put the String of data
                    taskList); // where to get all the data

            mBabyNameView.setAdapter(mBabyNameAdapter); // set it as the adapter of the ListView instance
        } else {
            mBabyNameAdapter.clear();
            mBabyNameAdapter.addAll(taskList);
            mBabyNameAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    private void addNewBaby() {
        final EditText taskEditText = new EditText(this);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add a new Baby")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( final DialogInterface dialog, final int which) {
                        final String task = String.valueOf(taskEditText.getText());
                        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
                        final ContentValues values = new ContentValues();

                        values.put(DatabaseHelper.BabyEntry.BABY_NAME_COL, task);
                        db.insertWithOnConflict(DatabaseHelper.BabyEntry.TABLE_NAME,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        db.close();
                        updateBabyNameListView();

                        final String msg = String.format(getString(R.string.baby_created_msg), task);
                        makeToast(msg);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}
