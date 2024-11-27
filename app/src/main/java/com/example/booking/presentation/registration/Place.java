package com.example.booking.presentation.registration;

class Place {
    private String place_name; // 장소명, 업체명
    private String address_name; // 전체 지번 주소
    private String road_address_name; // 전체 도로명 주소
    private String x; // X 좌표값 혹은 longitude
    private String y; // Y 좌표값 혹은 latitude

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getRoad_address_name() {
        return road_address_name;
    }

    public void setRoad_address_name(String road_address_name) {
        this.road_address_name = road_address_name;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
}
