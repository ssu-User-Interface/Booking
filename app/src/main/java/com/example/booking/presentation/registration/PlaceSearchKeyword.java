package com.example.booking.presentation.registration;

import android.util.Log;

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

        @SerializedName("y")
        private double longitude; // 경도는 String으로 제공됨

        @SerializedName("x")
        private double latitude; // 위도는 String으로 제공됨

        public String getPlaceAddress() {
            return placeAddress;
        }

        public String getAddress() {
            return address;
        }

        public double getLatitude() {
            try {
                return longitude; // 위도를 Double로 변환
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        public double getLongitude() {
            try {
                return latitude; // 경도를 Double로 변환
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

//        public double getLatitude() {
//            if (latitude != null && !latitude.isEmpty()) {
//                try {
//                    return Double.parseDouble(latitude); // 위도를 Double로 변환
//                } catch (NumberFormatException e) {
//                    Log.e("Place", "Latitude 변환 실패: " + latitude, e);
//                }
//            }
//            return 0.0; // 기본값 반환
//        }
//
//        public double getLongitude() {
//            if (longitude != null && !longitude.isEmpty()) {
//                try {
//                    return Double.parseDouble(longitude); // 경도를 Double로 변환
//                } catch (NumberFormatException e) {
//                    Log.e("Place", "Longitude 변환 실패: " + longitude, e);
//                }
//            }
//            return 0.0; // 기본값 반환
//        }
    }
}