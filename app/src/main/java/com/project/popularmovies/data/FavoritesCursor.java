package com.project.popularmovies.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

public class FavoritesCursor extends AsyncTaskLoader<Cursor> {

    Cursor mFavoritesData = null;

    public FavoritesCursor(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {

        if(mFavoritesData != null) {
            deliverResult(mFavoritesData);
        } else {
            forceLoad();
        }
    }

    @Override
    public Cursor loadInBackground() {

        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI, null, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    public void deliverResult(Cursor cursor) {
        mFavoritesData = cursor;
        super.deliverResult(cursor);
    }
}
