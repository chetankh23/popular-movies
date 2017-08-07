package com.project.popularmovies.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.project.popularmovies.EndlessRecyclerViewScrollListener;
import com.project.popularmovies.MovieListAdapter;
import com.project.popularmovies.R;
import com.project.popularmovies.databinding.ActivityMainBinding;
import com.project.popularmovies.interfaces.OnItemClickListener;
import com.project.popularmovies.interfaces.ResponseHandler;
import com.project.popularmovies.models.Movie;
import com.project.popularmovies.utilities.AppUtils;
import com.project.popularmovies.utilities.GetMovieTask;
import com.project.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MovieListFragment extends Fragment implements OnItemClickListener, ResponseHandler {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private static final String PAGE_OFFSET_KEY = "page_offset_key";
    private static final String CATEGORY_CHOICE_KEY = "category_choice_key";
    private static final String LIST_ITEMS_KEY = "movie_list_key";
    OnMovieItemSelectedListener mCallback;
    ActivityMainBinding mBinding;
    List<Movie> mMovieList;
    MovieListAdapter mMovieListAdapter;
    int pageOffset = 1;

    public interface OnMovieItemSelectedListener {
        public void onMovieItemSelected(int position);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_main, container, false);
        View rootView = mBinding.getRoot();
        initUI();
        if(savedInstanceState != null) {
            String categoryChoice = readFromPreferences();
            getActivity().setTitle(categoryChoice);
            pageOffset = savedInstanceState.getInt(PAGE_OFFSET_KEY);
            mMovieList = savedInstanceState.getParcelableArrayList(LIST_ITEMS_KEY);
            mMovieListAdapter.setmMovieList(mMovieList);
        } else {
            saveToSharedPreferences(getString(R.string.popular));
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnMovieItemSelectedListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.getPackageName() + " must implement OnMovieItemSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMovieList();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_order_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        if(menuItemId == R.id.action_popular) {
            getActivity().setTitle(getString(R.string.popular));
            saveToSharedPreferences(getString(R.string.popular));
            refreshListWithNewData();
            return true;
        } else if(menuItemId == R.id.action_top_rated) {
            getActivity().setTitle(getString(R.string.top_rated));
            saveToSharedPreferences(getString(R.string.top_rated));
            refreshListWithNewData();
            return true;
        } else if(menuItemId == R.id.action_favorties) {
            getActivity().setTitle(getString(R.string.favorites));
            saveToSharedPreferences(getString(R.string.favorites));
            //getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, this);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        String categoryChoice = readFromPreferences();
        MenuItem popular = menu.findItem(R.id.action_popular);
        MenuItem topRated = menu.findItem(R.id.action_top_rated);
        MenuItem favorites = menu.findItem(R.id.action_favorties);
        if(categoryChoice.equals(getString(R.string.popular))) {
            popular.setVisible(false);
            topRated.setVisible(true);
            favorites.setVisible(true);
        } else if(categoryChoice.equals(getString(R.string.top_rated))) {
            popular.setVisible(true);
            topRated.setVisible(false);
            favorites.setVisible(true);
        } else if(categoryChoice.equals(getString(R.string.favorites))) {
            popular.setVisible(true);
            topRated.setVisible(true);
            favorites.setVisible(false);
        }
    }


    /**
     * Refresh the movie list with new data. This method is invoked when user
     * chooses new category from options menu to display the movie list. In this
     * case, we clear all the movie objects from the array list and set page offset
     * to 1.
     */
    private void refreshListWithNewData() {
        mMovieList.clear();
        pageOffset = 1;
        //getMovieList();
    }


    private void initUI() {
        mMovieListAdapter = new MovieListAdapter(getActivity().getApplicationContext(), MovieListFragment.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mBinding.rvMovielist.setLayoutManager(gridLayoutManager);
        mBinding.rvMovielist.setHasFixedSize(true);

        /**
         * When user scrolls to the end of the list, we need to load more data. So,
         * a web service APi is called when the user scrolls down to end of the list.
         */
        /*EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                String categoryChoice = readFromPreferences();
                if(!categoryChoice.equals(getString(R.string.favorites)))
                    getMovieList();
            }
        };*/

        // Setting the adapter attaches it to the RecyclerView in our layout.
        mBinding.rvMovielist.setAdapter(mMovieListAdapter);
        //mBinding.rvMovielist.addOnScrollListener(scrollListener);
    }

    /**
     * The method will get user's choice to display list of movies and then tell some
     * background method to fetch the data in the background.
     *
     */
    public void getMovieList() {
        //if(isAdded()) {

            //String categoryChoice = readFromPreferences();
            //if(categoryChoice.equals(getString(R.string.popular)))

            String apiToCall = getContext().getString(R.string.api_popular);

            //else if(categoryChoice.equals(getString(R.string.top_rated)))
              //  apiToCall = getString(R.string.api_top_rated);
            URL url = NetworkUtils.buildURL(apiToCall, pageOffset++);
            Log.d(LOG_TAG, url.toString());
            GetMovieTask movieTask = new GetMovieTask(null, this, null);
            movieTask.execute(url);
        //}
    }

    /**
     * We save the category selected by user to our SharedPreferences file. The category
     * can be one amongst Popular, Top Rated or Favorites.
     *
     * @param categoryChoice Category choice selected by user.
     */
    private void saveToSharedPreferences(String categoryChoice) {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CATEGORY_CHOICE_KEY, categoryChoice);
        editor.commit();
    }

    /**
     * Read from SharedPreferences file and return the chosen category to display
     * movie list.
     *
     * @return String, category chosen by user.
     */
    private String readFromPreferences() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String categoryChoice = sharedPreferences.getString(CATEGORY_CHOICE_KEY, getString(R.string.popular));
        return categoryChoice;
    }

    /**
     * The method handleResponse() handles the web service response by parsing
     * it and accordingly update the data used for the adapter.
     *
     * @param response Response obtained from the web service call.
     */
    @Override
    public void handleResponse(String response, String type) {
        Log.d(LOG_TAG, response);
        AppUtils.createMovieDataFromResponse(mMovieList, response);
        Log.d(LOG_TAG, mMovieList.size()+"");
        displayResults();
    }

    /**
     * Displays either the list of movies by making the RecyclerView visible
     * or displays a simple text view with an info message if the movie list is empty.
     */
    private void displayResults() {
        mMovieListAdapter.setmMovieList(mMovieList);
        if(mMovieList.size() > 0) {
            mBinding.rvMovielist.setVisibility(View.VISIBLE);
            mBinding.tvNoMovies.setVisibility(View.GONE);
        } else {
            mBinding.rvMovielist.setVisibility(View.GONE);
            mBinding.tvNoMovies.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClicked(int itemClickIndex) {
        mCallback.onMovieItemSelected(itemClickIndex);
    }

    public List<Movie> getmMovieList() {
        return mMovieList;
    }

    public void setmMovieList(List<Movie> mMovieList) {
        this.mMovieList = mMovieList;
    }
}
