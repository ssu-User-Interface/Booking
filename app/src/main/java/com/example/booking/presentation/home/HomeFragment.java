package com.example.booking.presentation.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.booking.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentSnapshot mostRecentDocument; // 전역 변수로 선언
    private boolean hasReadingBook = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // NavController 초기화
        NavController navController = Navigation.findNavController(container);

        // EditText를 클릭 시 검색 Fragment로 이동
        EditText openBookSearchEditText = view.findViewById(R.id.et_home_search);
        openBookSearchEditText.setFocusable(false);

        openBookSearchEditText.setOnClickListener(v -> {
            navController.navigate(R.id.action_homeFragment_to_searchFragment);
        });

        // 위젯 초기화
        openBookSearchEditText.setFocusable(false);
        openBookSearchEditText.setOnClickListener(v -> navController.navigate(R.id.action_homeFragment_to_searchFragment));

        TextView tvBookTitle = view.findViewById(R.id.tv_home_first_widget_book_title);
        View viewLine = view.findViewById(R.id.view_home_line);
        TextView tvReadingPeriod = view.findViewById(R.id.tv_home_first_widget_reading_period);
        TextView tvReadingPage = view.findViewById(R.id.tv_home_first_widget_reading_page);
        ProgressBar prg = view.findViewById(R.id.prg);
        View toTimerButton = view.findViewById(R.id.view_home_first_widget_right_to_timer);
        ImageView ivBookImg = view.findViewById(R.id.iv_home_book);

        ImageView ivBookIcon = view.findViewById(R.id.iv_home_book_ic);
        TextView tvNoBook = view.findViewById(R.id.tv_home_first_widget_no_book);
        View addBookButton = view.findViewById(R.id.view_home_first_widget_right_add_book);
        TextView tvHomeTitleName = view.findViewById(R.id.tv_home_title_name);

        // UI element initialization
        TextView tvDdayResult = view.findViewById(R.id.tv_home_d_day_result);
        TextView tvTotalTimeResult = view.findViewById(R.id.tv_home_total_time_result);

        // 사용자 닉네임 가져오기
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId != null) {
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String nickname = documentSnapshot.getString("nickname");
                        tvHomeTitleName.setText(nickname != null ? nickname + "님," : "사용자님,");
                    })
                    .addOnFailureListener(e -> {
                        tvHomeTitleName.setText("사용자님,");
                        e.printStackTrace();
                    });

                db.collection("users").document(userId).collection("books")
                        .get()
                        .addOnSuccessListener(booksSnapshot -> {
                            if (!booksSnapshot.isEmpty()) {
                                final AtomicInteger totalReadingTimeInSeconds = new AtomicInteger(0);
                                List<Timestamp> recordDates = new ArrayList<>();

                                for (DocumentSnapshot bookDoc : booksSnapshot) {
                                    String bookId = bookDoc.getId();

                                    // Fetch book records
                                    db.collection("users").document(userId)
                                            .collection("books").document(bookId)
                                            .collection("records")
                                            .get()
                                            .addOnSuccessListener(recordsSnapshot -> {
                                                for (DocumentSnapshot recordDoc : recordsSnapshot) {
                                                    // Calculate total reading time
                                                    Long readingTime = recordDoc.getLong("readingTime");
                                                    if (readingTime != null) {
                                                        totalReadingTimeInSeconds.addAndGet(readingTime.intValue());
                                                    }

                                                    // Collect record dates
                                                    Timestamp recordDate = recordDoc.getTimestamp("recordDate");
                                                    if (recordDate != null) {
                                                        recordDates.add(recordDate);
                                                        Log.d("D-Day", "Fetched recordDate: " + recordDate.toDate());
                                                    } else {
                                                        Log.d("D-Day", "recordDate가 null입니다: " + recordDoc.getId());
                                                    }
                                                }

                                                // Update UI: Total Reading Time
                                                String totalTimeFormatted = formatSecondsToTime(totalReadingTimeInSeconds.get());
                                                tvTotalTimeResult.setText(totalTimeFormatted);

                                                // Update UI: D-Day
                                                int consecutiveDays = calculateConsecutiveDays(recordDates);
                                                tvDdayResult.setText("D+" + consecutiveDays);
                                            })
                                            .addOnFailureListener(e -> Log.e("HomeFragment", "Failed to fetch records", e));
                                }
                            } else {
                                tvDdayResult.setText("D+0");
                                tvTotalTimeResult.setText("00:00:00");
                            }
                        })
                        .addOnFailureListener(e -> Log.e("HomeFragment", "Failed to fetch books", e));

            // 책 정보 가져오기
            db.collection("users").document(userId).collection("books")
                    .whereEqualTo("readingStatus", "reading_books")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot mostRecentDocument = null;
                            Timestamp mostRecentTimestamp = null;

                            for (DocumentSnapshot document : querySnapshot) {
                                Timestamp currentTimestamp = document.getTimestamp("startDate");
                                if (currentTimestamp != null) {
                                    if (mostRecentTimestamp == null || currentTimestamp.toDate().after(mostRecentTimestamp.toDate())) {
                                        mostRecentDocument = document;
                                        mostRecentTimestamp = currentTimestamp;
                                    }
                                }
                            }

                            if (mostRecentDocument != null) {
                                hasReadingBook = true;

                                // 책 정보 업데이트
                                String title = mostRecentDocument.getString("title");
                                long totalPages = mostRecentDocument.getLong("totalPages") != null
                                        ? mostRecentDocument.getLong("totalPages")
                                        : 350;
                                long readingPage = mostRecentDocument.getLong("readingPage") != null
                                        ? mostRecentDocument.getLong("readingPage")
                                        : 0;
                                String coverUrl = mostRecentDocument.getString("image"); // 표지 이미지 URL 가져오기

                                tvBookTitle.setText(title != null ? title : "제목 없음");
                                tvReadingPage.setText(readingPage + "/" + totalPages + "p");

                                if (mostRecentTimestamp != null) {
                                    String startDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                                            .format(mostRecentTimestamp.toDate());
                                    tvReadingPeriod.setText(startDate);
                                }

                                prg.setVisibility(View.VISIBLE);
                                // ProgressBar 업데이트
                                if (totalPages > 0) {
                                    prg.setMax((int) totalPages); // totalPages를 max로 설정
                                    prg.setProgress((int) readingPage); // readingPage를 progress로 설정
                                } else {
                                    prg.setMax(1); // 0으로 설정 시 오류가 발생할 수 있으므로 기본값 설정
                                    prg.setProgress(0);
                                }

                                if (coverUrl != null && !coverUrl.isEmpty()) {
                                    Glide.with(requireContext()) // Glide 사용
                                            .load(coverUrl)
                                            .into(ivBookImg);
                                } else {
                                    ivBookImg.setImageResource(R.drawable.img_book); // 표지가 없는 경우 기본 이미지 설정
                                }

                                // 읽고 있는 책 UI
                                tvBookTitle.setVisibility(View.VISIBLE);
                                viewLine.setVisibility(View.VISIBLE);
                                tvReadingPeriod.setVisibility(View.VISIBLE);
                                tvReadingPage.setVisibility(View.VISIBLE);
                                prg.setVisibility(View.VISIBLE);
                                toTimerButton.setVisibility(View.VISIBLE);
                                ivBookImg.setVisibility(View.VISIBLE);

                                ivBookIcon.setVisibility(View.GONE);
                                tvNoBook.setVisibility(View.GONE);
                                addBookButton.setVisibility(View.GONE);

                                final DocumentSnapshot finalMostRecentDocument = mostRecentDocument;

                                toTimerButton.setOnClickListener(v -> {
                                    if (finalMostRecentDocument != null && userId != null) {
                                        // 캡처를 위한 final 변수 선언
                                        final DocumentSnapshot finalDocument = finalMostRecentDocument;
                                        final String finalUserId = userId;

                                        // Firestore 초기화
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                        // 새로운 record 데이터 생성
                                        db.collection("users").document(finalUserId)
                                                .collection("books").document(finalDocument.getId())
                                                .collection("records")
                                                .add(new HashMap<>()) // 빈 데이터로 추가 (recordId만 생성)
                                                .addOnSuccessListener(recordReference -> {
                                                    // 기록 생성 성공
                                                    String recordId = recordReference.getId();
                                                    Log.d("Firestore", "Record created with ID: " + recordId);

                                                    // TimerFragment로 이동 및 데이터 전달
                                                    Bundle timerBundle = new Bundle();
                                                    timerBundle.putString("bookId", finalDocument.getId());

                                                    navController.navigate(R.id.action_homeFragment_to_timerFragment, timerBundle);
                                                })
                                                .addOnFailureListener(e -> {
                                                    // 기록 생성 실패
                                                    Log.e("Firestore", "Failed to create record", e);
                                                    Toast.makeText(getContext(), "기록 생성 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(getContext(), "책 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        }

                        // 책이 없을 경우 처리
                        if (!hasReadingBook) {
                            // "읽는 책 없음" UI 표시
                            ivBookIcon.setVisibility(View.VISIBLE);
                            tvNoBook.setVisibility(View.VISIBLE);
                            addBookButton.setVisibility(View.VISIBLE);

                            // 기존 읽는 책 관련 UI 숨김
                            tvBookTitle.setVisibility(View.GONE);
                            viewLine.setVisibility(View.GONE);
                            tvReadingPeriod.setVisibility(View.GONE);
                            tvReadingPage.setVisibility(View.GONE);
                            prg.setVisibility(View.GONE);
                            toTimerButton.setVisibility(View.GONE);
                            ivBookImg.setVisibility(View.GONE);

                            // 책 추가 버튼 클릭 이벤트
                            addBookButton.setOnClickListener(v -> {
                                navController.navigate(R.id.action_homeFragment_to_searchFragment);
                            });

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "책 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        } else {
            tvHomeTitleName.setText("사용자님,");
        }

        return view;
    }


    // Helper: 초를 HH:mm:ss로 변환
    private String formatSecondsToTime(int totalSeconds) {
        int hours = (totalSeconds / 1000) / 3600;
        int minutes = ((totalSeconds / 1000) % 3600) / 60;
        int seconds = (totalSeconds / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Helper: D-Day 계산
    private int calculateConsecutiveDays(List<Timestamp> recordDates) {
        // 최신순으로 정렬
        recordDates.sort((d1, d2) -> d2.toDate().compareTo(d1.toDate()));

        int consecutiveDays = 0;
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        Log.d("D-Day", "Today's date: " + today);

        // Debug: 정렬된 recordDates 출력
        for (Timestamp recordDate : recordDates) {
            Log.d("D-Day", "Record date: " + recordDate.toDate());
        }

        // 연속 날짜 확인
        for (Timestamp recordDate : recordDates) {
            Date record = recordDate.toDate();
            Log.d("D-Day", "Checking record date: " + record);

            if (consecutiveDays == 0 && isSameDay(record, today)) {
                consecutiveDays++;
                Log.d("D-Day", "Matched today: " + record);
            } else if (isSameDay(record, getPreviousDate(today, consecutiveDays))) {
                consecutiveDays++;
                Log.d("D-Day", "Matched previous day: " + record);
            } else {
                Log.d("D-Day", "No match, breaking loop at date: " + record);
                break; // 연속되지 않는 날이 발견되면 중단
            }
        }

        Log.d("D-Day", "Final Consecutive days: " + consecutiveDays);
        return consecutiveDays;
    }

    // Helper: 이전 날짜 계산
    private Date getPreviousDate(Date date, int daysAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo); // 날짜에서 daysAgo 만큼 이전 날짜 계산
        Date previousDate = calendar.getTime();
        Log.d("D-Day", "Calculated previous date: " + previousDate);
        return previousDate;
    }

    // Helper: 두 날짜가 같은 날인지 확인
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        Log.d("D-Day", "Comparing dates: " + date1 + " and " + date2 + " -> " + sameDay);
        return sameDay;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
