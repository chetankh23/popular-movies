package com.project.popularmovies.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.popularmovies.R;
import com.project.popularmovies.ReviewListAdapter;
import com.project.popularmovies.TrailerListAdapter;
import com.project.popularmovies.databinding.ActivityMovieDetailBinding;
import com.project.popularmovies.interfaces.OnItemClickListener;
import com.project.popularmovies.interfaces.ResponseHandler;
import com.project.popularmovies.models.Movie;
import com.project.popularmovies.models.Review;
import com.project.popularmovies.models.Trailer;
import com.project.popularmovies.utilities.AppUtils;
import com.project.popularmovies.utilities.GetMovieTask;
import com.project.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MovieDetailFragment extends Fragment implements OnItemClickListener, ResponseHandler{

    Movie mMovieDetail;
    ActivityMovieDetailBinding mBinding;
    List<Trailer> mTrailerList;
    List<Review> mReviewList;
    TrailerListAdapter mTrailerListAdapter;
    ReviewListAdapter mReviewListAdapter;

    // Backdrop Image Base URL of movie.
    private static final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w780";

    // Poster image URL of movie.
    private static final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    // Movie trailer Base URL.
    private static final String MOVIE_TRAILER_BASE_URI = "https://www.youtube.com/watch?v=";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_movie_detail, container, false);
        View rootView = mBinding.getRoot();
        loadUiWithDetails();
        return rootView;
    }

    /**
     * The method loadUiWithDetails() displays the details of the movie such as Title,
     * Release Date, Rating and Plot Synoposis of the movie.
     */
    private void loadUiWithDetails() {
        mBinding.collapsingToolbar.setTitle(mMovieDetail.getTitle());
        mBinding.collapsingToolbar.setCollapsedTitleTextAppearance(R.style.coll_toolbar_title);
        mBinding.collapsingToolbar.setExpandedTitleTextAppearance(R.style.exp_toolbar_title);
        Picasso.with(getActivity()).load(BACKDROP_BASE_URL + mMovieDetail.getBackdropPath()).into(mBinding.ivMovieBackdropImage);
        Picasso.with(getActivity()).load(MOVIE_IMAGE_BASE_URL + mMovieDetail.getPosterPath()).into(mBinding.movieInfoLayout.ivMovieThumbnail);
        mBinding.movieInfoLayout.tvMovieTitle.setText(mMovieDetail.getTitle());
        mBinding.movieInfoLayout.tvReleaseDate.setText(AppUtils.convertDateToCustomFormat(mMovieDetail.getReleaseDate()));
        mBinding.movieInfoLayout.tvMovieRating.setText(mMovieDetail.getVoteAverage() + getString(R.string.out_of_rating));
        mBinding.movieInfoLayout.tvMovieSynopsis.setText(mMovieDetail.getOverView());
        createTrailerListUI();
        createReviewListUI();
    }

    /**
     * Initializes the requisite UI to display list of trailers. This includes
     * initializing the RecyclerView, TrailerListAdapter and trailerList.
     */
    private void createTrailerListUI() {

        mTrailerList = new ArrayList<Trailer>();

        // The TrailerListAdapter is responsible for linking the trailer data with the views
        // that will end up displaying the trailer data.
        mTrailerListAdapter = new TrailerListAdapter(getActivity(), MovieDetailFragment.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.movieInfoLayout.rvTrailerList.setLayoutManager(layoutManager);
        mBinding.movieInfoLayout.rvTrailerList.setHasFixedSize(true);

        // Setting the adapter attaches it to the RecyclerView in our layout.
        mBinding.movieInfoLayout.rvTrailerList.setAdapter(mTrailerListAdapter);
    }

    /**
     * Initializes the requisite UI to display list of reviews. This includes
     * initializing the RecyclerView, ReviewListAdapter and reviewList.
     */
    private void createReviewListUI() {

        mReviewList = new ArrayList<Review>();

        // The ReviewListAdapter is responsible for linking the review data with the views
        // that will end up displaying the review data.
        mReviewListAdapter = new ReviewListAdapter(getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.movieInfoLayout.rvReviewList.setLayoutManager(layoutManager);
        mBinding.movieInfoLayout.rvReviewList.setHasFixedSize(true);

        // Setting the adapter attaches it to the RecyclerView in our layout.
        mBinding.movieInfoLayout.rvReviewList.setAdapter(mReviewListAdapter);
    }

    /**
     * Builds a URL and calls webservice API to retrieve list of trailers.
     */
    private void loadMovieTrailers() {
        URL trailersUrl = NetworkUtils.buildTrailersURL(mMovieDetail.getMovieId());
        GetMovieTask movieTask = new GetMovieTask(null, this, getString(R.string.trailers));
        movieTask.execute(trailersUrl);
    }

    /**
     * Builds a URL and calls webservice API to retrieve list of reviews.
     */
    private void loadReviews() {
        URL reviewsUrl = NetworkUtils.buildReviewsURL(mMovieDetail.getMovieId());
        GetMovieTask movieTask = new GetMovieTask(null, this, getString(R.string.reviews));
        movieTask.execute(reviewsUrl);
    }

    /*
    public void actionFavorites(View view) {
        if(!isFavorite) {
            addMovieToFavorites();
        } else {
            removeMovieFromFavorites();
        }
    }*/

    public void actionShare(View view) {
        createShareIntent();
    }

    /**
     * Creates a share intent to share movie's first trailer URL.
     */
    private void createShareIntent() {

        if(mTrailerList != null && mTrailerList.size() > 0) {
            String mimeType = getString(R.string.mimeType_to_share);
            String title = getString(R.string.share_title);
            String textToShare = Uri.parse(MOVIE_TRAILER_BASE_URI + mTrailerList.get(0).getTrailerKey()).toString();
            ShareCompat.IntentBuilder.from(getActivity())
                    .setType(mimeType)
                    .setChooserTitle(title)
                    .setText(textToShare)
                    .startChooser();
        }
    }

    public Movie getmMovieDetail() {
        return mMovieDetail;
    }

    public void setmMovieDetail(Movie mMovieDetail) {
        this.mMovieDetail = mMovieDetail;
    }

    @Override
    public void onItemClicked(int itemClickIndex) {
        Trailer trailer = mTrailerList.get(itemClickIndex);
        if(trailer != null) {

            // Create an implicit intent to view the movie trailer.
            Uri videoUri = Uri.parse(MOVIE_TRAILER_BASE_URI + trailer.getTrailerKey());
            Intent playVideoIntent = new Intent(Intent.ACTION_VIEW, videoUri);
            startActivity(playVideoIntent);
        }
    }

    @Override
    public void handleResponse(String response, String type) {
        if(type.equals(getString(R.string.trailers))) {
            mTrailerList = AppUtils.createTrailerListFromResponse(response);
            mTrailerListAdapter.setTrailerList(mTrailerList);
        } else if(type.equals(getString(R.string.reviews))) {
            List<Review> reviewList = AppUtils.createReviewListFromResponse(response);
            mReviewListAdapter.setReviewList(reviewList);
            if(reviewList.size() > 0) {
                mBinding.movieInfoLayout.tvNoReviews.setVisibility(View.GONE);
                mBinding.movieInfoLayout.rvReviewList.setVisibility(View.VISIBLE);
            } else {
                mBinding.movieInfoLayout.tvNoReviews.setVisibility(View.VISIBLE);
                mBinding.movieInfoLayout.rvReviewList.setVisibility(View.GONE);
            }
        }
    }
}
