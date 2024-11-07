package com.example.booking.presentation.search;

public class Book {
    private String title;
    private String author;
    private String publisher;
    private String year;
    private String coverImageUrl;

    public Book(String title, String author, String publisher, String year, String coverImageUrl) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.coverImageUrl = coverImageUrl;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getYear() { return year; }
    public String getCoverImageUrl() { return coverImageUrl; }
}
