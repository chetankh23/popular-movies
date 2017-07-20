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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.popularmovies.data.FavoriteContract;
import com.project.popularmovies.data.FavoritesCursor;
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

/*
* The class MovieDetailActivity displays the details of a particular movie when selects
* one from the movie list.
*/
public class MovieDetailActivity extends AppCompatActivity implements ResponseHandler, View.OnClickListener, OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

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
    private static final String IS_FAVORITE_KEY = "is_favorite";
    private static final String MOVIE_DETAILS_KEY = "movie_detail";

    // Backdrop Image Base URL of movie.
    private static final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w780";

    // Poster image URL of movie.
    private static final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    // Movie trailer Base URL.
    private static final String MOVIE_TRAILER_BASE_URI = "https://www.youtube.com/watch?v=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null) {
            isFavorite = savedInstanceState.getBoolean(IS_FAVORITE_KEY);
            mMovieDetail = savedInstanceState.getParcelable(MOVIE_DETAILS_KEY);
            init();
        } else {
            // Get intent from calling activity. If the intent is not null, we
            // extract the details of movie using extras.
            Intent getMovieIntent = getIntent();
            if (getMovieIntent != null) {

                // Get movie detail.
                mMovieDetail = getMovieIntent.getParcelableExtra(getString(R.string.extra_movie_detail));
                init();
            }
        }
    }

    private void init() {

        if (mMovieDetail != null) {
            loadUiWithDetails();

            // initialize loader to load favorites.
            getSupportLoaderManager().initLoader(FAVORITES_LOADER_ID, null, this);

            // call to get movie trailers.
            loadMovieTrailers();

            // call to get movie reviews.
            loadReviews();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FAVORITE_KEY, isFavorite);
        outState.putParcelable(MOVIE_DETAILS_KEY, mMovieDetail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, this);
    }

    /**
     * The method loadUiWithDetails() displays the details of the movie such as Title,
     * Release Date, Rating and Plot Synoposis of the movie.
     */
    private void loadUiWithDetails() {
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
        createTrailerListUI();
        createReviewListUI();

        collapsingToolbar.setTitle(mMovieDetail.getTitle());
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.coll_toolbar_title);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.exp_toolbar_title);
        Picasso.with(this).load(BACKDROP_BASE_URL + mMovieDetail.getBackdropPath()).into(mMovieBackdropImageView);
        Picasso.with(this).load(MOVIE_IMAGE_BASE_URL + mMovieDetail.getPosterPath()).into(mMovieImageThumbnail);
        mMovieTitle.setText(mMovieDetail.getTitle());
        mMovieReleaseDate.setText(AppUtils.convertDateToCustomFormat(mMovieDetail.getReleaseDate()));
        mMovieRating.setText(mMovieDetail.getVoteAverage() + getString(R.string.out_of_rating));
        mMovieSynopsis.setText(mMovieDetail.getOverView());

    }

    /**
     * Initializes the requisite UI to display list of trailers. This includes
     * initializing the RecyclerView, TrailerListAdapter and trailerList.
     */
    private void createTrailerListUI() {

        trailerList = new ArrayList<Trailer>();

        // The TrailerListAdapter is responsible for linking the trailer data with the views
        // that will end up displaying the trailer data.
        trailerListAdapter = new TrailerListAdapter(this, this);
        trailerListRecyclerView = (RecyclerView) findViewById(R.id.rv_trailer_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        trailerListRecyclerView.setLayoutManager(layoutManager);
        trailerListRecyclerView.setHasFixedSize(true);

        // Setting the adapter attaches it to the RecyclerView in our layout.
        trailerListRecyclerView.setAdapter(trailerListAdapter);
    }

    /**
     * Initializes the requisite UI to display list of reviews. This includes
     * initializing the RecyclerView, ReviewListAdapter and reviewList.
     */
    private void createReviewListUI() {

        reviewList = new ArrayList<Review>();

        // The ReviewListAdapter is responsible for linking the review data with the views
        // that will end up displaying the review data.
        reviewListAdapter = new ReviewListAdapter(this);
        reviewListRecyclerView = (RecyclerView) findViewById(R.id.rv_review_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewListRecyclerView.setLayoutManager(layoutManager);
        reviewListRecyclerView.setHasFixedSize(true);

        // Setting the adapter attaches it to the RecyclerView in our layout.
        reviewListRecyclerView.setAdapter(reviewListAdapter);
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

    @Override
    public void handleResponse(String response, String type) {

        if(type.equals(getString(R.string.trailers))) {
            trailerList = AppUtils.createTrailerListFromResponse(response);
            trailerListAdapter.setTrailerList(trailerList);
        } else if(type.equals(getString(R.string.reviews))) {
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
    public void onItemClicked(int itemClickIndex) {
        Trailer trailer = trailerList.get(itemClickIndex);
        if(trailer != null) {

            // Create an implicit intent to view the movie trailer.
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

    /**
     * Add movie to Favorites using Content Provider.
     */
    private void addMovieToFavorites() {

        // Create a content values object to store details of the movie.
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, mMovieDetail.getMovieId());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, mMovieDetail.getTitle());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW, mMovieDetail.getOverView());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_BACKDROP_URL, mMovieDetail.getBackdropPath());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL, mMovieDetail.getPosterPath());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, mMovieDetail.getReleaseDate());
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE, mMovieDetail.getVoteAverage());

        // getContentResolver to insert the movie details in the underlying database.
        Uri uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);

        if (uri != null) {

            // Addition to Favorites was successful.
            String toastMessage = getString(R.string.add_favorites_success);
            isFavorite = true;
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            mFavoritesButton.setText(getString(R.string.remove_from_favorties));
        } else {

            // Addition to Favorites failed.
            String toastMessage = getString(R.string.add_favorites_failed);
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Remove the movie from favorites using movie Id.
     */
    private void removeMovieFromFavorites() {

        // Build Uri using movie Id and delete the movie item from the underlying database using content resolver.
        Uri uri = FavoriteContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mMovieDetail.getMovieId())).build();
        int rowsDeleted = getContentResolver().delete(uri, null, null);
        if(rowsDeleted > 0) {

            // Deletion was successful.
            isFavorite = false;
            String toastMessage = getString(R.string.remove_favorites_success);
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            mFavoritesButton.setText(getString(R.string.add_to_favorites));
        } else {

            // Deletion failed
            String toastMessage = getString(R.string.remove_favorites_failed);
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Creates a share intent to share movie's first trailer URL.
     */
    private void createShareIntent() {

        if(trailerList != null && trailerList.size() > 0) {
            String mimeType = getString(R.string.mimeType_to_share);
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
