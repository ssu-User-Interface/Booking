package com.example.booking.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.R;
import com.example.booking.presentation.registration.PlaceSearchKeyword;

import java.util.List;

public class MapSearchAdapter extends RecyclerView.Adapter<MapSearchAdapter.MapSearchViewHolder> {

    private final List<PlaceSearchKeyword.Place> places; // 검색 결과 리스트

    public MapSearchAdapter(List<PlaceSearchKeyword.Place> places) {
        this.places = places;
    }

    @NonNull
    @Override
    public MapSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_map_search, parent, false);
        return new MapSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapSearchViewHolder holder, int position) {
        PlaceSearchKeyword.Place place = places.get(position);
        holder.title.setText(place.getName()); // 장소 이름 설정
        holder.address.setText(place.getAddress()); // 주소 설정
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    static class MapSearchViewHolder extends RecyclerView.ViewHolder {
        TextView title, address;

        public MapSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title_1);
            address = itemView.findViewById(R.id.item_address_1);
        }
    }
}