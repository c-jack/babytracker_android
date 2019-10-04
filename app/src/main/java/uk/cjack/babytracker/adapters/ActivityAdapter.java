package uk.cjack.babytracker.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.cjack.babytracker.R;
import uk.cjack.babytracker.enums.ActivityEnum;
import uk.cjack.babytracker.model.Activity;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private Context context;
    private List<Activity> mActivityList;
    private String mActivityDate;

    public ActivityAdapter( final Context context, final List<Activity> feedList ) {
        this.context = context;
        this.mActivityList = feedList;
    }


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    @NonNull
    public ActivityAdapter.ViewHolder onCreateViewHolder( @NonNull final ViewGroup parent,
                                                          final int viewType ) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from( context );

        // Inflate the custom layout
        final View feed_item_view = inflater.inflate( R.layout.baby_activity_item, parent, false );

        // Return a new holder instance
        return new ViewHolder( feed_item_view );
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder( @NonNull final ActivityAdapter.ViewHolder viewHolder,
                                  final int position ) {
        // Get the data model based on position
        final Activity activity = mActivityList.get( position );
        final ViewGroup.LayoutParams lp = viewHolder.itemView.getLayoutParams();

        if ( mActivityDate == null ) {
            mActivityDate = activity.getActivityDate();
            viewHolder.activityDateTextView.setText( activity.getActivityDate() );
            lp.height = lp.height * 2;
        }
        else if ( !activity.getActivityDate().equals( mActivityDate ) ) {
            mActivityDate = activity.getActivityDate();
            viewHolder.activityDateTextView.setText( activity.getActivityDate() );
            lp.height = lp.height * 2;
        }

        // Set item views based on your views and data model
        final TextView feedAmountTextView = viewHolder.activityValueTextView;
        final TextView feedTimeTextView = viewHolder.activityTimeTextView;
        final ImageView activityIcon = viewHolder.activityIconTextView;

        final ActivityEnum activityType = activity.getActivityType();
        if ( activityType != null ) {
            final int drawableActivityIcon;
            switch ( activityType ) {
                case FEED:
                    drawableActivityIcon = R.drawable.feed;
                    final String feedAmount = activity.getActivityValue() + activityType.getUnit();
                    feedAmountTextView.setText( feedAmount );
                    break;
                case CHANGE:
                    drawableActivityIcon = R.drawable.nappy;
                    feedAmountTextView.setText( String.valueOf( activity.getActivityValue() ) );
                    break;
                default:
                    drawableActivityIcon = R.drawable.unknown;
                    break;
            }
            activityIcon.setImageResource( drawableActivityIcon );
        }
        feedTimeTextView.setText( activity.getActivityTime() );

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mActivityList.size();
    }


    /**
     * @param activityList
     */
    public void notify( final List<Activity> activityList ) {
        if ( mActivityList != null ) {
            mActivityList.clear();
            mActivityList.addAll( activityList );

        }
        else {
            mActivityList = activityList;
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView activityDateTextView;
        TextView activityTimeTextView;
        TextView activityValueTextView;
        ImageView activityIconTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder( final View itemView ) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super( itemView );

            itemView.setOnCreateContextMenuListener( this );

            activityDateTextView = itemView.findViewById( R.id.activityDate );
            activityTimeTextView = itemView.findViewById( R.id.activityTime );
            activityValueTextView = itemView.findViewById( R.id.activityValue );
            activityIconTextView = itemView.findViewById( R.id.activityIcon );


        }

        @Override
        public void onCreateContextMenu( final ContextMenu menu, final View v,
                                         final ContextMenu.ContextMenuInfo menuInfo ) {
            final Activity selectedActivity = mActivityList.get( getLayoutPosition() );

            menu.add( 0, selectedActivity.getActivityId(), 0, "Edit" );//groupId, itemId, order,
            // title
            menu.add( 0, selectedActivity.getActivityId(), 0, "Delete" );
        }
    }
}