package uk.cjack.babytracker.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import uk.cjack.babytracker.database.entities.Baby;
import uk.cjack.babytracker.view.ActivityViewModel;
import uk.cjack.babytracker.view.BabyViewModel;

public class BabyViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;

    public BabyViewModelFactory( final Application application ) {
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create( @NonNull final Class<T> modelClass ) {
        return ( T ) new BabyViewModel( mApplication );
    }

}