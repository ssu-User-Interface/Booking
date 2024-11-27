package com.example.booking.presentation.record;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.booking.R;
import com.example.booking.data.model.Record;
import com.example.booking.presentation.record.adapter.RecordAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordSpecificFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private List<Record> recordList;

    private ImageView icNote, Logo;
    private TextView tvNote, tvDate, tvReview, status;
    private LinearLayout score;
    private View line;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_specific, container, false);

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerView_record_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recordList = new ArrayList<>();
        recordAdapter = new RecordAdapter(recordList);
        recyclerView.setAdapter(recordAdapter);

        icNote = view.findViewById(R.id.iv_record_specific_note_ic);
        tvNote = view.findViewById(R.id.tv_record_specific_note_count);
        score = view.findViewById(R.id.llayout_record_specific_score);
        tvDate = view.findViewById(R.id.tv_record_specific_date);
        tvReview = view.findViewById(R.id.tv_record_specific_review);

        Logo = view.findViewById(R.id.iv_record_specific_logo);
        status = view.findViewById(R.id.tv_record_specific_status);
        line = view.findViewById(R.id.view_line_2);


        // UI 요소 초기화
        TextView titleTextView = view.findViewById(R.id.tv_record_specific_book_title);
        TextView authorTextView = view.findViewById(R.id.tv_record_main_book_author);
        ImageView imageView = view.findViewById(R.id.iv_record_specific_book);

        // NavController 초기화
        NavController navController = Navigation.findNavController(container);

        // 뒤로 가기 버튼 이벤트
        ImageView backButton = view.findViewById(R.id.iv_record_specific_back_arrow);
        backButton.setOnClickListener(v -> navController.navigate(R.id.action_recordSpecificFragment_to_recordFragment));

        // Bundle에서 데이터 가져오기
        Bundle args = getArguments();
        if (args != null) {
            String bookId = args.getString("bookId");
            String bookTitle = args.getString("bookTitle");
            String bookAuthor = args.getString("bookAuthor");
            String bookImage = args.getString("bookImage");
            String category = args.getString("category"); // 카테고리 정보
            boolean showReadBooksUI = args.getBoolean("showReadBooksUI", false);

            // 독서 시작 버튼 이벤트
            Button startButton = view.findViewById(R.id.btn_specific_record_start_timer);
            startButton.setOnClickListener(v -> {
                // Firestore 초기화
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String userId = auth.getCurrentUser().getUid(); // 현재 사용자 ID

                // Firestore에서 bookId에 해당하는 book 문서의 record 컬렉션에 빈 문서 추가
                db.collection("users").document(userId)
                        .collection("books").document(bookId)
                        .collection("records")
                        .add(new HashMap<>()) // 빈 데이터로 추가
                        .addOnSuccessListener(documentReference -> {
                            // 기록 생성 성공 후 recordId 가져오기
                            String recordId = documentReference.getId();
                            Log.d("Firestore", "Record created with ID: " + recordId);

                            // Bundle 생성 및 TimerFragment로 이동
                            Bundle timerBundle = new Bundle();
                            timerBundle.putString("recordId", recordId);
                            timerBundle.putString("bookId", bookId);
                            timerBundle.putString("bookTitle", bookTitle);
                            timerBundle.putString("bookImage", bookImage);
                            timerBundle.putString("bookAuthor", bookAuthor);
                            timerBundle.putString("category", category);

                            navController.navigate(R.id.action_recordSpecificFragment_to_timerFragment, timerBundle);
                        })
                        .addOnFailureListener(e -> {
                            // 기록 생성 실패 로그 및 메시지
                            Log.e("Firestore", "Failed to create record", e);
                            Toast.makeText(getContext(), "기록 생성 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });


            Log.d("RecordSpecificFragment", "bookId: " + bookId + ", category: " + category);

            if (showReadBooksUI) {
                readBooksVisibility();
            }

            if (bookId != null && category != null) {
                // 기본 UI 업데이트
                titleTextView.setText(bookTitle != null ? bookTitle : "Loading...");
                authorTextView.setText(bookAuthor != null ? bookAuthor : "Loading...");
                if (bookImage != null) {
                    Glide.with(this).load(bookImage).into(imageView);
                }

                // Firebase에서 데이터 로드
                loadBookDetails(category, bookId, recordList, recordAdapter);
            } else {
                Log.e("RecordSpecificFragment", "bookId or category is null");
            }
        }
        return view;

    }

    private void loadBookDetails(String category, String bookId, List<Record> recordList, RecordAdapter adapter) {
        // Firestore 초기화
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        // Firestore 경로 설정 (category는 Firestore 구조에서 필요하지 않으므로 제거 가능)
        DocumentReference bookRef = db.collection("users").document(userId)
                .collection("books").document(bookId);

        // Firestore에서 책 정보 로드
        bookRef.get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        // 책 정보 로드
                        String title = snapshot.getString("title");
                        String author = snapshot.getString("author");
                        String image = snapshot.getString("image");

                        Log.d("FirestoreData", "Title: " + title + ", Author: " + author);

                        // UI 업데이트
                        updateBookDetailsUI(title, author, image);

                        // 기록 데이터 로드
                        bookRef.collection("records").get()
                                .addOnSuccessListener(querySnapshot -> {
                                    recordList.clear();
                                    for (DocumentSnapshot recordSnapshot : querySnapshot) {
                                        Record record = recordSnapshot.toObject(Record.class);
                                        if (record != null) {
                                            recordList.add(record);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();

                                    // 기록이 비어 있는 경우 처리
                                    if (recordList.isEmpty()) {
                                        emptyRecordList();
                                        status.setText("기록이 비어있어요.");
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("FirestoreError", "기록 데이터 로드 실패: " + e.getMessage()));
                    } else {
                        Log.e("FirestoreError", "책 데이터를 찾을 수 없음");
                        Toast.makeText(getContext(), "책 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "책 데이터 로드 실패: " + e.getMessage()));
    }


    // 책 세부 정보 UI 업데이트
    private void updateBookDetailsUI(String title, String author, String image) {
        TextView titleTextView = getView().findViewById(R.id.tv_record_specific_book_title);
        TextView authorTextView = getView().findViewById(R.id.tv_record_main_book_author);
        ImageView imageView = getView().findViewById(R.id.iv_record_specific_book);

        titleTextView.setText(title != null ? title : "제목 없음");
        authorTextView.setText(author != null ? author : "저자 없음");

        if (image != null) {
            Glide.with(this).load(image).into(imageView);
        }
    }
    public void readBooksVisibility()
    {
        icNote.setVisibility(View.VISIBLE);
        tvNote.setVisibility(View.VISIBLE);
        tvDate.setVisibility(View.VISIBLE);
        tvReview.setVisibility(View.VISIBLE);
        score.setVisibility(View.VISIBLE);
    }

    public void emptyRecordList()
    {
        Logo.setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
    }
}
