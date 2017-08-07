package com.project.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.popularmovies.interfaces.OnItemClickListener;
import com.project.popularmovies.models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * The class MovieListAdapter exposes list of movies to the Recycler View of Main Activity.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ImageViewHolder> {

    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    private static final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";
    private final Context mContext;
    final List<Target> mTargets = new ArrayList<Target>();

    // A list of movie objects.
    private List<Movie> mMovieList;

    // An on-click handler that is defined to respond to movie item click.
    private OnItemClickListener mOnMovieItemClickListener;

    /**
     * Creates a MovieListAdapter
     * @param context Context of the calling Activity.
     * @param movieItemClickListener The onClick handler for this adapter.
     */
    public MovieListAdapter(Context context, OnItemClickListener movieItemClickListener) {
        mContext = context;
        mOnMovieItemClickListener = movieItemClickListener;
        mMovieList = new ArrayList<Movie>();
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
        int layoutId = R.layout.movie_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean attachToParent = false;

        View view = layoutInflater.inflate(layoutId, parent, attachToParent);
        return new ImageViewHolder(view);
    }

    /**
     * This method is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the
     * movie details for this particular position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item
     *               at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.bind(mMovieList.get(position).getPosterPath());
    }

    /**
     * This method simply returns the number of items to display.
     *
     * @return The number of items available in our movie list.
     */
    @Override
    public int getItemCount() {
        if(mMovieList.size() == 0)
            return 0;

        return mMovieList.size();
    }

    /**
     * Cache of the children views for movie list item.
     */
    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView mMovieImageView;

        public ImageViewHolder(View view) {
            super(view);
            mMovieImageView = (ImageView) view.findViewById(R.id.iv_item_image);
            view.setOnClickListener(this);
        }

        public void bind(String imageURL) {
            String url = MOVIE_IMAGE_BASE_URL + imageURL;
            Log.d(LOG_TAG, url);
            Picasso.with(mContext).load(url).into(mMovieImageView);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked.
         */
        @Override
        public void onClick(View v) {
            int itemClickIndex = getAdapterPosition();
            mOnMovieItemClickListener.onItemClicked(itemClickIndex);
        }



    }

    /**
     * This method is used to set the movie list on MovieListAdapter if we've already
     * created one.
     *
     * @param movieList The new movie list to be displayed.
     */
    public void setmMovieList(List<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }


}
