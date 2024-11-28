package com.example.booking.presentation.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.booking.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MapBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_PLACE_NAME = "place_name";

    public static MapBottomSheetDialogFragment newInstance(String placeName) {
        MapBottomSheetDialogFragment fragment = new MapBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_NAME, placeName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_bottom_sheet, container, false);

        // 마커 이름을 표시하는 TextView
        TextView tvPlaceName = view.findViewById(R.id.tv_record_registration);

        // 전달받은 마커 데이터 표시
        if (getArguments() != null) {
            String placeName = getArguments().getString(ARG_PLACE_NAME);
            tvPlaceName.setText(placeName);
        }

        return view;
    }

}
