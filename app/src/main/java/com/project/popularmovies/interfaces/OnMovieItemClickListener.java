package com.project.popularmovies.interfaces;

/*
* An interface that receives onClick messages when user clicks any
* particular movie item.
*/
public interface OnMovieItemClickListener {
    void onMovieItemClicked(int movieItemClickIndex);
}
