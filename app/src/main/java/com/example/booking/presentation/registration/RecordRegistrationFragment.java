package com.example.booking.presentation.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.booking.R;

import java.util.Locale;

public class RecordRegistrationFragment extends Fragment {

    private String currentBookId;  // 현재 책 ID
    private String currentRecordId; // 현재 기록 ID

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_registration, container, false);

        // NavController 가져오기
        NavController navController = Navigation.findNavController(container);

        // 전달받은 타이머 값 설정
        EditText etRecordTime = view.findViewById(R.id.et_record_time);
        Bundle bundle = getArguments();
        long elapsedTimeInMillis = 0;
        if (bundle != null) {
            elapsedTimeInMillis = bundle.getLong("elapsedTime", 0);

            // 시간을 hh:mm:ss 형식으로 변환하여 EditText에 표시
            int hours = (int) (elapsedTimeInMillis / 1000) / 3600;
            int minutes = (int) ((elapsedTimeInMillis / 1000) % 3600) / 60;
            int seconds = (int) (elapsedTimeInMillis / 1000) % 60;

            String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
            etRecordTime.setText(timeFormatted);
        }

        // 전달받은 장소 값 설정
        TextView tvRecordPlace = view.findViewById(R.id.tv_record_place);
        Bundle place_bundle = getArguments();
        if(place_bundle != null && place_bundle.containsKey("selectedPlaceName")) {
            String selectedPlaceName = place_bundle.getString("selectedPlaceName");
            tvRecordPlace.setText(selectedPlaceName);
        }

        // 장소 검색
        TextView placeText = view.findViewById(R.id.tv_record_place);
        placeText.setOnClickListener(v -> {
            Bundle bundle_place = new Bundle();
            bundle_place.putString("bookId", currentBookId);
            bundle_place.putString("recordId",currentRecordId);
            navController.navigate(R.id.action_recordRegistrationFragment_to_recordRegistrationMapSearchFragment,bundle_place);
        });

        // 독서 종료 버튼
        Button openBottomSheetButton = view.findViewById(R.id.btn_complete_reading);
        openBottomSheetButton.setOnClickListener(v -> {
            RecordRegistrationBottomSheetDialogFragment bottomSheetDialogFragment = new RecordRegistrationBottomSheetDialogFragment();
            bottomSheetDialogFragment.show(getParentFragmentManager(), "RecordRegistrationBottomSheetDialogFragment");
        });

        // 기록 저장 버튼
        Button saveRecordButton = view.findViewById(R.id.btn_save_record);
        saveRecordButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_recordRegistrationFragment_to_recordSpecificFragment);
        });

        // 뒤로가기 이미지
        ImageView backArrow = view.findViewById(R.id.iv_back_arrow);
        long finalElapsedTimeInMillis = elapsedTimeInMillis; // effectively final로 사용
        backArrow.setOnClickListener(v -> {
            // 현재 데이터를 Bundle에 저장하여 다시 TimerFragment로 전달
            Bundle backBundle = new Bundle();
            backBundle.putLong("elapsedTime", finalElapsedTimeInMillis);

            navController.navigate(R.id.action_recordRegistrationFragment_to_timerFragment, backBundle);
        });

        return view;
    }
}
