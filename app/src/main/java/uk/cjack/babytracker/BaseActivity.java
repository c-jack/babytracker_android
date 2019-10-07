package uk.cjack.babytracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import uk.cjack.babytracker.database.db.DatabaseHelper;

public class BaseActivity extends AppCompatActivity {

    protected DatabaseHelper mDatabaseHelper;

    /**
     * Initial items
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate( final Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }

    @Override
    protected void onPause() {

        // TODO close database

        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();

        // TODO open database
    }
}
