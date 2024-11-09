package com.example.booking.presentation.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.booking.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RecordRegistrationBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflater 설정
        View view = inflater.inflate(R.layout.fragment_record_bottom_sheet, container, false);

        // navigation 가져오기
        NavController navController = Navigation.findNavController(container);

        // 버튼 및 클릭 리스너 설정
        Button saveButton = view.findViewById(R.id.btn_save_record);
        saveButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_recordRegistrationBottomSheetDialogFragment_to_recordSpecificFragment);
        });

        return view;
    }
}
