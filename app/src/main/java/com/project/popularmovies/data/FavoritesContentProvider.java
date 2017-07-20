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

    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;

    private FavoritesDbHelper mFavoritesDbHelper;
    public static UriMatcher sUriMatcher = buildMatcher();

    private static UriMatcher buildMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoritesDbHelper = new FavoritesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mFavoritesDbHelper.getReadableDatabase();

        int matchId = sUriMatcher.match(uri);
        Cursor cursor = null;
        switch (matchId) {

            case FAVORITES :
                            cursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                            break;

            case FAVORITES_WITH_ID :
                            String id = uri.getPathSegments().get(1);
                            String mSelection = "movie_id=?";
                            String[] mSelectionArgs = new String[] {id};
                            cursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                            break;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        int matchId = sUriMatcher.match(uri);
        Uri insertedUri = null;
        switch (matchId) {

            case FAVORITES:
                            long id = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
                            if(id > 0) {
                                insertedUri = ContentUris.withAppendedId(uri, id);
                            } else {
                                throw new SQLiteException("Failed to insert row into " + uri);
                            }
                            break;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if(insertedUri != null)
            getContext().getContentResolver().notifyChange(uri, null);

        return insertedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        int matchId = sUriMatcher.match(uri);
        int favoritesDeleted;
        switch (matchId) {
            case FAVORITES_WITH_ID:
                                    String id = uri.getPathSegments().get(1);
                                    String whereClause = "movie_id=?";
                                    String[] whereArgs = new String[]{id};
                                    favoritesDeleted = db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, whereClause, whereArgs);
                                    break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return favoritesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
