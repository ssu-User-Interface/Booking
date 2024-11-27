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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecordSpecificFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private List<Record> recordList;

    private ImageView icNote, Logo;
    private TextView tvNote, tvDate, tvReview, status;
    private LinearLayout score;
    private View line;

    @Override
    public void onResume() {
        super.onResume();

        Bundle args = getArguments();
        if (args != null) {
            String bookId = args.getString("bookId");
            if (bookId != null) {
                Log.d("RecordSpecificFragment", "Reloading data for bookId: " + bookId);
                loadBookDetails(bookId);
            } else {
                Log.e("RecordSpecificFragment", "bookId is null in onResume()");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_specific, container, false);

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

        NavController navController = Navigation.findNavController(container);

        ImageView backButton = view.findViewById(R.id.iv_record_specific_back_arrow);
        backButton.setOnClickListener(v -> navController.navigate(R.id.action_recordSpecificFragment_to_recordFragment));

        Bundle args = getArguments();
        if (args != null) {
            String bookId = args.getString("bookId");

            Button startButton = view.findViewById(R.id.btn_specific_record_start_timer);
            startButton.setOnClickListener(v ->
                    {
                        Bundle timerBundle = new Bundle();
                        timerBundle.putString("bookId", bookId);

                        navController.navigate(R.id.action_recordSpecificFragment_to_timerFragment, timerBundle);

                    });
        }
        return view;
    }

    private void loadBookDetails(String bookId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        DocumentReference bookRef = db.collection("users").document(userId)
                .collection("books").document(bookId);

        bookRef.get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String title = snapshot.getString("title");
                        String author = snapshot.getString("author");
                        String image = snapshot.getString("image");
                        String readingStatus = snapshot.getString("readingStatus");

                        updateBookDetailsUI(title, author, image);

                        if ("read_books".equals(readingStatus)) {
                            readBooksVisibility();
                        }


                        loadRecordList(bookRef);
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "책 데이터 로드 실패: " + e.getMessage()));
    }

    private void loadRecordList(DocumentReference bookRef) {
        bookRef.collection("records").get()
                .addOnSuccessListener(querySnapshot -> {
                    recordList.clear();
                    for (DocumentSnapshot recordSnapshot : querySnapshot) {
                        Record record = recordSnapshot.toObject(Record.class);
                        if (record != null) {
                            recordList.add(record);
                        }
                    }
                    recordAdapter.notifyDataSetChanged();

                    if (recordList.isEmpty()) {
                        emptyRecordList();
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "기록 데이터 로드 실패: " + e.getMessage()));
    }

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

    public void readBooksVisibility() {
        icNote.setVisibility(View.VISIBLE);
        tvNote.setVisibility(View.VISIBLE);
        tvDate.setVisibility(View.VISIBLE);
        tvReview.setVisibility(View.VISIBLE);
        score.setVisibility(View.VISIBLE);
    }

    public void emptyRecordList() {
        Logo.setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
    }
}
