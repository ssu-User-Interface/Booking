package com.example.booking.dto.response;

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
        private String title;
        private String link;
        private String image;
        private String author;
        private String publisher;
        private String pubdate;
        private String description;

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

        public BookItemDto() {
            // Firebase에서 객체 변환 시 필요
        }
    }


}

