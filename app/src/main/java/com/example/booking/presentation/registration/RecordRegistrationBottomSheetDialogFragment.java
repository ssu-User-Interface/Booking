package com.example.booking.presentation.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        return inflater.inflate(R.layout.fragment_record_bottom_sheet, container, false);
    }
}
