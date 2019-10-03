package uk.cjack.babytracker.utils;

import android.text.Editable;
import android.text.TextWatcher;

public class MaskWatcher implements TextWatcher {
    private boolean isRunning = false;
    private boolean isDeleting = false;
    private final String mask;

    public MaskWatcher( final String mask ) {
        this.mask = mask;
    }

    public static MaskWatcher buildCpf() {
        return new MaskWatcher( "###.###.###-##" );
    }

    @Override
    public void beforeTextChanged( CharSequence charSequence, int start, int count, int after ) {
        isDeleting = count > after;
    }

    @Override
    public void onTextChanged( CharSequence charSequence, int start, int before, int count ) {
    }

    @Override
    public void afterTextChanged( final Editable editable ) {
        if ( isRunning || isDeleting ) {
            return;
        }
        isRunning = true;

        editable.append( mask );

        isRunning = false;
    }
}
