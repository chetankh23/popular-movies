package com.project.popularmovies;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.popularmovies.models.Movie;
import com.project.popularmovies.utilities.AppUtils;
import com.squareup.picasso.Picasso;

/*
* The class MovieDetailActivity displays the details of a particular movie when selects
* one from the movie list.
*/
public class MovieDetailActivity extends AppCompatActivity {

    private Movie mMovieDetail;

    private static final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w780";
    private static final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent getMovieIntent = getIntent();
        if(getMovieIntent != null) {
            mMovieDetail = getMovieIntent.getParcelableExtra(getString(R.string.extra_movie_detail));
            if(mMovieDetail != null)
                displayMovieDetails();
        }
    }

    /*
    * The method displayMovieDetails() displays the details of the movie such as Title,
    * Release Date, Rating and Plot Synoposis of the movie.
    */
    private void displayMovieDetails() {
        ImageView mMovieBackdropImageView = (ImageView) findViewById(R.id.iv_movie_backdrop_image);
        ImageView mMovieImageThumbnail = (ImageView) findViewById(R.id.iv_movie_thumbnail);
        TextView mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        TextView mMovieReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        TextView mMovieRating = (TextView) findViewById(R.id.tv_movie_rating);
        TextView mMovieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);

        Picasso.with(this).load(BACKDROP_BASE_URL + mMovieDetail.getBackdropPath()).into(mMovieBackdropImageView);
        Picasso.with(this).load(MOVIE_IMAGE_BASE_URL + mMovieDetail.getPosterPath()).into(mMovieImageThumbnail);
        mMovieTitle.setText(mMovieDetail.getTitle());
        mMovieReleaseDate.setText(AppUtils.convertDateToCustomFormat(mMovieDetail.getReleaseDate()));
        mMovieRating.setText(mMovieDetail.getVoteAverage() + getString(R.string.out_of_rating));
        mMovieSynopsis.setText(mMovieDetail.getOverView());
    }
}
