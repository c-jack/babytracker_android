package uk.cjack.babytracker.database.db;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampConverter {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @TypeConverter
    public static Date fromTimestamp( final String value) {
        if (value != null) {
            try {
                return dateFormat.parse(value);
            } catch ( final ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String toTimestamp( final Date dateValue) {
        if (dateValue != null) {
            return dateFormat.format( dateValue );
        } else {
            return null;
        }
    }
}