package com.example.booking.presentation.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.R;
import com.example.booking.presentation.registration.PlaceSearchKeyword;

import java.util.List;

public class MapPlaceAdapter extends RecyclerView.Adapter<MapPlaceAdapter.MapPlaceViewHolder> {

    private List<PlaceSearchKeyword.Place> placeList;

    public MapPlaceAdapter(List<PlaceSearchKeyword.Place> placeList) {
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public MapPlaceAdapter.MapPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_search, parent, false);
        return new MapPlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapPlaceAdapter.MapPlaceViewHolder holder, int position) {
        PlaceSearchKeyword.Place place = placeList.get(position);

        // ViewHolder에 데이터 바인딩
        holder.placeName.setText(place.getAddress());
        holder.placeAddress.setText(place.getPlaceAddress());
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class MapPlaceViewHolder extends RecyclerView.ViewHolder {
        TextView placeName, placeAddress;
        public MapPlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.item_title_1);
            placeAddress = itemView.findViewById(R.id.item_address_1);
        }
    }
}
