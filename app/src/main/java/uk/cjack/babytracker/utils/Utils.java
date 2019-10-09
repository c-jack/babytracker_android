package uk.cjack.babytracker.utils;

import android.widget.Toast;

import uk.cjack.babytracker.MainActivity;

public class Utils {



    /**
     * Alert sample
     */
    private void showAlert() {
//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder();
//        alertDialogBuilder
//                .setMessage( "Test" );
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
    }



    /**
     * Toast sample
     *
     * @param msg message to display in the toast
     * @param context
     */
    public static void makeToast( final MainActivity context, final String msg) {
        Toast.makeText( context, msg, Toast.LENGTH_SHORT ).show();
    }
}
