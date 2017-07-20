package com.project.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoritesContentProvider extends ContentProvider {

    // Defining integer constant for entire favorites table.
    public static final int FAVORITES = 100;

    // Defining integer constant for single item in favorites table with Id.
    public static final int FAVORITES_WITH_ID = 101;

    private FavoritesDbHelper mFavoritesDbHelper;

    // Static variable for UriMatcher.
    public static UriMatcher sUriMatcher = buildMatcher();

    /**
     * The method buildMatcher() associates Uri's with their int match.
     *
     * @return static instance of UriMatcher.
     */
    private static UriMatcher buildMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);
        return uriMatcher;
    }

    /**
     * In this method, we initialize FavoritesDbHelper.
     *
     * @return true, as we have handled the onCreate() method.
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoritesDbHelper = new FavoritesDbHelper(context);
        return true;
    }

    // Implement query to handle requests for data by Uri.
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get access to Favorites Database to read from it.
        final SQLiteDatabase db = mFavoritesDbHelper.getReadableDatabase();

        int matchId = sUriMatcher.match(uri);
        Cursor cursor = null;
        switch (matchId) {

            case FAVORITES :
                            // query to return directory of favorites table.
                            cursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                            break;

            case FAVORITES_WITH_ID :
                            // query to return single item of favorites table.
                            String id = uri.getPathSegments().get(1);
                            String mSelection = "movie_id=?";
                            String[] mSelectionArgs = new String[] {id};
                            cursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                            break;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        // set notification Uri on the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    // Insert to handle requests to insert single new row of data.
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        // Get access to favorites database to write to it.
        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        int matchId = sUriMatcher.match(uri);
        Uri insertedUri = null;
        switch (matchId) {

            case FAVORITES:
                            // insert new values into the favorites table.
                            long id = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
                            if(id > 0) {
                                // insert is successful.
                                insertedUri = ContentUris.withAppendedId(uri, id);
                            } else {
                                // insertion failed.
                                throw new SQLiteException("Failed to insert row into " + uri);
                            }
                            break;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        // Notifying the resolver if the uri has been changed.
        if(insertedUri != null)
            getContext().getContentResolver().notifyChange(uri, null);

        return insertedUri;
    }

    // Implement delete to delete a single row of data using movie id.
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Get access to favorites database to write to it.
        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        int matchId = sUriMatcher.match(uri);
        int favoritesDeleted;
        switch (matchId) {
            case FAVORITES_WITH_ID:
                                    // delete favorites from favorites database using movie id in the uri.
                                    String id = uri.getPathSegments().get(1);
                                    String whereClause = "movie_id=?";
                                    String[] whereArgs = new String[]{id};
                                    favoritesDeleted = db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, whereClause, whereArgs);
                                    break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);

        }

        // Notify resolver if any change has occurred.
        if(favoritesDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return favoritesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
