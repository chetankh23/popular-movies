package com.project.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.popularmovies.interfaces.OnItemClickListener;
import com.project.popularmovies.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * The class TrailerListAdapter displays list of trailers for a particular movie.
 */
class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ImageViewHolder> {

    private static final String TRAILER_IMAGE_BASE_URL = "http://img.youtube.com/vi/";
    private final Context mContext;
    private List<Trailer> mTrailerList;
    private OnItemClickListener onItemClickListener;

    /**
     * Creates a TrailerListAdapter
     * @param context Context of the calling Activity.
     * @param itemClickListener The onClick handler for this adapter.
     *
     */
    public TrailerListAdapter(Context context, OnItemClickListener itemClickListener) {
        mContext = context;
        onItemClickListener = itemClickListener;
        mTrailerList = new ArrayList<Trailer>();
    }

    /**
     * This method is called when each new ViewHolder is created. As many ViewHolders will
     * be created enough to fill the screen and allow for scrolling.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType Used in case the RecyclerView has more than one type of item.
     *
     * @return A new ImageViewHolder that holds the view for each list item.
     */
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.trailer_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean attachToParent = false;

        View view = layoutInflater.inflate(layoutId, parent, attachToParent);
        return new TrailerListAdapter.ImageViewHolder(view);
    }

    /**
     * This method is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the
     * trailer details for this particular position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item
     *               at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.bind(mTrailerList.get(position).getTrailerKey(), mTrailerList.get(position).getTrailerName());
    }

    /**
     * This method simply returns the number of items to display.
     *
     * @return The number of items available in our trailer list.
     */
    @Override
    public int getItemCount() {
        if(mTrailerList.size() == 0)
            return 0;

        return mTrailerList.size();
    }

    /**
     * Cache of the children views for trailer list item.
     */
    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView trailerImageView;
        final TextView trailerNameTextView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            trailerImageView = (ImageView) itemView.findViewById(R.id.iv_trailer_thumbnail);
            trailerNameTextView = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            itemView.setOnClickListener(this);
        }

        public void bind(String trailerImageUrl, String trailerName) {
            Picasso.with(mContext).load(TRAILER_IMAGE_BASE_URL+trailerImageUrl+mContext.getString(R.string.trailerImageFormat)).into(trailerImageView);
            trailerNameTextView.setText(trailerName);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onItemClickListener.onItemClicked(position);
        }
    }

    /**
     * This method is used to set the trailer list on TrailerListAdapter if we've already
     * created one.
     *
     * @param trailerList The new trailer list to be displayed.
     */
    public void setTrailerList(List<Trailer> trailerList) {
        mTrailerList = trailerList;
        notifyDataSetChanged();
    }
}
