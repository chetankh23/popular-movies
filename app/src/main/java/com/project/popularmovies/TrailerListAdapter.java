package com.project.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.popularmovies.interfaces.OnMovieItemClickListener;
import com.project.popularmovies.models.Movie;
import com.project.popularmovies.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ImageViewHolder> {

    private static final String TRAILER_IMAGE_BASE_URL = "http://img.youtube.com/vi/";
    private final Context mContext;
    private List<Trailer> mTrailerList;
    private OnMovieItemClickListener onMovieItemClickListener;

    public TrailerListAdapter(Context context, OnMovieItemClickListener itemClickListener) {
        mContext = context;
        onMovieItemClickListener = itemClickListener;
        mTrailerList = new ArrayList<Trailer>();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.trailer_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean attachToParent = false;

        View view = layoutInflater.inflate(layoutId, parent, attachToParent);
        return new TrailerListAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.bind(mTrailerList.get(position).getTrailerKey(), mTrailerList.get(position).getTrailerName());
    }

    @Override
    public int getItemCount() {
        if(mTrailerList.size() == 0)
            return 0;

        return mTrailerList.size();
    }

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
            onMovieItemClickListener.onMovieItemClicked(position);
        }
    }

    public void setTrailerList(List<Trailer> trailerList) {
        mTrailerList = trailerList;
        notifyDataSetChanged();
    }
}
