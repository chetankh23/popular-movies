package com.project.popularmovies;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

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
public class MainActivity extends AppCompatActivity implements ResponseHandler, OnMovieItemClickListener {

    private List<Movie> movieList;
    private MovieListAdapter movieListAdapter;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        getMovieList(getString(R.string.api_popular));
    }

    /**
     * The method initUI() initializes the Recycler View and attaches a List Adapter to it.
     */
    private void initUI() {

        // Initialize an arrayList of movies.
        movieList = new ArrayList<Movie>();

        // The MovieListAdapter is responsible for linking the movie data with the views
        // that will end up displaying the movie data.
        movieListAdapter = new MovieListAdapter(this, this);

        // Progress Bar to indicate the user that data is being loaded.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        RecyclerView movieListRecyclerView = (RecyclerView) findViewById(R.id.rv_movielist);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        movieListRecyclerView.setLayoutManager(gridLayoutManager);
        movieListRecyclerView.setHasFixedSize(true);

        // Setting the adapter attaches it to the RecyclerView in our layout.
        movieListRecyclerView.setAdapter(movieListAdapter);
    }

    /**
     * The method will get user's choice to display list of movies and then tell some
     * background method to fetch the data in the background.
     *
     * @param choice User's choice to display the list of movies.
     */
    private void getMovieList(String choice) {
        URL url = NetworkUtils.buildURL(choice);
        GetMovieTask movieTask = new GetMovieTask(mLoadingIndicator, this);
        movieTask.execute(url);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String itemName = (String) item.getTitle();
        ActionBar actionBar;
        if(itemName.equals(getString(R.string.top_rated))) {
            item.setTitle(R.string.popular);
            getMovieList(getString(R.string.api_top_rated));
            actionBar = getSupportActionBar();
            if(actionBar != null)
                actionBar.setTitle(getString(R.string.top_rated));
            return true;
        } else if(itemName.equals(getString(R.string.popular))) {
            item.setTitle(R.string.top_rated);
            getMovieList(getString(R.string.api_popular));
            actionBar = getSupportActionBar();
            if(actionBar != null)
                actionBar.setTitle(getString(R.string.app_name));
            return true;
        }
        return onOptionsItemSelected(item);
    }

    /**
     * The method handleResponse() handles the web service response by parsing
     * it and accordingly update the data used for the adapter.
     *
     * @param response Response obtained from the web service call.
     */
    @Override
    public void handleResponse(String response) {
        movieList = AppUtils.createMovieDataFromResponse(response);
        movieListAdapter.setmMovieList(movieList);
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
}
