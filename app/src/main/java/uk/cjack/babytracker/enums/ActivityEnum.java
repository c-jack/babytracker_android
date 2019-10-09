package uk.cjack.babytracker.enums;

import java.util.Objects;

import uk.cjack.babytracker.R;

public enum ActivityEnum {
    CHANGE( new ActivityConfig( "change", null, R.drawable.grey_nappy ) ),
    FEED( new ActivityConfig( "feed", "ml", R.drawable.grey_baby_bottle ) );

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
        private final int resourceImage;

        ActivityConfig( final String name, final String unit, final int resourceImage ) {
            this.name = name;
            this.unit = unit;
            this.resourceImage = resourceImage;
        }

        public String getName() {
            return name;
        }

        public String getUnit() {
            return unit;
        }

        public int getResourceImage() {
            return resourceImage;
        }

    }
}
