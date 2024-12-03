package com.example.booking.presentation.record;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booking.R;
import com.example.booking.dto.response.BookSearchResponseDto;
import com.example.booking.presentation.record.adapter.BookAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordFragment extends Fragment {
    private RecordViewModel viewModel;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<BookSearchResponseDto.BookItemDto> booksWillRead, booksReading, booksRead;
    private Typeface boldFont, regularFont;
    private TextView tvRecordWill, tvRecordIng, tvRecordPast;
    private FirebaseFirestore db;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(RecordViewModel.class);

        // Firebase Auth로 사용자 ID 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return view;
        }
        // Firestore 초기화
        db = FirebaseFirestore.getInstance();

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerView_book_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        bookAdapter = new BookAdapter(new ArrayList<>());
        recyclerView.setAdapter(bookAdapter);

        // 폰트 초기화
        boldFont = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold);
        regularFont = ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular);

        // TextView 초기화
        tvRecordWill = view.findViewById(R.id.tv_record_will);
        tvRecordIng = view.findViewById(R.id.tv_record_ing);
        tvRecordPast = view.findViewById(R.id.tv_record_past);

        // 카테고리 데이터 리스트 초기화
        booksWillRead = new ArrayList<>();
        booksReading = new ArrayList<>();
        booksRead = new ArrayList<>();

        // 도서 추가 버튼 초기화 및 클릭 리스너 설정
        Button addBookButton = view.findViewById(R.id.btn_main_record_add_book);
        addBookButton.setOnClickListener(v -> {
            // NavController를 사용하여 SearchFragment로 이동
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_recordFragment_to_searchFragment);
        });

        // 데이터 로드 및 UI 업데이트
        String currentCategory = viewModel.getCurrentCategory(); // ViewModel에서 상태 가져오기
        loadBooksByCategory(currentCategory);
        setFont(getCategoryTextView(currentCategory));

        // 카테고리 버튼 클릭 이벤트 설정
        setupCategoryClickListeners();

        // 어댑터 클릭 리스너 설정
        bookAdapter.setOnItemClickListener(book -> navigateToRecordSpecific(book, view));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 전달받은 카테고리 복원
        if (getArguments() != null) {
            String restoredCategory = getArguments().getString("currentCategory");
            if (restoredCategory != null) {
                viewModel.setCurrentCategory(restoredCategory);
            }
        }

        // ViewModel에서 현재 카테고리 가져오기
        String currentCategory = viewModel.getCurrentCategory();
        loadBooksByCategory(currentCategory);
        setFont(getCategoryTextView(currentCategory));
    }

    private void setupCategoryClickListeners() {
        tvRecordWill.setOnClickListener(v -> onCategorySelected("will_read_books", tvRecordWill));
        tvRecordIng.setOnClickListener(v -> onCategorySelected("reading_books", tvRecordIng));
        tvRecordPast.setOnClickListener(v -> onCategorySelected("read_books", tvRecordPast));
    }

    private void onCategorySelected(String category, TextView selectedTextView) {
        viewModel.setCurrentCategory(category); // ViewModel에 저장
        loadBooksByCategory(category);
        setFont(selectedTextView);
    }

    private void loadBooksByCategory(String category) {
        List<BookSearchResponseDto.BookItemDto> bookList = getCategoryList(category);
        db.collection("users").document(userId).collection("books")
                .whereEqualTo("readingStatus", category)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookList.clear();
                    List<BookSearchResponseDto.BookItemDto> sortedBooks = sortBooksByCreatedAt(querySnapshot);
                    bookList.addAll(sortedBooks);
                    bookAdapter.updateBooks(bookList);
                })
                .addOnFailureListener(e -> Log.e("RecordFragment", "Firestore 요청 실패", e));
    }

    private List<BookSearchResponseDto.BookItemDto> sortBooksByCreatedAt(QuerySnapshot querySnapshot) {
        List<BookSearchResponseDto.BookItemDto> booksWithCreatedAt = new ArrayList<>();
        List<BookSearchResponseDto.BookItemDto> booksWithoutCreatedAt = new ArrayList<>();

        for (QueryDocumentSnapshot doc : querySnapshot) {
            BookSearchResponseDto.BookItemDto book = doc.toObject(BookSearchResponseDto.BookItemDto.class);
            book.setId(doc.getId());
            if (doc.contains("createdAt") && doc.getTimestamp("createdAt") != null) {
                book.setCreatedAt(doc.getTimestamp("createdAt").toDate());
                booksWithCreatedAt.add(book);
            } else {
                booksWithoutCreatedAt.add(book);
            }
        }

        booksWithCreatedAt.sort((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()));
        booksWithCreatedAt.addAll(booksWithoutCreatedAt);
        return booksWithCreatedAt;
    }

    private List<BookSearchResponseDto.BookItemDto> getCategoryList(String category) {
        switch (category) {
            case "reading_books":
                return booksReading;
            case "read_books":
                return booksRead;
            default:
                return booksWillRead;
        }
    }

    private TextView getCategoryTextView(String category) {
        switch (category) {
            case "reading_books":
                return tvRecordIng;
            case "read_books":
                return tvRecordPast;
            default:
                return tvRecordWill;
        }
    }

    private void setFont(TextView selectedTextView) {
        tvRecordWill.setTypeface(regularFont);
        tvRecordIng.setTypeface(regularFont);
        tvRecordPast.setTypeface(regularFont);
        selectedTextView.setTypeface(boldFont);
    }

    private void navigateToRecordSpecific(BookSearchResponseDto.BookItemDto book, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("bookId", book.getId());
        bundle.putString("category", viewModel.getCurrentCategory());

        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_recordFragment_to_recordSpecificFragment, bundle,
                new NavOptions.Builder().setPopUpTo(R.id.recordFragment, false).build());
    }
}
