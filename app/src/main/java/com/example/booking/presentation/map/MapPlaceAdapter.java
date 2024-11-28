package com.example.booking.presentation.map;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.R;
import com.example.booking.adapter.MapSearchAdapter;
import com.example.booking.presentation.registration.PlaceSearchKeyword;

import java.util.List;

public class MapPlaceAdapter extends RecyclerView.Adapter<MapPlaceAdapter.MapPlaceViewHolder> {

    private List<PlaceSearchKeyword.Place> placeList;
    private int selectedPosition = -1;


    // 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener2 {
        void onItemClick(PlaceSearchKeyword.Place place);
    }

    private OnItemClickListener2 listener;

    // 클릭 리스너 설정 메서드
    public void setOnItemClickListener(OnItemClickListener2 listener) {
        this.listener = listener;
    }


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

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#8E8E8E")); // 선택된 색상
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FBFAF7")); // 기본 색상
        }

        // 클릭 리스너 연결
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged(); // 선택 상태를 갱신
            if (listener != null) {
                listener.onItemClick(place); // 클릭 이벤트 전달
            }
        });
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
