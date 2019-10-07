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
import uk.cjack.babytracker.database.entities.Activity;
import uk.cjack.babytracker.enums.ActivityEnum;
import uk.cjack.babytracker.enums.ChangeTypeEnum;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private static final int CLEAR_IMAGE = android.R.color.transparent;
    private List<Activity> mActivityList;
    private String mActivityDate;
    private Context mContext;

    public ActivityAdapter( final Context context ) {
        this.mContext = context;
    }


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    @NonNull
    public ActivityAdapter.ViewHolder onCreateViewHolder( @NonNull final ViewGroup parent,
                                                          final int viewType ) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from( context );

        // Inflate the custom layout
        final View feed_item_view = inflater.inflate( R.layout.activity_item, parent, false );

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

        if ( !activity.getActivityDate().equals( mActivityDate )) {
            mActivityDate = activity.getActivityDate();
            viewHolder.activityDateTextView.setText( activity.getActivityDate() );
            lp.height = ( int ) mContext.getResources().getDimension( R.dimen.activity_height_with_date );
        }
        else {
            lp.height = ( int ) mContext.getResources().getDimension( R.dimen.activity_height );
        }

        // Set item views based on your views and data model
        final TextView activityValueTextView = viewHolder.activityValueTextView;
        final TextView activityTimeTextView = viewHolder.activityTimeTextView;
        final ImageView activityIcon = viewHolder.activityIconTextView;
        final ImageView nappyChangeTypeIcon = viewHolder.nappyChangeIcon;

        // Clear the icons and values first
        nappyChangeTypeIcon.setImageResource( CLEAR_IMAGE );
        activityIcon.setImageResource( CLEAR_IMAGE );
        activityValueTextView.setText( null );

        final ActivityEnum activityType = ActivityEnum.getEnum( activity.getActivityTypeValue() );
        final int drawableActivityIcon;
        int changeValueIcon = CLEAR_IMAGE;
        if ( activityType != null ) {
            drawableActivityIcon = activityType.config().getResourceImage();
            switch ( activityType ) {
                case FEED:
                    final String feedAmount = activity.getActivityValue() + activityType.getUnit();
                    activityValueTextView.setText( feedAmount );
                    break;
                case CHANGE:
                    final ChangeTypeEnum changeTypeEnum =
                            ChangeTypeEnum.getEnum( activity.getActivityValue() );
                    if ( changeTypeEnum != null ) {
                        changeValueIcon = changeTypeEnum.getConfig().getResourceImage();
                    }
                    break;
            }
        }
        else {
            drawableActivityIcon = R.drawable.unknown;
        }

        nappyChangeTypeIcon.setImageResource( changeValueIcon );
        nappyChangeTypeIcon.setVisibility( View.VISIBLE );

        activityIcon.setImageResource( drawableActivityIcon );
        activityTimeTextView.setText( activity.getActivityTime() );

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {

        if ( mActivityList != null ) {
            return mActivityList.size();
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView activityDateTextView;
        TextView activityTimeTextView;
        TextView activityValueTextView;
        ImageView activityIconTextView;
        ImageView nappyChangeIcon;

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
            nappyChangeIcon = itemView.findViewById( R.id.nappyChangeTypeIcon );


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