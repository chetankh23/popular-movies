package com.project.popularmovies.utilities;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.project.popularmovies.interfaces.ResponseHandler;

import java.io.IOException;
import java.net.URL;

public class GetMovieTask extends AsyncTask<URL, Void, String> {

    private final ProgressBar mLoadingIndicator;
    private ResponseHandler mResponseHandler;

    public GetMovieTask(ProgressBar loadingIndicator, ResponseHandler responseHandler) {
        mResponseHandler = responseHandler;
        mLoadingIndicator = loadingIndicator;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(URL... params) {
        URL url = params[0];
        String response = null;
        try {
            response = NetworkUtils.getResponseFromHttpURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s != null && !s.equals("")) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mResponseHandler.handleResponse(s);
        }
    }
}
