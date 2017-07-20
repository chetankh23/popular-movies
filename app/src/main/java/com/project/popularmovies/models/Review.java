package com.project.popularmovies.models;

/**
 * The class Review includes properties and methods to store the details
 * of particular review.
 */
public class Review {

    private String author;
    private String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
