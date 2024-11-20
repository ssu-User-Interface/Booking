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
        private String name;

        @SerializedName("road_address_name")
        private String address;

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }
    }
}