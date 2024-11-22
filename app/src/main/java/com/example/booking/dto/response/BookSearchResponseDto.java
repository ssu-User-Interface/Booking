package com.example.booking.dto.response;

import java.util.Date;
import java.util.List;

public class BookSearchResponseDto {
    private List<BookItemDto> items;

    public List<BookItemDto> getItems() {
        return items;
    }

    public void setItems(List<BookItemDto> items) {
        this.items = items;
    }

    public static class BookItemDto {
        private String id;
        private String title;
        private String link;
        private String image;
        private String author;
        private String publisher;
        private String pubdate;
        private String description;
        private String readingStatus;

        // 새로 추가된 필드
        private Date createdAt;
        private Date startDate;
        private Date endDate;
        private int totalPages;
        private int readingPage;
        private int rating;
        private String review;

        // 기존 필드
        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getReadingStatus() {
            return readingStatus;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setReadingStatus(String readingStatus) {
            this.readingStatus = readingStatus;
        }

        // 추가된 필드의 getter와 setter
        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getReadingPage() {
            return readingPage;
        }

        public void setReadingPage(int readingPage) {
            this.readingPage = readingPage;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }

        public BookItemDto() {
            // Firebase에서 객체 변환 시 필요
        }
    }
}
