package com.project.popularmovies;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.ColorRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

/*
* The class MovieDetailActivity displays the details of a particular movie when selects
* one from the movie list.
*/
public class MovieDetailActivity extends AppCompatActivity implements ResponseHandler{

    private Movie mMovieDetail;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private TrailerListAdapter trailerListAdapter;
    private ReviewListAdapter reviewListAdapter;
    private List<Trailer> trailerList;
    private List<Review> reviewList;

    private static final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w780";
    private static final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail2);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent getMovieIntent = getIntent();
        if(getMovieIntent != null) {
            mMovieDetail = getMovieIntent.getParcelableExtra(getString(R.string.extra_movie_detail));
            if(mMovieDetail != null) {
                displayMovieDetails();
                loadMovieTrailers();
                loadReviews();
            }
        }
    }

    /*
    * The method displayMovieDetails() displays the details of the movie such as Title,
    * Release Date, Rating and Plot Synoposis of the movie.
    */
    private void displayMovieDetails() {
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ImageView mMovieBackdropImageView = (ImageView) findViewById(R.id.iv_movie_backdrop_image);
        ImageView mMovieImageThumbnail = (ImageView) findViewById(R.id.iv_movie_thumbnail);
        TextView mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        TextView mMovieReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        TextView mMovieRating = (TextView) findViewById(R.id.tv_movie_rating);
        TextView mMovieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);

        collapsingToolbar.setTitle(mMovieDetail.getTitle());
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.coll_toolbar_title);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.exp_toolbar_title);
        Picasso.with(this).load(BACKDROP_BASE_URL + mMovieDetail.getBackdropPath()).into(mMovieBackdropImageView);
        Picasso.with(this).load(MOVIE_IMAGE_BASE_URL + mMovieDetail.getPosterPath()).into(mMovieImageThumbnail);
        mMovieTitle.setText(mMovieDetail.getTitle());
        mMovieReleaseDate.setText(AppUtils.convertDateToCustomFormat(mMovieDetail.getReleaseDate()));
        mMovieRating.setText(mMovieDetail.getVoteAverage() + getString(R.string.out_of_rating));
        mMovieSynopsis.setText(mMovieDetail.getOverView());
        initTrailerList();
        initReviewList();
    }

    private void initTrailerList() {

        trailerList = new ArrayList<Trailer>();

        // The MovieListAdapter is responsible for linking the movie data with the views
        // that will end up displaying the movie data.
        trailerListAdapter = new TrailerListAdapter(this);
        RecyclerView trailerListRecyclerView = (RecyclerView) findViewById(R.id.rv_trailer_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        trailerListRecyclerView.setLayoutManager(layoutManager);
        trailerListRecyclerView.setHasFixedSize(true);

        // Setting the adapter attaches it to the RecyclerView in our layout.
        trailerListRecyclerView.setAdapter(trailerListAdapter);

    }

    private void initReviewList() {

        reviewList = new ArrayList<Review>();

        // The MovieListAdapter is responsible for linking the movie data with the views
        // that will end up displaying the movie data.
        reviewListAdapter = new ReviewListAdapter(this);
        RecyclerView reviewListRecyclerView = (RecyclerView) findViewById(R.id.rv_review_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewListRecyclerView.setLayoutManager(layoutManager);
        reviewListRecyclerView.setHasFixedSize(true);

        // Setting the adapter attaches it to the RecyclerView in our layout.
        reviewListRecyclerView.setAdapter(reviewListAdapter);

    }

    private void loadMovieTrailers() {
        URL trailersUrl = NetworkUtils.buildTrailersURL(mMovieDetail.getMovieId());
        GetMovieTask movieTask = new GetMovieTask(this, "trailers");
        movieTask.execute(trailersUrl);
    }

    private void loadReviews() {
        URL reviewsUrl = NetworkUtils.buildReviewsURL(mMovieDetail.getMovieId());
        GetMovieTask movieTask = new GetMovieTask(this, "reviews");
        movieTask.execute(reviewsUrl);
    }

    @Override
    public void handleResponse(String response, String type) {

        if(type.equals("trailers")) {
            List<Trailer> trailerList = AppUtils.createTrailerListFromResponse(response);
            trailerListAdapter.setTrailerList(trailerList);
        } else if(type.equals("reviews")) {
            Log.d("Reviews", response);
            List<Review> reviewList = AppUtils.createReviewListFromResponse(response);
            reviewListAdapter.setReviewList(reviewList);
        }
    }
}
