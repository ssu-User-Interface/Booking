package com.example.booking.presentation.registration;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.booking.R;
import com.example.booking.presentation.registration.RecordRegistrationBottomSheetDialogFragment;

public class RecordRegistrationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_registration, container, false);

        // NavControl 가져오기
        NavController navController = Navigation.findNavController(container);

        // 버튼 설정 및 클릭 리스너 추가
        LinearLayout layout = view.findViewById(R.id.layout_record_place);
        layout.setOnClickListener(v -> {
            navController.navigate(R.id.action_recordRegistrationFragment_to_recordRegistrationMapSearchFragment);
        });

        // 독서 종료 버튼
        Button openBottomSheetButton = view.findViewById(R.id.btn_complete_reading);
        openBottomSheetButton.setOnClickListener(v -> {
            RecordRegistrationBottomSheetDialogFragment bottomSheetDialogFragment = new RecordRegistrationBottomSheetDialogFragment();
            bottomSheetDialogFragment.show(getParentFragmentManager(),"RecordRegistrationBottomSheetDialogFragment");
        });

        // 기록 저장 버튼
        Button saveRecordButton = view.findViewById(R.id.btn_save_record);
        saveRecordButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_recordRegistrationFragment_to_recordSpecificFragment);
        });

        return view;
    }

}
