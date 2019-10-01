package uk.cjack.babytracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import uk.cjack.babytracker.R;
import uk.cjack.babytracker.model.Feed;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private Context context;
    private List<Feed> mFeedList;

    public FeedAdapter( final Context context, final List<Feed> feedList ) {
        this.context = context;
        this.mFeedList = feedList;
    }


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    @NonNull
    public FeedAdapter.ViewHolder onCreateViewHolder( @NonNull final ViewGroup parent, final int viewType ) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from( context );

        // Inflate the custom layout
        final View feed_item_view = inflater.inflate( R.layout.baby_feed_item, parent, false );

        // Return a new holder instance
        return new ViewHolder( feed_item_view );
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder( final FeedAdapter.ViewHolder viewHolder, final int position ) {
        // Get the data model based on position
        final Feed feed = mFeedList.get( position );

        // Set item views based on your views and data model
        final TextView feedAmountTextView = viewHolder.feedAmountTextView;
        feedAmountTextView.setText( String.valueOf( feed.getFeedAmount() ) );
        final TextView feedDateTextView = viewHolder.feedDateTextView;
        feedDateTextView.setText( String.valueOf( feed.getFeedDate() ) );
        final TextView feedTimeTextView = viewHolder.feedTimeTextView;
        feedTimeTextView.setText( String.valueOf( feed.getFeedTime() ) );
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mFeedList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView feedDateTextView;
        TextView feedTimeTextView;
        TextView feedAmountTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder( final View itemView ) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super( itemView );

            feedDateTextView = itemView.findViewById( R.id.feedDate );
            feedTimeTextView = itemView.findViewById( R.id.feedTime );
            feedAmountTextView = itemView.findViewById( R.id.feedAmount );
        }
    }
}