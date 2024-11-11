package com.example.booking.presentation.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.booking.R;
import com.example.booking.presentation.record.RecordFragment;
import com.example.booking.presentation.record.RecordSpecificFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RecordRegistrationBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }


    // 바텀시트 저장하기 버튼
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_bottom_sheet, container, false);


        Button saveButton = view.findViewById(R.id.btn_save_record);
        saveButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              NavController navController = Navigation.findNavController(requireActivity(), R.id.main_frm);
              navController.navigate(R.id.action_recordRegistrationFragment_to_recordSpecificFragment);
              dismiss();
          }
      });

        return view;
    }
}
