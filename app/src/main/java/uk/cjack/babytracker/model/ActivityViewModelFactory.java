package uk.cjack.babytracker.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import uk.cjack.babytracker.database.entities.Baby;
import uk.cjack.babytracker.view.ActivityViewModel;

public class ActivityViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final Baby mSelectedBaby;
    private final String mFilterDate;


    public ActivityViewModelFactory( final Application application, final Baby baby, final String filterDate ) {
        mApplication = application;
        mSelectedBaby = baby;
        mFilterDate = filterDate;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create( @NonNull final Class<T> modelClass ) {
        return ( T ) new ActivityViewModel( mApplication, mSelectedBaby, mFilterDate );
    }

}