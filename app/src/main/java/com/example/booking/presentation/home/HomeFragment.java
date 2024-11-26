package com.example.booking.presentation.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.booking.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView tvHomeTitleName;
    private TextView tvBookTitle;
    private TextView tvReadingPeriod;
    private TextView tvReadingPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 사용자 ID 가져오기
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(getContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // View 초기화
        tvHomeTitleName = view.findViewById(R.id.tv_home_title_name);
        tvBookTitle = view.findViewById(R.id.tv_home_first_widget_book_title);
        tvReadingPeriod = view.findViewById(R.id.tv_home_first_widget_reading_period);
        tvReadingPage = view.findViewById(R.id.tv_home_first_widget_reading_page);

        // 사용자 이름 설정
        setUserNickname(userId);

        // 가장 최신 startDate의 reading_books 정보 설정
        setMostRecentReadingBook(userId);
    }

    // Firestore에서 사용자 닉네임 가져오기
    private void setUserNickname(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nickname = documentSnapshot.getString("nickname");
                        if (nickname != null) {
                            tvHomeTitleName.setText(nickname + "님,");
                        } else {
                            tvHomeTitleName.setText("사용자님,");
                        }
                    } else {
                        tvHomeTitleName.setText("사용자님,");
                    }
                })
                .addOnFailureListener(e -> {
                    tvHomeTitleName.setText("사용자님,");
                    e.printStackTrace();
                });
    }

    // 가장 최신 startDate의 reading_books 정보 설정
    private void setMostRecentReadingBook(String userId) {
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
                            } else {
                                tvReadingPeriod.setText("날짜 없음");
                            }
                        } else {
                            Toast.makeText(getContext(), "현재 읽고 있는 책이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "현재 읽고 있는 책이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "책 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

}
