package com.example.booking.presentation.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.R;
import com.example.booking.data.model.Book;
import com.example.booking.presentation.record.adapter.BookAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_PLACE_NAME = "place_name";

    public static MapBottomSheetDialogFragment newInstance(String placeName) {
        MapBottomSheetDialogFragment fragment = new MapBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_NAME, placeName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_bottom_sheet, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_map_place_book_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String placeName = getArguments().getString(ARG_PLACE_NAME);


        // 마커 이름을 표시하는 TextView
        TextView tvPlaceName = view.findViewById(R.id.tv_record_registration);

        // 전달받은 마커 데이터 표시
        if (getArguments() != null) {
            tvPlaceName.setText(placeName);
        }

        Log.d("MapBottomSheet","start fetch");
        fetchBooksByPlace(placeName, new DataCallback() {
            @Override
            public void onSuccess(List<BookItem> books) {
                Log.d("MapBottomSheet", "Adapter set with item count: " + books.size());

                MapBottomSheetAdapter adapter = new MapBottomSheetAdapter(books);
                adapter.setOnItemClickListener(bookItem -> {
                    // 클릭된 아이템 처리
                    Toast.makeText(getContext(), "Clicked: " + bookItem.getTitle(), Toast.LENGTH_SHORT).show();
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("MapBottomSheet","finish fetch");
        return view;
    }

    private void fetchBooksByPlace(String placeName, DataCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        List<BookItem> bookList = new ArrayList<>();

        firestore.collection("users").get()
                .addOnSuccessListener(userSnapshots -> {
                    for (QueryDocumentSnapshot userSnapshot : userSnapshots) {
                        String userId = userSnapshot.getId();
                        Log.d("MapBottomSheet", "User ID: " + userId);

                        firestore.collection("users").document(userId).collection("books").get()
                                .addOnSuccessListener(bookSnapshots -> {
                                    for (QueryDocumentSnapshot bookSnapshot : bookSnapshots) {
                                        String bookId = bookSnapshot.getId();
                                        String title = bookSnapshot.getString("title");
                                        String author = bookSnapshot.getString("author");
                                        String imageUrl = bookSnapshot.getString("image");

                                        Log.d("MapBottomSheet", "Book ID: " + bookId + ", Title: " + title);

                                        firestore.collection("users").document(userId)
                                                .collection("books").document(bookId)
                                                .collection("records").get()
                                                .addOnSuccessListener(recordSnapshots -> {
                                                    QueryDocumentSnapshot latestRecordSnapshot = null;
                                                    Timestamp latestRecordDate = null;

                                                    for (QueryDocumentSnapshot recordSnapshot : recordSnapshots) {
                                                        String recordPlaceName = recordSnapshot.getString("address");
                                                        Timestamp recordDate = recordSnapshot.getTimestamp("recordDate");

                                                        Log.d("MapBottomSheet", "Record Place Name: " + recordPlaceName);
                                                        Log.d("MapBottomSheet", "Record Date: " + recordDate);

                                                        if (recordPlaceName != null && placeName.equalsIgnoreCase(recordPlaceName.trim())) {
                                                            if (latestRecordDate == null || (recordDate != null && recordDate.toDate().after(latestRecordDate.toDate()))) {
                                                                latestRecordSnapshot = recordSnapshot;
                                                                latestRecordDate = recordDate;

                                                                Log.d("MapBottomSheet", "Updated Latest Record Snapshot ID: " + latestRecordSnapshot.getId());
                                                                Log.d("MapBottomSheet", "Updated Latest Record Date: " + latestRecordDate);
                                                            }
                                                        }
                                                    }

                                                    if (latestRecordSnapshot != null) {
                                                        String recordTitle = latestRecordSnapshot.getString("myTitle");
                                                        String likePhrase = latestRecordSnapshot.getString("phrase");

                                                        // 날짜 형식 변환
                                                        String formattedDate = "";
                                                        if (latestRecordDate != null) {
                                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
                                                            formattedDate = sdf.format(latestRecordDate.toDate());
                                                        }

                                                        bookList.add(new BookItem(imageUrl, title, author, recordTitle, likePhrase, formattedDate));
                                                        Log.d("MapBottomSheet", "Added Book: " + title);
                                                    }

                                                    // 콜백 실행 (각 책 데이터 처리 완료 후)
                                                    callback.onSuccess(bookList);
                                                })
                                                .addOnFailureListener(e -> Log.e("MapBottomSheet", "Failed to fetch records", e));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("MapBottomSheet", "Failed to fetch books", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("MapBottomSheet", "Failed to fetch users", e));
    }

    // 콜백 인터페이스 정의
    public interface DataCallback {
        void onSuccess(List<BookItem> books);
        void onFailure(Exception e);
    }

}
