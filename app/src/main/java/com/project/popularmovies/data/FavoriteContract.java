package com.project.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {

    // The authority, which is how the code knows which Content Provider to access.
    public static final String AUTHORITY = "com.project.popularmovies";

    // Base Content URI = "content://" + <authority>
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    // defining possible paths to access data.
    public static final String PATH_FAVORITES = "favorites";

    /**
     * Favorite Entry is an inner class that defines contents of the favorites table.
     */
    public static final class FavoriteEntry implements BaseColumns {

        // Content URI = BASE_URI + PATH;
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        // Favorites table name
        public static final String TABLE_NAME = "favorites";

        // Favorites column names
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";
    }
}
