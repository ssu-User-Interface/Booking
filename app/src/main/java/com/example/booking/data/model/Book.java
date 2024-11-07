package com.example.booking.data.model;

public class Book {
    private int imageResId;
    private String title;
    private String author;

    public Book(int imageResId, String title, String author) {
        this.imageResId = imageResId;
        this.title = title;
        this.author = author;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
