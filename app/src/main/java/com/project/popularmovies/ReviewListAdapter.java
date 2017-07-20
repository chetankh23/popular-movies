package com.project.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.popularmovies.models.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * The class ReviewListAdapter displays list of reviews for a particular movie.
 */
public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewTextViewHolder> {

    private final Context mContext;
    private List<Review> mReviewList;

    /**
     * Creates a ReviewListAdapter
     * @param context Context of the calling Activity.
     *
     */
    public ReviewListAdapter(Context context) {
        mContext = context;
        mReviewList = new ArrayList<Review>();
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
    public ReviewTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.review_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean attachToParent = false;

        View view = layoutInflater.inflate(layoutId, parent, attachToParent);
        return new ReviewListAdapter.ReviewTextViewHolder(view);
    }

    /**
     * This method is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the
     * review details for this particular position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item
     *               at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ReviewTextViewHolder holder, int position) {
        holder.bind(mReviewList.get(position).getContent(), mReviewList.get(position).getAuthor());
    }

    /**
     * This method simply returns the number of items to display.
     *
     * @return The number of items available in our review list.
     */
    @Override
    public int getItemCount() {
        if(mReviewList.size() == 0)
            return 0;

        return mReviewList.size();
    }

    /**
     * Cache of the children views for review list item.
     */
    public class ReviewTextViewHolder extends RecyclerView.ViewHolder {

        final TextView reviewContentTextView;
        final TextView reviewAuthorTextView;

        public ReviewTextViewHolder(View itemView) {
            super(itemView);
            reviewContentTextView = (TextView) itemView.findViewById(R.id.tv_review_content);
            reviewAuthorTextView = (TextView) itemView.findViewById(R.id.tv_review_author);
        }

        public void bind(String reviewContent, String reviewAuthor) {
            reviewContentTextView.setText(reviewContent);
            reviewAuthorTextView.setText(reviewAuthor);
        }
    }

    /**
     * This method is used to set the review list on ReviewListAdapter if we've already
     * created one.
     *
     * @param reviewList The new review list to be displayed.
     */
    public void setReviewList(List<Review> reviewList) {
        mReviewList = reviewList;
        notifyDataSetChanged();
    }
}
