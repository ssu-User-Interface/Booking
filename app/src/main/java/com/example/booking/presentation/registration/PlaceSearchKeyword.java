package com.example.booking.presentation.registration;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceSearchKeyword {

    @SerializedName("documents")
    private List<Place> places;

    public List<Place> getPlaces() {
        return places;
    }

    public static class Place {
        @SerializedName("place_name")
        private String placeAddress;

        @SerializedName("road_address_name")
        private String address;

        @SerializedName("x")
        private String x; // 경도는 String으로 제공됨

        @SerializedName("y")
        private String y; // 위도는 String으로 제공됨

        public String getPlaceAddress() {
            return placeAddress;
        }

        public String getAddress() {
            return address;
        }

        public double getLatitude() {
            try {
                return Double.parseDouble(y); // 위도를 Double로 변환
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        public double getLongitude() {
            try {
                return Double.parseDouble(x); // 경도를 Double로 변환
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }
}