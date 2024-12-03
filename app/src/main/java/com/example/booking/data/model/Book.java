package com.example.booking.data.model;

public class Book {
    private String imageResId;
    private String title;
    private String author;

    public Book(String imageResId, String title, String author) {
        this.imageResId = imageResId;
        this.title = title;
        this.author = author;
    }

    public String getImageResId() {
        return imageResId;
    }
    public String getAuthor() {

        return author;
    }

    public String getTitle() {
        return title;
    }

    public void setImageResId(String imageResId) {
        this.imageResId = imageResId;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void setAuthor(String author) {
        this.author = author;
    }
}
