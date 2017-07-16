package com.project.popularmovies.utilities;

import android.net.Uri;

import com.project.popularmovies.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class NetworkUtils {

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "api_key";
    private static final String VIDEOS = "videos";
    private static final String REVIEWS = "reviews";

    public static URL buildURL(String choice) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(choice)
                .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildTrailersURL(int movieId) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(VIDEOS)
                .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildReviewsURL(int movieId) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(REVIEWS)
                .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    public static String getResponseFromHttpURL(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        BufferedReader inputStream = null;
        try {
            inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput)
                return scanner.next();
            else
                return null;
        } finally {
            if(inputStream != null)
                inputStream.close();
        }
    }
}
