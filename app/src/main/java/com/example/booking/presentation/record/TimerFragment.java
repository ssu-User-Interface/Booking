package com.example.booking.presentation.record;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

            // 결과값 전달
            Bundle bundle = new Bundle();
            bundle.putLong("elapsedTime", timeElapsedInMillis);

            navController.navigate(R.id.action_timerFragment_to_recordRegistrationFragment, bundle);
        });

        // 뒤로가기 버튼 동작
        ivTimerBackArrow.setOnClickListener(v -> {
            pauseStopwatch();
            navController.navigate(R.id.action_timerFragment_to_recordSpecificFragment);
        });

        // 타이머 초기화
        updateTimerText();

        return view;
    }

    // 스톱워치 시작
    private void startStopwatch() {
        isTimerRunning = true;
        startTimeInMillis = System.currentTimeMillis() - timeElapsedInMillis;
        handler.post(timerRunnable);
    }

    // 스톱워치 일시정지
    private void pauseStopwatch() {
        isTimerRunning = false;
        handler.removeCallbacks(timerRunnable);
    }

    // 스톱워치 업데이트 작업
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTimerRunning) {
                timeElapsedInMillis = System.currentTimeMillis() - startTimeInMillis;
                updateTimerText();
                handler.postDelayed(this, 1000); // 1초마다 갱신
            }
        }
    };

    // 타이머 텍스트 업데이트
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
        pauseStopwatch(); // Fragment 종료 시 스톱워치 정지
    }
}
