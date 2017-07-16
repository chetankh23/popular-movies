package com.project.popularmovies.interfaces;

/**
 * The interface that handles response from the Web Service API call.
 */
public interface ResponseHandler {
    void handleResponse(String response, String type);
}
