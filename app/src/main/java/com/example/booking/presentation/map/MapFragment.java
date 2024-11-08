package com.example.booking.presentation.map;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.booking.R;

public class MapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // NavController 가져오기
        NavController navController = Navigation.findNavController(container);

        // 버튼 설정 및 클릭 리스너 추가
        EditText editText = view.findViewById(R.id.et_home_search);
        editText.setOnClickListener(v -> {
            navController.navigate(R.id.action_mapFragment_to_mapSearchFragment);
        });
        return view;
    }
}