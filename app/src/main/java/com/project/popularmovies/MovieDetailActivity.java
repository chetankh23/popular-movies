package com.project.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.popularmovies.data.FavoriteContract;
import com.project.popularmovies.data.FavoritesCursor;
import com.project.popularmovies.interfaces.OnMovieItemClickListener;
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
public class MovieDetailActivity extends AppCompatActivity implements ResponseHandler, View.OnClickListener, OnMovieItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private Movie mMovieDetail;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private TrailerListAdapter trailerListAdapter;
    private ReviewListAdapter reviewListAdapter;
    private List<Trailer> trailerList;
    private List<Review> reviewList;
    private Button mFavoritesButton;
    private static int FAVORITES_LOADER_ID = 1;
    private boolean isFavorite;
    private TextView mNoReviewsTextView;
    private RecyclerView reviewListRecyclerView, trailerListRecyclerView;
    private FloatingActionButton shareFab;

    private static final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w780";
    private static final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private static final String MOVIE_TRAILER_BASE_URI = "https://www.youtube.com/watch?v=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent getMovieIntent = getIntent();
        if(getMovieIntent != null) {
            mMovieDetail = getMovieIntent.getParcelableExtra(getString(R.string.extra_movie_detail));
            if(mMovieDetail != null) {
                displayMovieDetails();
                getSupportLoaderManager().initLoader(FAVORITES_LOADER_ID, null, this);
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
        shareFab = (FloatingActionButton) findViewById(R.id.fab_share);
        mNoReviewsTextView = (TextView) findViewById(R.id.tv_no_reviews);
        mFavoritesButton = (Button) findViewById(R.id.b_favorites);
        mFavoritesButton.setOnClickListener(this);
        shareFab.setOnClickListener(this);

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
        trailerListAdapter = new TrailerListAdapter(this, this);
        trailerListRecyclerView = (RecyclerView) findViewById(R.id.rv_trailer_list);
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
        reviewListRecyclerView = (RecyclerView) findViewById(R.id.rv_review_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewListRecyclerView.setLayoutManager(layoutManager);
        reviewListRecyclerView.setHasFixedSize(true);

        // Setting the adapter attaches it to the RecyclerView in our layout.
        reviewListRecyclerView.setAdapter(reviewListAdapter);

    }

    private void loadMovieTrailers() {
        URL trailersUrl = NetworkUtils.buildTrailersURL(mMovieDetail.getMovieId());
        GetMovieTask movieTask = new GetMovieTask(null, this, "trailers");
        movieTask.execute(trailersUrl);
    }

    private void loadReviews() {
        URL reviewsUrl = NetworkUtils.buildReviewsURL(mMovieDetail.getMovieId());
        GetMovieTask movieTask = new GetMovieTask(null, this, "reviews");
        movieTask.execute(reviewsUrl);
    }

    @Override
    public void handleResponse(String response, String type) {

        if(type.equals("trailers")) {
            trailerList = AppUtils.createTrailerListFromResponse(response);
            trailerListAdapter.setTrailerList(trailerList);
        } else if(type.equals("reviews")) {
            Log.d("Reviews", response);
            List<Review> reviewList = AppUtils.createReviewListFromResponse(response);
            reviewListAdapter.setReviewList(reviewList);
            if(reviewList.size() > 0) {
                mNoReviewsTextView.setVisibility(View.GONE);
                reviewListRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mNoReviewsTextView.setVisibility(View.VISIBLE);
                reviewListRecyclerView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onMovieItemClicked(int movieItemClickIndex) {
        Trailer trailer = trailerList.get(movieItemClickIndex);
        if(trailer != null) {
            Uri videoUri = Uri.parse(MOVIE_TRAILER_BASE_URI + trailer.getTrailerKey());
            Intent playVideoIntent = new Intent(Intent.ACTION_VIEW, videoUri);
            startActivity(playVideoIntent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new FavoritesCursor(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(isMovieFavorite(data)) {
            isFavorite = true;
            mFavoritesButton.setText(getString(R.string.remove_from_favorties));
        }
        else {
            isFavorite = false;
            mFavoritesButton.setText(getString(R.string.add_to_favorites));
        }
    }

    private boolean isMovieFavorite(Cursor cursor) {
        if(cursor == null)
            return false;

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID));
            if(mMovieDetail.getMovieId() == id) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.b_favorites) {
            if(!isFavorite) {
                addMovieToFavorites();
            } else {
                removeMovieFromFavorites();
            }
        } else if(v.getId() == R.id.fab_share) {
            FloatingActionButton shareButton = (FloatingActionButton) v;
            createShareIntent();
        }
    }

    private void addMovieToFavorites() {
        Log.d("Movie overview", mMovieDetail.getOverView());
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, mMovieDetail.getMovieId());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, mMovieDetail.getTitle());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW, mMovieDetail.getOverView());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_BACKDROP_URL, mMovieDetail.getBackdropPath());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL, mMovieDetail.getPosterPath());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, mMovieDetail.getReleaseDate());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE, mMovieDetail.getVoteAverage());
        Uri uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);
        if (uri != null) {
            String toastMessage = getString(R.string.add_favorites_success);
            isFavorite = true;
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            mFavoritesButton.setText(getString(R.string.remove_from_favorties));
        } else {
            String toastMessage = getString(R.string.add_favorites_failed);
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void removeMovieFromFavorites() {
        Uri uri = FavoriteContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mMovieDetail.getMovieId())).build();
        int rowsDeleted = getContentResolver().delete(uri, null, null);
        if(rowsDeleted > 0) {
            isFavorite = false;
            String toastMessage = getString(R.string.remove_favorites_success);
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            mFavoritesButton.setText(getString(R.string.add_to_favorites));
        } else {
            String toastMessage = getString(R.string.remove_favorites_failed);
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void createShareIntent() {
        if(trailerList != null && trailerList.size() > 0) {
            String mimeType = "text/plain";
            String title = getString(R.string.share_title);
            String textToShare = Uri.parse(MOVIE_TRAILER_BASE_URI + trailerList.get(0).getTrailerKey()).toString();
            ShareCompat.IntentBuilder.from(this)
                    .setType(mimeType)
                    .setChooserTitle(title)
                    .setText(textToShare)
                    .startChooser();
        }
    }
}
