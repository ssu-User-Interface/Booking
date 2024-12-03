package com.example.booking.presentation.map;

public class BookItem {
    private String imageUrl;      // 책 이미지 URL
    private String title;         // 책 제목
    private String author;        // 책 저자
    private String recordTitle;   // 가장 최근 기록 제목
    private String likePhrase;    // 가장 최근 기록의 마음에 드는 구절
    private String recordDate;    // 기록 날짜

    public BookItem(String imageUrl, String title, String author, String recordTitle, String likePhrase, String recordDate) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.author = author;
        this.recordTitle = recordTitle;
        this.likePhrase = likePhrase;
        this.recordDate = recordDate;
    }

    // Getter 메서드
    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getRecordTitle() { return recordTitle; }
    public String getLikePhrase() { return likePhrase; }
    public String getRecordDate() { return recordDate; }
}
