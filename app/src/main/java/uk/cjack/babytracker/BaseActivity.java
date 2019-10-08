package uk.cjack.babytracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

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
