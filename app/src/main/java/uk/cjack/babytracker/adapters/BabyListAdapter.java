package uk.cjack.babytracker.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.cjack.babytracker.R;
import uk.cjack.babytracker.database.entities.Baby;

public class BabyListAdapter extends RecyclerView.Adapter<BabyListAdapter.BabyViewHolder> {
    private OnItemClicked onClick;
    private List<Baby> mBabyList; // Cached copy of words


    public BabyListAdapter() {
    }

    @NonNull
    @Override
    public BabyViewHolder onCreateViewHolder( @NonNull final ViewGroup parent,
                                              final int viewType ) {

        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from( context );

        // Inflate the custom layout
        final View feed_item_view = inflater.inflate( R.layout.baby_name_item, parent, false );

        // Return a new holder instance
        return new BabyViewHolder( feed_item_view );
    }

    @Override
    public void onBindViewHolder( @NonNull final BabyViewHolder holder, final int position ) {
        if ( mBabyList != null ) {
            final Baby current = mBabyList.get( position );
            holder.babyNameItemView.setText( current.getBabyName() );
            holder.babyNameItemView.setOnClickListener( v -> onClick.onItemClick( position,
                    current )
            );
        }
    }

    public void setBabyList( final List<Baby> babyList ) {
        mBabyList = babyList;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mBabyList has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if ( mBabyList != null ) {
            return mBabyList.size();
        }
        else {
            return 0;
        }
    }

    public List<Baby> getBabyList() {
        return mBabyList;
    }


    /**
     *
     */
    class BabyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final TextView babyNameItemView;

        private BabyViewHolder( final View itemView ) {
            super( itemView );
            babyNameItemView = itemView.findViewById( R.id.babyName );
            itemView.setOnCreateContextMenuListener( this );
        }

        @Override
        public void onCreateContextMenu( final ContextMenu menu, final View v,
                                         final ContextMenu.ContextMenuInfo menuInfo ) {
            final String babyName =
                    mBabyList.get( getLayoutPosition() ).getBabyName();

            menu.setHeaderIcon( R.drawable.baby );
            menu.setHeaderTitle( babyName );
            final int id = mBabyList.get( getLayoutPosition() ).getBabyId();
            menu.add( 0, id, 0, "Edit" );
            menu.add( 0, id, 0, "Delete" );
        }
    }


    public interface OnItemClicked {
        void onItemClick( int position, final Baby current );
    }


    public void setOnClick( final OnItemClicked onClick ) {
        this.onClick = onClick;
    }


}