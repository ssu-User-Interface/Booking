package com.example.booking.presentation.map;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.booking.R;

public class MapSearchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate for this view
        View view = inflater.inflate(R.layout.fragment_map_search, container, false);

        // 네비게이션 가져오기
        NavController navController = Navigation.findNavController(container);

        // 버튼 설정 및 클릭 리스너 구현
        ImageView imageView = view.findViewById(R.id.iv_back_arrow);
        imageView.setOnClickListener(v -> {
            navController.navigate(R.id.action_mapSearchFragment_to_mapFragment);
        });

        return view;
    }
}