package com.example.booking.presentation.map;

public class Place {
    private String myTitle;
    private String placeAddress;

    public Place() {
        // Firebase 요구사항을 위한 빈 생성자
    }

    public String getMyTitle() {
        return myTitle;
    }

    public void setMyTitle(String myTitle) {
        this.myTitle = myTitle;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }
}
