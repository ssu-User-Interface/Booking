package com.example.booking.data.model;

public class Record {
    private String title;
    private String author;
    private String review;
    private String date;
    private int recordCount;
    private int rating;

    // Constructor
    public Record(String title, String author, String review, String date, int recordCount, int rating) {
        this.title = title;
        this.author = author;
        this.review = review;
        this.date = date;
        this.recordCount = recordCount;
        this.rating = rating;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getTitle2() {
        return author;
    }

    public String getReview() {
        return review;
    }

    public String getDate() {
        return date;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public int getRating() {
        return rating;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
