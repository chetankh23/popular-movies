package com.project.popularmovies.utilities;

import com.project.popularmovies.models.Movie;
import com.project.popularmovies.models.Review;
import com.project.popularmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppUtils {

    // Constants used to parse JSON response for movies.
    private static final String TOTAL_RESULTS = "total_results";
    private static final String TOTAL_PAGES = "total_pages";
    private static final String RESULTS = "results";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String ID = "id";
    private static final String OVERVIEW = "overview";
    private static final String POSTER_PATH = "poster_path";
    private static final String TITLE = "title";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";

    // Constants used to parse JSON response for trailers.
    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String SITE = "site";
    private static final String SIZE = "size";
    private static final String TYPE = "type";

    // Constants used to parse JSON response for reviews.
    private static final String CONTENT = "content";
    private static final String AUTHOR = "author";


    /**
     * Parses the JSON response and creates a new Movie Object for each element
     * in the JSON Array 'results'. The movie object is then added the list.
     *
     * @param movieList List to which movie objects are to be appended.
     * @param response JSON Response obtained from web service API.
     */
    public static void createMovieDataFromResponse(List<Movie> movieList, String response) {

        try {
            JSONObject responseObject = new JSONObject(response);
            int totalResults = responseObject.getInt(TOTAL_RESULTS);
            int totalPages = responseObject.getInt(TOTAL_PAGES);
            JSONArray results = responseObject.getJSONArray(RESULTS);
            for(int i = 0 ; i < results.length() ; i++) {
                JSONObject movieDetails = results.getJSONObject(i);

                // create a movie object and add details to it.
                Movie movie = new Movie();
                movie.setMovieId(movieDetails.getInt(ID));
                movie.setBackdropPath(movieDetails.getString(BACKDROP_PATH));
                movie.setOverView(movieDetails.getString(OVERVIEW));
                movie.setPosterPath(movieDetails.getString(POSTER_PATH));
                movie.setTitle(movieDetails.getString(TITLE));
                movie.setReleaseDate(movieDetails.getString(RELEASE_DATE));
                movie.setVoteAverage(movieDetails.getDouble(VOTE_AVERAGE));

                // add the movie object to the list.
                movieList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the JSON response and creates a new Trailer Object for each element
     * in the JSON Array 'results'. The trailer object is then added to the list of trailers.
     *
     * @param response JSON Response obtained from web service API.
     * @return List of Trailers.
     */
    public static List<Trailer> createTrailerListFromResponse(String response) {

        List<Trailer> trailerList = new ArrayList<Trailer>();
        try {
            JSONObject responseObject = new JSONObject(response);
            JSONArray results = responseObject.getJSONArray(RESULTS);
            for(int i = 0 ; i < results.length() ; i++) {
                JSONObject trailerDetails = results.getJSONObject(i);

                // create a trailer object and store details in that object.
                Trailer trailer = new Trailer();
                trailer.setTrailerId(trailerDetails.getString(ID));
                trailer.setTrailerKey(trailerDetails.getString(KEY));
                trailer.setTrailerName(trailerDetails.getString(NAME));
                trailer.setTrailerSite(trailerDetails.getString(SITE));
                trailer.setTrailerSize(trailerDetails.getInt(SIZE));
                trailer.setTrailerType(trailerDetails.getString(TYPE));

                // add trailer object to list.
                trailerList.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailerList;
    }

    /**
     * Parses the JSON response and creates a new Review Object for each element
     * in the JSON Array 'results'. The review object is then added to the review list.
     *
     * @param response JSON Response obtained from web service API.
     * @return List of Reviews.
     */
    public static List<Review> createReviewListFromResponse(String response) {

        List<Review> reviewList = new ArrayList<Review>();
        try {
            JSONObject responseObject = new JSONObject(response);
            JSONArray results = responseObject.getJSONArray(RESULTS);
            for(int i = 0 ; i < results.length() ; i++) {
                JSONObject reviewDetails = results.getJSONObject(i);

                // create a review object and add details to that object.
                Review review = new Review();
                review.setContent(reviewDetails.getString(CONTENT));
                review.setAuthor(reviewDetails.getString(AUTHOR));

                // add the review object to the list.
                reviewList.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewList;
    }

    // Converting given date to user friendly date format.
    public static String convertDateToCustomFormat(String dateInput) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date date = null;
        try {
            date = dateFormat.parse(dateInput);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat convertFormat = new SimpleDateFormat("MMM dd, yyyy");
        return convertFormat.format(date);
    }
}
