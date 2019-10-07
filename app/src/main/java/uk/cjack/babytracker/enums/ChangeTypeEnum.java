package uk.cjack.babytracker.enums;

import java.util.Objects;

import uk.cjack.babytracker.R;

/**
 * Nappy Change enum
 */
public enum ChangeTypeEnum {
    WET( new ChangeConfig("wet", R.drawable.wet_yellow_filled, R.id.rb_wetNappy )  ),
    SOILED( new ChangeConfig( "soiled", R.drawable.poo_brown_fill, R.id.rb_soiledNappy ) );

    private final ChangeConfig config;

    ChangeTypeEnum( final ChangeConfig value ) {
        this.config = value;
    }

    public static ChangeTypeEnum getEnum( final String valueToRetrieve ) {
        for ( final ChangeTypeEnum enumVal :
                Objects.requireNonNull( ChangeTypeEnum.class.getEnumConstants() ) ) {
            if ( enumVal.getConfig().getDescription().equalsIgnoreCase( valueToRetrieve ) ) {
                return enumVal;
            }
        }
        return null;
    }

    public ChangeConfig getConfig() {
        return config;
    }

    public String getDescription() {
        return config.description;
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

    public static class ChangeConfig
        {
         private String description;
         private int resourceImage;
         private int radioButtonId;


            ChangeConfig( final String description, final int resourceImage, final int radioButtonId ) {
                this.description = description;
                this.resourceImage = resourceImage;
                this.radioButtonId = radioButtonId;
            }

            String getDescription() {
                return description;
            }

            public int getResourceImage() {
                return resourceImage;
            }

            public int getRadioButtonId() {
                return radioButtonId;
            }
        }
}