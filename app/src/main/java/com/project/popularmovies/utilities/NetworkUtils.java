package com.project.popularmovies.utilities;

import android.net.Uri;

import com.project.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "api_key";

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

    public static String getResponseFromHttpURL(URL url) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = httpConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput)
                return scanner.next();
            else
                return null;
        } finally {
            httpConnection.disconnect();
        }
    }
}
