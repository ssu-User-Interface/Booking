package com.example.booking.presentation.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.booking.R;

public class TimerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        //NavCotroller 가져오기
        NavController navController = Navigation.findNavController(container);

        // 버튼 초기화 및 클릭 이벤트 설정
        Button addButton = view.findViewById(R.id.btn_timer_complete);
        addButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_timerFragment_to_recordRegistrationFragment);
        });

        // 버튼 초기화 및 클릭 이벤트 설정
        ImageView quitButton = view.findViewById(R.id.iv_timer_back_arrow);
        quitButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_timerFragment_to_recordSpecificFragment);
        });


        return view;
    }
}
