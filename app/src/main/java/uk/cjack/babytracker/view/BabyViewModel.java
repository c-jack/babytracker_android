package uk.cjack.babytracker.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import uk.cjack.babytracker.database.entities.Baby;
import uk.cjack.babytracker.database.repository.BabyRepository;

public class BabyViewModel extends AndroidViewModel {

    private final BabyRepository mBabyRepository;
    private final LiveData<List<Baby>> mAllBabies;


    public BabyViewModel ( final Application application) {
        super(application);
        mBabyRepository = new BabyRepository(application);
        mAllBabies = mBabyRepository.getAllBabies();
    }

    public LiveData<List<Baby>> getAllBabies() { return mAllBabies; }

    public void insert(final Baby baby) { mBabyRepository.insert(baby); }
    public void delete(final Baby baby) { mBabyRepository.delete(baby); }
    public void update(final Baby baby) { mBabyRepository.update(baby); }
}
