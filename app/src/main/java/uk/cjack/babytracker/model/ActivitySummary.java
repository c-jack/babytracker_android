package uk.cjack.babytracker.model;

import androidx.room.ColumnInfo;

public class ActivitySummary {
    @ColumnInfo( name = "groupValue" )
    private String groupValue;

    @ColumnInfo( name = "resultValue" )
    private String resultValue;

    public String getGroupValue() {
        return groupValue;
    }

    public void setGroupValue( final String groupValue ) {
        this.groupValue = groupValue;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue( final String resultValue ) {
        this.resultValue = resultValue;
    }
}
