package com.example.booking.adapter;

import android.graphics.Color;
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
    private int selectedPosition = -1;

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
        selectedPosition = -1; // 데이터 변경 시 선택 초기화
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
        holder.title.setText(place.getPlaceAddress()); // 장소 이름 설정
        holder.address.setText(place.getAddress()); // 주소 설정

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#8E8E8E")); // 선택된 색상
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#EFEBE0")); // 기본 색상
        }


        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // 선택된 위치 변경
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                // 이전 선택된 아이템과 현재 선택된 아이템 업데이트
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

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