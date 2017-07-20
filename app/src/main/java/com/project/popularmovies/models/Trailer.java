package com.project.popularmovies.models;

/**
 * The class Trailer includes properties and methods to store the details
 * of particular movie trailer.
 */
public class Trailer {

    private String trailerId;
    private String trailerKey;
    private String trailerName;
    private String trailerSite;
    private int trailerSize;
    private String trailerType;

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }

    public String getTrailerSite() {
        return trailerSite;
    }

    public void setTrailerSite(String trailerSite) {
        this.trailerSite = trailerSite;
    }

    public int getTrailerSize() {
        return trailerSize;
    }

    public void setTrailerSize(int trailerSize) {
        this.trailerSize = trailerSize;
    }

    public String getTrailerType() {
        return trailerType;
    }

    public void setTrailerType(String trailerType) {
        this.trailerType = trailerType;
    }
}
