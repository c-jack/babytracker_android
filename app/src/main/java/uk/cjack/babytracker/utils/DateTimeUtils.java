package uk.cjack.babytracker.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    private static final String DATE_FORMAT = "EEEE d MMM yyyy";
    private static final String TIME_FORMAT = "HH:mm a";

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



    /**
     * @param dateToConvert
     * @return
     */
    public static String getTimeFromDate( final Date dateToConvert ) {
        final DateFormat timeFormat = new SimpleDateFormat( TIME_FORMAT, Locale.getDefault() );
        return timeFormat.format( dateToConvert );
    }

    /**
     * @param dateToConvert
     * @return
     */
    public static String getDateStringFromDate( final Date dateToConvert ) {
        final DateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT, Locale.getDefault() );
        return dateFormat.format( dateToConvert );
    }
}
