package com.example.booking.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;


public class BookSearchPageResponseDto {

    @SerializedName("item")
    private List<BookItemDto> items = new ArrayList<>(); // 빈 배열도 기본값으로 처리

    @SerializedName("totalResults")
    private int totalResults;

    @SerializedName("itemsPerPage")
    private int itemsPerPage;

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public List<BookItemDto> getItems() {
        return items;
    }

    public void setItems(List<BookItemDto> items) {
        this.items = items;
    }

    public static class BookItemDto {
        @SerializedName("title")
        private String title; // 책 제목

        @SerializedName("author")
        private String author; // 책 지은이

        @SerializedName("publisher")
        private String publisher; // 출판사

        @SerializedName("pubdate")
        private String pubdate; // 출판 날짜

        @SerializedName("cover")
        private String cover; // 표지 사진

        @SerializedName("bookinfo")
        private BookInfo bookInfo;

        @SerializedName("isbn")
        private  String isbn;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return cover;
        }

        public void setImage(String image) {
            this.cover = image;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public String getPubdate() {
            return pubdate;
        }

        public void setPubdate(String pubdate) {
            this.pubdate = pubdate;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }


        public BookInfo getBookInfo() {
            return bookInfo;
        }

        public void setBookInfo(BookInfo bookInfo) {
            this.bookInfo = bookInfo;
        }

    }
    public static class BookInfo {

        @SerializedName("itemPage")
        private Integer itemPage;

        public Integer getItemPage() {
            return itemPage != null ? itemPage : 0; // null이면 기본값 0 반환
        }

        public void setItemPage(Integer itemPage) {
            this.itemPage = itemPage;
        }
    }
}
