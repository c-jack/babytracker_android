package uk.cjack.babytracker.utils;

import java.util.Calendar;

public class BabyTrackerUtils {

    /**
     * Is the Calendar object today?
     *
     * @param cal
     * @return
     */
    public static boolean isToday( final Calendar cal ) {
        if ( cal == null ) {
            throw new IllegalArgumentException( "The dates must not be null" );
        }
        final Calendar today = Calendar.getInstance();
        return ( cal.get( Calendar.ERA ) == today.get( Calendar.ERA ) &&
                cal.get( Calendar.YEAR ) == today.get( Calendar.YEAR ) &&
                cal.get( Calendar.DAY_OF_YEAR ) == today.get( Calendar.DAY_OF_YEAR ) );
    }
}
