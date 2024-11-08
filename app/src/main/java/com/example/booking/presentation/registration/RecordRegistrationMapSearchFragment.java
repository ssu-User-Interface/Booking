package com.example.booking.presentation.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.booking.R;

public class RecordRegistrationMapSearchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate for this view
        View view = inflater.inflate(R.layout.fragment_record_registration_map_search, container, false);

        // 네비게이션 설정
        NavController navController = Navigation.findNavController(container);

        // 버튼 설정 및 클릭 리스너 설정
        Button button = view.findViewById(R.id.iv_back_arrow);
        button.setOnClickListener(v -> {
            navController.navigate(R.id.action_recordRegistrationMapSearchFragment_to_recordRegistrationFragment);
        });
        return view;
    }
}
