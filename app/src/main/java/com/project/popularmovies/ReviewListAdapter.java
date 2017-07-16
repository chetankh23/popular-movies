package com.project.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.popularmovies.models.Review;

import java.util.ArrayList;
import java.util.List;


public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewTextViewHolder> {

    private final Context mContext;
    private List<Review> mReviewList;

    public ReviewListAdapter(Context context) {
        mContext = context;
        mReviewList = new ArrayList<Review>();
    }

    @Override
    public ReviewTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.review_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean attachToParent = false;

        View view = layoutInflater.inflate(layoutId, parent, attachToParent);
        return new ReviewListAdapter.ReviewTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewTextViewHolder holder, int position) {
        holder.bind(mReviewList.get(position).getContent(), mReviewList.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        if(mReviewList.size() == 0)
            return 0;

        return mReviewList.size();
    }

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

    public void setReviewList(List<Review> reviewList) {
        mReviewList = reviewList;
        notifyDataSetChanged();
    }
}
