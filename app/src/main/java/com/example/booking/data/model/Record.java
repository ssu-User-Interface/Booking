package com.example.booking.data.model;

public class Record {
    private String title;
    private String myTitle;
    private String phrase;
    private String address;
    private String date; // String 타입
    private int time;

    // Constructor
    public Record(String title, String myTitle, String memo, String address, int date, int time) {
        this.title = title;
        this.myTitle = myTitle;
        this.phrase = memo;
        this.address = address;
        this.date = String.valueOf(date);
        this.time = time;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getMyTitle() {
        return myTitle;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public int getTime() {
        return time;
    }

    public Record() {
        // Firebase에서 객체 변환 시 필요
    }
}
