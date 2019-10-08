package uk.cjack.babytracker.database.repository;

import android.os.AsyncTask;

import uk.cjack.babytracker.database.dao.ActivityDao;
import uk.cjack.babytracker.database.dao.BabyDao;
import uk.cjack.babytracker.database.entities.Activity;
import uk.cjack.babytracker.database.entities.Baby;
import uk.cjack.babytracker.database.enums.DatabaseActionEnum;

/**
 * Async task class
 */
public class BabyAsyncTask extends AsyncTask<Object, Void, Void> {

    private final BabyDao mBabyDao;
    private final ActivityDao mActivityDao;
    private final DatabaseActionEnum mAction;

    BabyAsyncTask( final BabyDao dao, final DatabaseActionEnum action ) {
        mBabyDao = dao;
        mActivityDao = null;
        mAction = action;
    }

    BabyAsyncTask( final ActivityDao dao, final DatabaseActionEnum action ) {
        mBabyDao = null;
        mActivityDao = dao;
        mAction = action;
    }

    /**
     * Common handler for doInBackground
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground( final Object... params ) {
        switch ( mAction ) {
            case INSERT:
                if ( params[0] instanceof Activity && mActivityDao != null ) {
                    mActivityDao.insert( ( Activity ) params[0] );
                }
                else if ( params[0] instanceof Baby && mBabyDao != null ) {
                    mBabyDao.insert( ( Baby ) params[0] );
                }
                break;
            case UPDATE:
                if ( params[0] instanceof Activity && mActivityDao != null ) {
                    mActivityDao.update( ( Activity ) params[0] );
                }
                else if ( params[0] instanceof Baby && mBabyDao != null ) {
                    mBabyDao.update( ( Baby ) params[0] );
                }
                break;
            case DELETE:
                if ( params[0] instanceof Activity && mActivityDao != null ) {
                    mActivityDao.delete( ( Activity ) params[0] );
                }
                else if ( params[0] instanceof Baby && mBabyDao != null ) {
                    mBabyDao.delete( ( Baby ) params[0] );
                }
                break;
        }
        return null;
    }
}