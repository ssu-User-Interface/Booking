package com.example.booking.presentation.record;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.booking.R;

import java.util.Locale;

public class TimerFragment extends Fragment {

    private TextView tvTimer;
    private Button btnTimerComplete;
    private ImageView ivTimerBackArrow;
    private ToggleButton viewTimerRound;

    private Handler handler;
    private boolean isTimerRunning = false;
    private long timeElapsedInMillis = 0; // 경과 시간 (밀리초)
    private long startTimeInMillis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        // UI 요소 초기화
        tvTimer = view.findViewById(R.id.tv_timer);
        btnTimerComplete = view.findViewById(R.id.btn_timer_complete);
        ivTimerBackArrow = view.findViewById(R.id.iv_timer_back_arrow);
        viewTimerRound = view.findViewById(R.id.view_timer_round);

        handler = new Handler(Looper.getMainLooper());

        // NavController 가져오기
        NavController navController = Navigation.findNavController(container);

        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
            // 타이머와 관련된 데이터
            timeElapsedInMillis = receivedBundle.getLong("elapsedTime", 0);
            updateTimerText();

            // 책 정보와 관련된 데이터
            String bookId = receivedBundle.getString("bookId");
            String bookTitle = receivedBundle.getString("bookTitle");
            String bookImage = receivedBundle.getString("bookImage");
            String bookAuthor = receivedBundle.getString("bookAuthor");
            String category = receivedBundle.getString("category");

            // UI 업데이트 (예: 책 제목 표시)
            TextView bookTitleTextView = view.findViewById(R.id.tv_timer_book_title);
            bookTitleTextView.setText(bookTitle != null ? bookTitle : "책 제목 없음");

            TextView bookAuthorTextView = view.findViewById(R.id.tv_timer_book_author);
            bookAuthorTextView.setText(bookAuthor != null ? bookTitle : "책 저자 없음");

            // 책 이미지를 표시 (Glide 활용)
            ImageView bookImageView = view.findViewById(R.id.iv_timer_book);
            if (bookImage != null) {
                Glide.with(this).load(bookImage).into(bookImageView);
            }

            // 디버깅 로그
            Log.d("TimerFragment", "Received Data - bookId: " + bookId + ", category: " + category);
        }

        // 타이머 시작 버튼 동작
        viewTimerRound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startStopwatch();
            } else {
                pauseStopwatch();
            }
        });

        // 독서 완료 버튼 동작
        btnTimerComplete.setOnClickListener(v -> {
            pauseStopwatch();

            // Bundle 생성 및 데이터 추가
            Bundle bundleToNext = new Bundle();
            bundleToNext.putLong("elapsedTime", timeElapsedInMillis);
            bundleToNext.putString("bookId", getArguments().getString("bookId"));
            bundleToNext.putString("bookTitle", getArguments().getString("bookTitle"));
            bundleToNext.putString("bookImage", getArguments().getString("bookImage"));
            bundleToNext.putString("recordId", getArguments().getString("recordId"));

            // RecordRegistrationFragment로 이동
            navController.navigate(R.id.action_timerFragment_to_recordRegistrationFragment, bundleToNext);
        });


        // 뒤로가기 버튼 동작
        ivTimerBackArrow.setOnClickListener(v -> {
            pauseStopwatch();
            navController.navigate(R.id.action_timerFragment_to_recordSpecificFragment);
        });

        return view;
    }

    private void startStopwatch() {
        isTimerRunning = true;
        startTimeInMillis = System.currentTimeMillis() - timeElapsedInMillis;
        handler.post(timerRunnable);
    }

    private void pauseStopwatch() {
        isTimerRunning = false;
        handler.removeCallbacks(timerRunnable);
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTimerRunning) {
                timeElapsedInMillis = System.currentTimeMillis() - startTimeInMillis;
                updateTimerText();
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void updateTimerText() {
        int hours = (int) (timeElapsedInMillis / 1000) / 3600;
        int minutes = (int) ((timeElapsedInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeElapsedInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        tvTimer.setText(timeFormatted);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pauseStopwatch();
    }
}
