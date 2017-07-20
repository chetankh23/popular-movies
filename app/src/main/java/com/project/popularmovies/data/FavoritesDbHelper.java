package com.project.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "favoritesDb.db";

    // Database Version
    private static final int VERSION = 1;

    // Constructor
    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * The method onCreate() is invoked when favorites database is created for the first time.
     *
     * @param db Instance of SQLiteDatabase.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create favorites table using SQL Formatting rules.
        final String CREATE_TABLE = "CREATE TABLE " + FavoriteContract.FavoriteEntry.TABLE_NAME + "(" +
                FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoriteContract.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_BACKDROP_URL + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    /**
     * The method onUpgrade() discards the old table of data and calls onCreate()
     * to create a new one. This occurs when the DATABASE VERSION is incremented.
     *
     * @param db Instance of SQLiteDatabase.
     * @param oldVersion Old version of database.
     * @param newVersion New version of database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
