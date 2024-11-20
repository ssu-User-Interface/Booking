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

    // 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(PlaceSearchKeyword.Place place);
    }

    private OnItemClickListener listener;

    // 클릭 리스너 설정 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MapSearchAdapter(List<PlaceSearchKeyword.Place> places) {
        this.places = places;
    }

    public void updateData(List<PlaceSearchKeyword.Place> newPlaces) {
        this.places.clear();
        this.places.addAll(newPlaces);
        notifyDataSetChanged();
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

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(place); // 클릭된 장소 객체 전달
            }
        });
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