package uk.cjack.babytracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import uk.cjack.babytracker.R;
import uk.cjack.babytracker.model.Baby;

public class BabyAdapter extends ArrayAdapter<Baby> {
    private List<Baby> lBabies;
    private static LayoutInflater inflater = null;

    public BabyAdapter( final Activity activity, final int textViewResourceId,
                        final ArrayList<Baby> _lBabies ) {
        super( activity, textViewResourceId );
        try {
            this.lBabies = _lBabies;

            inflater =
                    ( LayoutInflater ) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        }
        catch ( final Exception e ) {
            // TODO
        }
    }

    public int getCount() {
        return lBabies.size();
    }

    public long getItemId( final int position ) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_name;

    }

    /**
     * @param babyList
     */
    public void notify( final List<uk.cjack.babytracker.model.Baby> babyList ) {
        if ( lBabies != null ) {
            lBabies.clear();
            lBabies.addAll( babyList );

        }
        else {
            lBabies = babyList;
        }
        notifyDataSetChanged();
    }

    public View getView( final int position, final View convertView, @NonNull ViewGroup parent ) {
        View view = convertView;
        final ViewHolder holder;
        try {
            if ( convertView == null ) {
                view = inflater.inflate( R.layout.baby_name_item, null );
                holder = new ViewHolder();

                holder.display_name = view.findViewById( R.id.babyName );


                view.setTag( holder );
            }
            else {
                holder = ( ViewHolder ) view.getTag();
            }

            holder.display_name.setText( lBabies.get( position ).getBabyName() );

        }
        catch ( final Exception e ) {
        // TODO
        }
        return view;
    }
}