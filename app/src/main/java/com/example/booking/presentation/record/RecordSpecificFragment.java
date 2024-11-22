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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

        // 독서 시작 버튼 이벤트
        Button startButton = view.findViewById(R.id.btn_specific_record_start_timer);
        startButton.setOnClickListener(v -> navController.navigate(R.id.action_recordSpecificFragment_to_timerFragment));

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
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference(category).child(bookId);

        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                String author = snapshot.child("author").getValue(String.class);
                String image = snapshot.child("image").getValue(String.class);

                Log.d("FirebaseData", "Title: " + title + ", Author: " + author);

                // 기록 데이터 로드
                DataSnapshot recordsSnapshot = snapshot.child("records");
                recordList.clear();
                for (DataSnapshot recordSnapshot : recordsSnapshot.getChildren()) {
                    Record record = recordSnapshot.getValue(Record.class);
                    if (record != null) {
                        recordList.add(record);
                    }
                }
                adapter.notifyDataSetChanged();

                // 기록이 비어 있는 경우 처리
                if (recordList.isEmpty()) {
                    emptyRecordList();
                    if ("will_read_books".equals(category)) {
                        status.setText("독서를 시작하세요!");
                    } if ("reading_books".equals(category)) {
                        status.setText("기록이 비어있어요.");
                    } else if("read_books".equals(category)) {
                        status.setText("기록을 추가하시겠어요?");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database Error: " + error.getMessage());
            }
        });
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
