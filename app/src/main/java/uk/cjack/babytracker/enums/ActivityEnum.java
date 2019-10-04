package uk.cjack.babytracker.enums;

import java.util.Objects;

import uk.cjack.babytracker.model.Activity;

public enum ActivityEnum {
    CHANGE( new ActivityConfig("change", null) ), FEED( new ActivityConfig("feed", "ml") );

    private final ActivityConfig config;

    ActivityEnum( final ActivityConfig value ) {
        this.config = value;
    }

    public String getName() {
        return this.config.getName();
    }
    public String getUnit() {
        return this.config.getUnit();
    }
    public ActivityConfig config() {
        return this.config;
    }


    public static ActivityEnum getEnum( final String valueToRetrieve ) {
        for ( final ActivityEnum enumVal :
                Objects.requireNonNull( ActivityEnum.class.getEnumConstants() ) ) {
            if ( enumVal.getName().equalsIgnoreCase( valueToRetrieve ) ) {
                return enumVal;
            }
        }
        return null;
    }



    /**
     *
     */
    public static class ActivityConfig {
        private final String name;
        private final String unit;

        ActivityConfig( final String name, final String unit )
        {
            this.name = name;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public String getUnit() {
            return unit;
        }
    }
}
