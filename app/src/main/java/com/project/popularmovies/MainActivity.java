package com.project.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.project.popularmovies.data.FavoriteContract;
import com.project.popularmovies.data.FavoritesCursor;
import com.project.popularmovies.interfaces.OnMovieItemClickListener;
import com.project.popularmovies.interfaces.ResponseHandler;
import com.project.popularmovies.models.Movie;
import com.project.popularmovies.utilities.AppUtils;
import com.project.popularmovies.utilities.GetMovieTask;
import com.project.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
* The class MainActivity is the landing page of the application when the user opens the
* application. It displays a list of movies in a grid using a Recycler View. The activity
* also has option menu for user to choose and display top rated movies.
*/
public class MainActivity extends AppCompatActivity implements ResponseHandler, OnMovieItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayList<Movie> movieList;
    private MovieListAdapter movieListAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mNoMoviesFoundTextView;
    private RecyclerView movieListRecyclerView;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int pageOffset = 1;
    private static final String PAGE_OFFSET_KEY = "page_offset_key";
    private static final String CATEGORY_CHOICE_KEY = "category_choice_key";
    private static final String LIST_ITEMS_KEY = "movie_list_key";
    private static final int FAVORITES_LOADER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        if(savedInstanceState != null) {
            String categoryChoice = readFromPreferences();
            setActionBarTitle(categoryChoice);
            pageOffset = savedInstanceState.getInt(PAGE_OFFSET_KEY);
            movieList = savedInstanceState.getParcelableArrayList(LIST_ITEMS_KEY);
            movieListAdapter.setmMovieList(movieList);
        } else {
            saveToSharedPreferences(getString(R.string.popular));
            setActionBarTitle(getString(R.string.popular));
            movieList = new ArrayList<Movie>();
            getMovieList();
        }
    }

    /**
     * The method initUI() initializes the Recycler View and attaches a List Adapter to it.
     */
    private void initUI() {

        // The MovieListAdapter is responsible for linking the movie data with the views
        // that will end up displaying the movie data.
        movieListAdapter = new MovieListAdapter(this, this);

        // Progress Bar to indicate the user that data is being loaded.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mNoMoviesFoundTextView = (TextView) findViewById(R.id.tv_no_movies);
        movieListRecyclerView = (RecyclerView) findViewById(R.id.rv_movielist);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        movieListRecyclerView.setLayoutManager(gridLayoutManager);
        movieListRecyclerView.setHasFixedSize(true);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                String categoryChoice = readFromPreferences();
                if(!categoryChoice.equals(getString(R.string.favorites)))
                    getMovieList();
            }
        };

        // Setting the adapter attaches it to the RecyclerView in our layout.
        movieListRecyclerView.setAdapter(movieListAdapter);
        movieListRecyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String categoryChoice = readFromPreferences();
        if(categoryChoice.equals(getString(R.string.favorites)))
            getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, this);
    }

    private void saveToSharedPreferences(String categoryChoice) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CATEGORY_CHOICE_KEY, categoryChoice);
        editor.commit();
    }

    private String readFromPreferences() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String categoryChoice = sharedPreferences.getString(CATEGORY_CHOICE_KEY, getString(R.string.popular));
        return categoryChoice;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_ITEMS_KEY, movieList);
        outState.putInt(PAGE_OFFSET_KEY, pageOffset);

    }

    /**
     * The method will get user's choice to display list of movies and then tell some
     * background method to fetch the data in the background.
     *
     */
    private void getMovieList() {
        String apiToCall = "";
        String categoryChoice = readFromPreferences();
        if(categoryChoice.equals(getString(R.string.popular)))
            apiToCall = getString(R.string.api_popular);
        else if(categoryChoice.equals(getString(R.string.top_rated)))
            apiToCall = getString(R.string.api_top_rated);
        URL url = NetworkUtils.buildURL(apiToCall, pageOffset++);
        Log.d("URL", url.toString());
        GetMovieTask movieTask = new GetMovieTask(mLoadingIndicator, this, null);
        movieTask.execute(url);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_order_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String categoryChoice = readFromPreferences();
        MenuItem popular = menu.findItem(R.id.action_popular);
        MenuItem topRated = menu.findItem(R.id.action_top_rated);
        MenuItem favorites = menu.findItem(R.id.action_favorties);
        if(categoryChoice.equals(getString(R.string.popular))) {
            popular.setVisible(false);
            topRated.setVisible(true);
            favorites.setVisible(true);
            return true;
        }
        else if(categoryChoice.equals(getString(R.string.top_rated))) {
            popular.setVisible(true);
            topRated.setVisible(false);
            favorites.setVisible(true);
            return true;
        } else if(categoryChoice.equals(getString(R.string.favorites))) {
            popular.setVisible(true);
            topRated.setVisible(true);
            favorites.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuItemId = item.getItemId();
        if(menuItemId == R.id.action_popular) {
            setActionBarTitle(getString(R.string.popular));
            saveToSharedPreferences(getString(R.string.popular));
            refreshListWithNewData();
            return true;

        } else if(menuItemId == R.id.action_top_rated) {
            setActionBarTitle(getString(R.string.top_rated));
            saveToSharedPreferences(getString(R.string.top_rated));
            refreshListWithNewData();
            return true;
        } else if(menuItemId == R.id.action_favorties) {
            setActionBarTitle(getString(R.string.favorites));
            saveToSharedPreferences(getString(R.string.favorites));
            getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshListWithNewData() {
        movieList.clear();
        pageOffset = 1;
        getMovieList();
    }


    private void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle(title);
    }

    /**
     * The method handleResponse() handles the web service response by parsing
     * it and accordingly update the data used for the adapter.
     *
     * @param response Response obtained from the web service call.
     */
    @Override
    public void handleResponse(String response, String type) {
        AppUtils.createMovieDataFromResponse(movieList, response);
        displayResults();
    }

    /**
     * The method onMovieItemClicked() is invoked when user clicks any particular movie list
     * item. It navigates the user to the MovieDetailActivity to show the details of
     * the selected movie. The movie object is wrapped in the intent and sent to the
     * MovieDetailActivity class.
     *
     * @param movieItemClickIndex The index of the item which was clicked.
     */
    @Override
    public void onMovieItemClicked(int movieItemClickIndex) {
        Movie selectedMovie = movieList.get(movieItemClickIndex);
        Intent movieDetailIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
        movieDetailIntent.putExtra(getString(R.string.extra_movie_detail), selectedMovie);
        startActivity(movieDetailIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new FavoritesCursor(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        addCursorDataToList(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //addCursorDataToList(null);
    }

    private void addCursorDataToList(Cursor cursor) {
        movieList.clear();

        if(cursor == null)
            return;

        while(cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.setMovieId(cursor.getInt(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE)));
            movie.setOverView(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW)));
            movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_BACKDROP_URL)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE)));
            movieList.add(movie);
        }
        displayResults();
    }

    private void displayResults() {
        movieListAdapter.setmMovieList(movieList);
        if(movieList.size() > 0) {
            movieListRecyclerView.setVisibility(View.VISIBLE);
            mNoMoviesFoundTextView.setVisibility(View.GONE);
        } else {
            movieListRecyclerView.setVisibility(View.GONE);
            mNoMoviesFoundTextView.setVisibility(View.VISIBLE);
        }
    }
}
