package com.project.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project.popularmovies.data.FavoriteContract;
import com.project.popularmovies.data.FavoritesCursor;
import com.project.popularmovies.fragments.MovieDetailFragment;
import com.project.popularmovies.fragments.MovieListFragment;
import com.project.popularmovies.interfaces.OnItemClickListener;
import com.project.popularmovies.interfaces.ResponseHandler;
import com.project.popularmovies.models.Movie;
import com.project.popularmovies.utilities.AppUtils;
import com.project.popularmovies.utilities.GetMovieTask;
import com.project.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

/*
* The class MainActivity is the landing page of the application when the user opens the
* application. It displays a list of movies in a grid using a Recycler View. The activity
* also has option menu for user to choose and display top rated movies.
*/
public class MainActivity extends FragmentActivity implements MovieListFragment.OnMovieItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

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
    private static final int FAVORITES_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_container);

        movieList = new ArrayList<Movie>();
        MovieListFragment movieListFragment = new MovieListFragment();
        movieListFragment.setmMovieList(movieList);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, movieListFragment).commit();
        //movieListFragment.getMovieList();
        //initUI();

        /**
         * Check if the savedInstanceState is null. If savedInstanceState is not null, the
         * activity is not being started for the first time. In this case, we retain the
         * page offset and movie list from bundle using their appropriate keys. Accordingly,
         * we set our MovieListAdapter and page offset.
         */
        /*if(savedInstanceState != null) {
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
        }*/
    }

    /**
     * The onResume() method is called when this activity is visible and has focus.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Read SharedPreferences and if the resumed activity is Favorites list,
        // we update the list.
        //String categoryChoice = readFromPreferences();
        //if(categoryChoice.equals(getString(R.string.favorites)))
          //  getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, this);
    }


    /**
     * The onSaveInstanceState() is called when the activity is about to lose focus
     * and destroyed.
     *
     * @param outState Bundle containing key, value pairs to save state of the activity.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_ITEMS_KEY, movieList);
        outState.putInt(PAGE_OFFSET_KEY, pageOffset);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_order_menu, menu);
        return true;
    }

    /**
     * Prepare our options menu based on category choice chosen by user. By
     * default, the category is popular. In this case, options menu will hide
     * popular menu item and show top rated and favorites menu item. The same
     * condition applies when user chooses Top Rated or Favorites category.
     *
     * @param menu Menu to display
     *
     * @return boolean to inflate the menu items.
     */
    /*
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
*/
    /**
     * Refresh the movie list with new data. This method is invoked when user
     * chooses new category from options menu to display the movie list. In this
     * case, we clear all the movie objects from the array list and set page offset
     * to 1.
     */
    private void refreshListWithNewData() {
        movieList.clear();
        pageOffset = 1;
        //getMovieList();
    }


    /**
     * Display action bar title based on chosen category
     *
     * @param title Action Bar Title to display.
     */
    private void setActionBarTitle(String title) {
       // ActionBar actionBar = getSupportActionBar();
        //if(actionBar != null)
          //  actionBar.setTitle(title);
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

    /**
     * The method addCursorDataToList() creates movie object for every row
     * returned by cursor and adds the newly created movie object to the movie list.
     * This movie list is displayed as part of Favorites list.
     *
     * @param cursor Cursor of favorite movie items.
     *
     */
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
        //displayResults();
    }

    @Override
    public void onMovieItemSelected(int position) {
        Toast.makeText(this, "Movie item selected", Toast.LENGTH_SHORT).show();
        Movie movie = movieList.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.extra_movie_detail), movie);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
