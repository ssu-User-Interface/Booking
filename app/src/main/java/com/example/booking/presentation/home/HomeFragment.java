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

import com.example.booking.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

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

            // 책 정보 가져오기
            db.collection("users").document(userId).collection("books")
                    .whereEqualTo("readingStatus", "will_read_books")
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
                                        : 0;
                                long readingPage = mostRecentDocument.getLong("readingPage") != null
                                        ? mostRecentDocument.getLong("readingPage")
                                        : 0;

                                tvBookTitle.setText(title != null ? title : "제목 없음");
                                tvReadingPage.setText(readingPage + "/" + totalPages + "p");

                                if (mostRecentTimestamp != null) {
                                    String startDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                                            .format(mostRecentTimestamp.toDate());
                                    tvReadingPeriod.setText(startDate);
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
                                                    timerBundle.putString("bookTitle", finalDocument.getString("title"));
                                                    timerBundle.putString("recordId", recordId);
                                                    timerBundle.putString("bookImage", finalDocument.getString("image"));
                                                    timerBundle.putString("bookAuthor", finalDocument.getString("author"));

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
                            tvBookTitle.setVisibility(View.GONE);
                            viewLine.setVisibility(View.GONE);
                            tvReadingPeriod.setVisibility(View.GONE);
                            tvReadingPage.setVisibility(View.GONE);
                            prg.setVisibility(View.GONE);
                            toTimerButton.setVisibility(View.GONE);
                            ivBookImg.setVisibility(View.GONE);

                            ivBookIcon.setVisibility(View.VISIBLE);
                            tvNoBook.setVisibility(View.VISIBLE);
                            addBookButton.setVisibility(View.VISIBLE);

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
