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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private String source;
    private String bookId;
    private String recordId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        NavController navController = Navigation.findNavController(container);

        // Initialize UI elements
        tvTimer = view.findViewById(R.id.tv_timer);
        btnTimerComplete = view.findViewById(R.id.btn_timer_complete);
        ivTimerBackArrow = view.findViewById(R.id.iv_timer_back_arrow);
        viewTimerRound = view.findViewById(R.id.view_timer_round);

        handler = new Handler(Looper.getMainLooper());

        // Get arguments
        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
            bookId = receivedBundle.getString("bookId");
            recordId = receivedBundle.getString("recordId");

            if (bookId != null) {
                loadBookDetails(bookId);
            } else {
                Log.e("TimerFragment", "bookId is null");
            }

            if (recordId != null) {
                Log.d("TimerFragment", "Using recordId: " + recordId);
            }
        }

        // Timer start/stop handling
        viewTimerRound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startStopwatch();
            } else {
                pauseStopwatch();
            }
        });

        // Timer complete action
        btnTimerComplete.setOnClickListener(v -> {
            pauseStopwatch();
            // Bundle with elapsed time and return to RecordRegistrationFragment
            Bundle bundleToNext = new Bundle();
            bundleToNext.putLong("elapsedTime", timeElapsedInMillis);
            bundleToNext.putString("recordId", recordId);
            bundleToNext.putString("bookId", bookId);
            bundleToNext.putString("source","timer");
            navController.navigate(R.id.action_timerFragment_to_recordRegistrationFragment, bundleToNext);
        });

        // Back button handling
        ivTimerBackArrow.setOnClickListener(v -> {
            pauseStopwatch();

            // Navigate back to RecordSpecificFragment with bookId
            Bundle bundleToPrevious = new Bundle();
            bundleToPrevious.putString("bookId", bookId);
            navController.navigate(R.id.action_timerFragment_to_recordSpecificFragment, bundleToPrevious);
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
                String author = snapshot.getString("author");
                String image = snapshot.getString("image");

                // Update UI with book details
                updateBookDetailsUI(title, author, image);
            } else {
                Log.e("TimerFragment", "Book not found for bookId: " + bookId);
            }
        }).addOnFailureListener(e -> Log.e("TimerFragment", "Failed to load book details", e));
    }

    private void updateBookDetailsUI(String title, String author, String image) {
        TextView bookTitleTextView = getView().findViewById(R.id.tv_timer_book_title);
        TextView bookAuthorTextView = getView().findViewById(R.id.tv_timer_book_author);
        ImageView bookImageView = getView().findViewById(R.id.iv_timer_book);

        bookTitleTextView.setText(title != null ? title : "제목 없음");
        bookAuthorTextView.setText(author != null ? author : "저자 없음");

        if (image != null) {
            Glide.with(this).load(image).into(bookImageView);
        }
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
