package com.example.booking.presentation.record;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booking.R;
import com.example.booking.dto.response.BookSearchResponseDto;
import com.example.booking.presentation.record.adapter.BookAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<BookSearchResponseDto.BookItemDto> booksWillRead, booksReading, booksRead; // 각 카테고리별 리스트
    private Typeface boldFont, regularFont;
    private TextView tvRecordWill, tvRecordIng, tvRecordPast, tvRecordRegistration;
    private DatabaseReference dbWillRead, dbReading, dbRead; // Firebase 노드 참조

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        // Firebase Database 참조 초기화
        dbWillRead = FirebaseDatabase.getInstance().getReference("will_read_books");
        dbReading = FirebaseDatabase.getInstance().getReference("reading_books");
        dbRead = FirebaseDatabase.getInstance().getReference("read_books");

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerView_book_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 폰트 초기화
        boldFont = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold);
        regularFont = ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular);

        // TextView 초기화
        tvRecordWill = view.findViewById(R.id.tv_record_will);
        tvRecordIng = view.findViewById(R.id.tv_record_ing);
        tvRecordPast = view.findViewById(R.id.tv_record_past);
        tvRecordRegistration = view.findViewById(R.id.tv_record_registration);

        // 카테고리별 데이터 리스트 초기화
        booksWillRead = new ArrayList<>();
        booksReading = new ArrayList<>();
        booksRead = new ArrayList<>();

        // 어댑터 설정 (기본: "읽을 책" 카테고리)
        bookAdapter = new BookAdapter(booksWillRead);
        recyclerView.setAdapter(bookAdapter);

        // Firebase 데이터 로드
        loadBooksFromFirebase(dbWillRead, booksWillRead);

        // 기본 선택된 카테고리 폰트 설정
        setFont(tvRecordWill);

        // 카테고리별 버튼 클릭 이벤트 설정
        tvRecordWill.setOnClickListener(v -> {
            loadBooksFromFirebase(dbWillRead, booksWillRead);
            setFont(tvRecordWill);
        });

        tvRecordIng.setOnClickListener(v -> {
            loadBooksFromFirebase(dbReading, booksReading);
            setFont(tvRecordIng);
        });

        tvRecordPast.setOnClickListener(v -> {
            loadBooksFromFirebase(dbRead, booksRead);
            setFont(tvRecordPast);
        });

        // "책 모아보기" 버튼 클릭 이벤트 설정
        tvRecordRegistration.setOnClickListener(v -> {
            addRandomBookToReadBooks();
        });

        // 책 추가 버튼 클릭 이벤트 설정
        Button addButton = view.findViewById(R.id.btn_main_record_add_book);
        addButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(container);
            navController.navigate(R.id.action_recordFragment_to_searchFragment);
        });

        return view;
    }

    // Firebase에서 데이터 로드
    private void loadBooksFromFirebase(DatabaseReference dbRef, List<BookSearchResponseDto.BookItemDto> bookList) {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    BookSearchResponseDto.BookItemDto book = data.getValue(BookSearchResponseDto.BookItemDto.class);
                    if (book != null) {
                        bookList.add(book);
                    }
                }
                bookAdapter.updateBooks(bookList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "데이터 로드 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // "read_books"에 임의의 책 추가
    private void addRandomBookToReadBooks() {
        BookSearchResponseDto.BookItemDto newBook = new BookSearchResponseDto.BookItemDto();
        newBook.setTitle("임의의 책 제목");
        newBook.setAuthor("임의의 작가");
        newBook.setImage("https://example.com/randombook.jpg");

        dbRead.push().setValue(newBook).addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "새 책이 '읽은 책'에 추가되었습니다.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "책 추가 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // 선택된 TextView에만 폰트를 변경하는 메서드
    private void setFont(TextView selectedTextView) {
        tvRecordWill.setTypeface(regularFont);
        tvRecordIng.setTypeface(regularFont);
        tvRecordPast.setTypeface(regularFont);

        selectedTextView.setTypeface(boldFont);
    }
}
