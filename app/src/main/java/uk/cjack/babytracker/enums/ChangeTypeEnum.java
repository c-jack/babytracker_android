package uk.cjack.babytracker.enums;

/**
 * Nappy Change enum
 */
public enum ChangeTypeEnum {
    WET( "wet" ),
    SOILED( "soiled" );

    private final String description;

    ChangeTypeEnum( final String value ) {
        this.description = value;
    }

    /**
     * Bristol scale for stools
     */
    enum BristolScaleEnum {
        TYPE_1( "rabbit droppings" ),
        TYPE_2( "bunch of grapes" ),
        TYPE_3( "corn on the cob" ),
        TYPE_4( "sausage" ),
        TYPE_5( "chicken nuggets" ),
        TYPE_6( "porridge" ),
        TYPE_7( "gravy" );

        private final String description;

        BristolScaleEnum( final String value ) {
            this.description = value;
        }
    }
}