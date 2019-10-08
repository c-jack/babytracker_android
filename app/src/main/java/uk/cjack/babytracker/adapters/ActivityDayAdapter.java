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
import uk.cjack.babytracker.database.entities.Activity;
import uk.cjack.babytracker.enums.ActivityEnum;
import uk.cjack.babytracker.model.DayActivityTotals;

public class ActivityDayAdapter extends RecyclerView.Adapter<ActivityDayAdapter.ViewHolder> {

    private List<Activity> mActivityList;
    private List<DayActivityTotals> mDayActivityTotalList;
    private Context mContext;

    public ActivityDayAdapter( final Context context ) {
        this.mContext = context;
    }


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    @NonNull
    public ActivityDayAdapter.ViewHolder onCreateViewHolder( @NonNull final ViewGroup parent,
                                                             final int viewType ) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from( context );

        // Inflate the custom layout
        final View feed_item_view = inflater.inflate( R.layout.activity_screen_item_bk, parent,
                false );

        // Return a new holder instance
        return new ViewHolder( feed_item_view );
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder( @NonNull final ActivityDayAdapter.ViewHolder viewHolder,
                                  final int position ) {
        // Get the data model based on position
        final DayActivityTotals activity = mDayActivityTotalList.get( position );

        // Set item views based on your views and data model
        final TextView activityFeedAmountTextView = viewHolder.activityFeedAmountTextView;
        final TextView activityDateTextView = viewHolder.activityDateTextView;
        final TextView activityChangeQtyTextView = viewHolder.activityChangeQtyTextView;

        activityDateTextView.setText( activity.getActivityDate() );
        activityFeedAmountTextView.setText( String.format( "%s%s", activity.getFeedTotal(),
                ActivityEnum.FEED.getUnit() ) );
        final String totalChanges =
                String.valueOf( ( Integer.valueOf( activity.getSoiledNappies() ) + Integer.valueOf( activity.getWetNappies() ) ) );
        activityChangeQtyTextView.setText( totalChanges );


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {

        if ( mDayActivityTotalList != null ) {
            return mDayActivityTotalList.size();
        }
        else {
            return 0;
        }
    }

    public void setActivityList( final List<Activity> activityList ) {
        mActivityList = activityList;
        notifyDataSetChanged();
    }

    public List<Activity> getActivityList() {
        return mActivityList;
    }

    public void setDayActivityList( final List<DayActivityTotals> dayActivityTotalList ) {
        mDayActivityTotalList = dayActivityTotalList;
        notifyDataSetChanged();
    }

    public List<DayActivityTotals> getActivitySummaryList() {
        return mDayActivityTotalList;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView activityDateTextView;
        TextView activityFeedAmountTextView;
        TextView activityChangeQtyTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder( final View itemView ) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super( itemView );

            itemView.setOnCreateContextMenuListener( this );

            activityDateTextView = itemView.findViewById( R.id.daily_date );
            activityFeedAmountTextView = itemView.findViewById( R.id.daily_feed_amnt );
            activityChangeQtyTextView = itemView.findViewById( R.id.daily_change_qty );


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