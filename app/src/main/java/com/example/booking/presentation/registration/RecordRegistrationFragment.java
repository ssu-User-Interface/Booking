package com.example.booking.presentation.registration;

import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class RecordRegistrationFragment extends Fragment {

//    @Override
//    public void onResume() {
//        super.onResume();
//        Bundle args = getArguments();
//        if (args != null) {
//            String bookId = args.getString("bookId");
//            if (bookId != null) {
//                Log.d("RecordSpecificFragment", "Reloading data for bookId: " + bookId);
//                loadBookDetails(bookId);
//            } else {
//                Log.e("RecordSpecificFragment", "bookId is null in onResume()");
//            }
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_registration, container, false);

        // NavController 가져오기
        NavController navController = Navigation.findNavController(container);

        // 전달받은 타이머 값 설정
        EditText etRecordTime = view.findViewById(R.id.et_record_time);
        TextView tvRecordRegistrationPlaceText = view.findViewById(R.id.tv_record_place);
        ImageView backArrow = view.findViewById(R.id.iv_back_arrow);

        Bundle bundle = getArguments();
        long elapsedTimeInMillis = 0;

        if (bundle != null) {
            Log.d("RecordRegistrationFragment", "Received Bundle Data:");
            String source = bundle.getString("source", ""); // source 확인 (timer/mapsearch)
            Log.d("sourceSource",source);
            if(source.equals("timer")){
                // bookId 처리
                String bookId = bundle.getString("bookId");
                loadBookDetails(bookId);
                // elapsedTime 처리
                elapsedTimeInMillis = bundle.getLong("elapsedTime", 0L);
                // 장소 정보 처리

                long finalElapsedTimeInMillis = elapsedTimeInMillis;

                // 시간을 hh:mm:ss 형식으로 변환하여 EditText에 표시
                int hours = (int) (elapsedTimeInMillis / 1000) / 3600;
                int minutes = (int) ((elapsedTimeInMillis / 1000) % 3600) / 60;
                int seconds = (int) (elapsedTimeInMillis / 1000) % 60;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

                etRecordTime.setText(timeFormatted);

                // 장소 검색
                tvRecordRegistrationPlaceText.setOnClickListener(v -> {
                    Bundle Bundle_all = new Bundle();
                    Bundle_all.putLong("elapsedTime", finalElapsedTimeInMillis);
                    Bundle_all.putString("bookId", bookId);
                    navController.navigate(R.id.action_recordRegistrationFragment_to_recordRegistrationMapSearchFragment,Bundle_all);
                });

                // 뒤로가기 이미지
                backArrow.setOnClickListener(v -> {
                    // 현재 데이터를 Bundle에 저장하여 다시 TimerFragment로 전달
                    Bundle backBundle = new Bundle();
                    backBundle.putLong("elapsedTime", finalElapsedTimeInMillis);
                    backBundle.putString("bookId",bookId);
                    navController.navigate(R.id.action_recordRegistrationFragment_to_timerFragment, backBundle);
                });

            } else if(source.equals("mapsearch")){
                // bookId 처리
                String bookId = bundle.getString("bookId");
                loadBookDetails(bookId);
                // elapsedTime 처리
                elapsedTimeInMillis = bundle.getLong("elapsedTime", 0L);
                // 장소 정보 처리
                String placeName = bundle.getString("placeName", "선택된 장소 없음");

                // 시간을 hh:mm:ss 형식으로 변환하여 EditText에 표시
                int hours = (int) (elapsedTimeInMillis / 1000) / 3600;
                int minutes = (int) ((elapsedTimeInMillis / 1000) % 3600) / 60;
                int seconds = (int) (elapsedTimeInMillis / 1000) % 60;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

                Log.d("RecordRegistrationFragment", "Received elapsedTimeInMillis from mapsearch: " + elapsedTimeInMillis);


                etRecordTime.setText(timeFormatted);
                tvRecordRegistrationPlaceText.setText(placeName);
            }
        }


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

        return view;
    }

    private void loadBookDetails(String bookId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        DocumentReference bookRef = db.collection("users").document(userId)
                .collection("books").document(bookId);

        // Fetch book details
        bookRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String title = snapshot.getString("title");
                String image = snapshot.getString("image");

                // Update UI with book details
                updateBookDetailsUI(title, image);
            } else {
                Log.e("TimerFragment", "Book not found for bookId: " + bookId);
            }
        }).addOnFailureListener(e -> Log.e("TimerFragment", "Failed to load book details", e));
    }

    private void updateBookDetailsUI(String title, String image) {
        TextView bookTitleTextView = getView().findViewById(R.id.tv_record_registration_book_title);
        ImageView bookImageView = getView().findViewById(R.id.iv_record_registration_book);

        bookTitleTextView.setText(title != null ? title : "제목 없음");

        if (image != null) {
            Glide.with(this).load(image).into(bookImageView);
        }
    }

    // 경과 시간 UI 업데이트
    private void updateElapsedTimeUI(Long elapsedTimeInMillis) {
        if (elapsedTimeInMillis == null) elapsedTimeInMillis = 0L;

        EditText etRecordTime = getView().findViewById(R.id.et_record_time);

        int hours = (int) (elapsedTimeInMillis / 1000) / 3600;
        int minutes = (int) ((elapsedTimeInMillis / 1000) % 3600) / 60;
        int seconds = (int) (elapsedTimeInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        etRecordTime.setText(timeFormatted);
    }
}
