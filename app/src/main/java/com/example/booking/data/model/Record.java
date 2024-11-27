package com.example.booking.data.model;

import java.sql.Timestamp;
import java.util.Date;

public class Record {
    private String title;
    private String myTitle;
    private String phrase;
    private String memo;
    private String address;
    private long readingTime;
    private Date recordDate;
    private int readingPage;

    // Constructor
    public Record(String title, String myTitle, String phrase, String memo, String address, int readingPage, long readingTime, Date recordDate) {
        this.title = title;
        this.myTitle = myTitle;
        this.phrase = phrase;
        this.memo = memo;
        this.address = address;
        this.readingTime = readingTime;
        this.recordDate = recordDate;
        this.readingPage = readingPage;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getMemo() {
        return memo;
    }

    public String getMyTitle() {
        return myTitle;
    }

    public int getReadingPage() {
        return readingPage;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getAddress() {
        return address;
    }


    public long getReadingTime() {
        return readingTime;
    }

    public Record() {
        // Firebase에서 객체 변환 시 필요
    }
}
