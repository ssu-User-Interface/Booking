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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<BookSearchResponseDto.BookItemDto> booksWillRead, booksReading, booksRead; // 각 카테고리별 리스트
    private Typeface boldFont, regularFont;
    private TextView tvRecordWill, tvRecordIng, tvRecordPast, tvRecordRegistration;
    private String currentCategory = "will_read_books"; // 기본 선택 카테고리
    private FirebaseFirestore db; // Firestore 인스턴스
    private String userId; // 현재 로그인한 사용자 ID

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

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

        // 어댑터 설정
        bookAdapter = new BookAdapter(getCurrentBookList());
        recyclerView.setAdapter(bookAdapter);
        loadBooksByCategory(currentCategory);
        setFont(getCurrentCategoryTextView());

        // 카테고리별 버튼 클릭 이벤트 설정
        tvRecordWill.setOnClickListener(v -> {
            currentCategory = "will_read_books";
            loadBooksByCategory(currentCategory);
            setFont(tvRecordWill);
            addRandomBookToWillReadBooks();
        });

        tvRecordIng.setOnClickListener(v -> {
            currentCategory = "reading_books";
            loadBooksByCategory(currentCategory);
            setFont(tvRecordIng);
            addRandomBookToReadingBooks();
        });

        tvRecordPast.setOnClickListener(v -> {
            currentCategory = "read_books";
            loadBooksByCategory(currentCategory);
            setFont(tvRecordPast);
            addRandomBookToReadBooks();
        });

        // 책 추가 버튼 클릭 이벤트 설정
        Button addButton = view.findViewById(R.id.btn_main_record_add_book);
        addButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(container);
            navController.navigate(R.id.action_recordFragment_to_searchFragment);
        });

        // 어댑터 클릭 리스너 설정
        bookAdapter.setOnItemClickListener(book -> {
            String bookId = book.getId();
            Bundle bundle = new Bundle();
            bundle.putString("bookId", bookId);
            bundle.putString("bookTitle", book.getTitle());
            bundle.putString("bookAuthor", book.getAuthor());
            bundle.putString("bookImage", book.getImage());
            bundle.putString("category", currentCategory);

            NavController navController = Navigation.findNavController(container);
            navController.navigate(R.id.action_recordFragment_to_recordSpecificFragment, bundle);
        });

        return view;
    }

    private List<BookSearchResponseDto.BookItemDto> getCurrentBookList() {
        switch (currentCategory) {
            case "reading_books":
                return booksReading;
            case "read_books":
                return booksRead;
            default:
                return booksWillRead;
        }
    }

    private TextView getCurrentCategoryTextView() {
        switch (currentCategory) {
            case "reading_books":
                return tvRecordIng;
            case "read_books":
                return tvRecordPast;
            default:
                return tvRecordWill;
        }
    }

    private void loadBooksByCategory(String category) {
        List<BookSearchResponseDto.BookItemDto> bookList = getCurrentBookList();
        db.collection("users").document(userId).collection("books")
                .whereEqualTo("readingStatus", category)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookList.clear(); // 기존 데이터를 초기화
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        BookSearchResponseDto.BookItemDto book = doc.toObject(BookSearchResponseDto.BookItemDto.class);
                        book.setId(doc.getId()); // Firestore 문서 ID 설정
                        bookList.add(book); // 책 리스트에 추가
                    }
                    bookAdapter.updateBooks(bookList); // 어댑터에 데이터 갱신
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "데이터 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void setFont(TextView selectedTextView) {
        tvRecordWill.setTypeface(regularFont);
        tvRecordIng.setTypeface(regularFont);
        tvRecordPast.setTypeface(regularFont);

        selectedTextView.setTypeface(boldFont);
    }

    private void addBookToFirestore(String userId, BookSearchResponseDto.BookItemDto book) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> bookData = new HashMap<>();
        bookData.put("title", book.getTitle());
        bookData.put("author", book.getAuthor());
        bookData.put("publisher", book.getPublisher());
        bookData.put("image", book.getImage());
        bookData.put("description", book.getDescription());
        bookData.put("readingStatus", book.getReadingStatus());

        db.collection("users").document(userId).collection("books")
                .add(bookData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "책 정보 저장 성공"))
                .addOnFailureListener(e -> Log.e("Firestore", "책 정보 저장 실패", e));
    }

    private void addRandomBookToWillReadBooks() {
        BookSearchResponseDto.BookItemDto newBook = new BookSearchResponseDto.BookItemDto();
        newBook.setTitle("읽을 책 제목");
        newBook.setAuthor("읽을 작가");
        newBook.setPublisher("읽을 출판사");
        newBook.setImage("https://example.com/reading.jpg");
        newBook.setDescription("읽을 책 설명");
        newBook.setPubdate("2024");
        newBook.setReadingStatus("will_read_books");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("books")
                .add(newBook) // Firestore가 자동으로 ID 생성
                .addOnSuccessListener(documentReference -> {
                    // Firestore에서 생성된 ID를 가져옴
                    String generatedId = documentReference.getId();
                    documentReference.update("id", generatedId) // 생성된 ID를 해당 문서에 업데이트
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "ID 업데이트 성공"))
                            .addOnFailureListener(e -> Log.e("Firestore", "ID 업데이트 실패", e));

                    Toast.makeText(getContext(), "새 책이 '읽는 중 책'에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "책 추가 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addRandomBookToReadBooks() {
        BookSearchResponseDto.BookItemDto newBook = new BookSearchResponseDto.BookItemDto();
        newBook.setTitle("읽는 중 책 제목");
        newBook.setAuthor("읽는 중 작가");
        newBook.setPublisher("읽는 중 출판사");
        newBook.setImage("https://example.com/reading.jpg");
        newBook.setDescription("읽는 중 책 설명");
        newBook.setPubdate("2024");
        newBook.setReadingStatus("read_books");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("books")
                .add(newBook) // Firestore가 자동으로 ID 생성
                .addOnSuccessListener(documentReference -> {
                    // Firestore에서 생성된 ID를 가져옴
                    String generatedId = documentReference.getId();
                    documentReference.update("id", generatedId) // 생성된 ID를 해당 문서에 업데이트
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "ID 업데이트 성공"))
                            .addOnFailureListener(e -> Log.e("Firestore", "ID 업데이트 실패", e));

                    Toast.makeText(getContext(), "새 책이 '읽는 중 책'에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "책 추가 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void addRandomBookToReadingBooks() {
        BookSearchResponseDto.BookItemDto newBook = new BookSearchResponseDto.BookItemDto();
        newBook.setTitle("읽는 중 책 제목");
        newBook.setAuthor("읽는 중 작가");
        newBook.setPublisher("읽는 중 출판사");
        newBook.setImage("https://example.com/reading.jpg");
        newBook.setDescription("읽는 중 책 설명");
        newBook.setPubdate("2024");
        newBook.setReadingStatus("reading_books");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("books")
                .add(newBook) // Firestore가 자동으로 ID 생성
                .addOnSuccessListener(documentReference -> {
                    // Firestore에서 생성된 ID를 가져옴
                    String generatedId = documentReference.getId();
                    documentReference.update("id", generatedId) // 생성된 ID를 해당 문서에 업데이트
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "ID 업데이트 성공"))
                            .addOnFailureListener(e -> Log.e("Firestore", "ID 업데이트 실패", e));

                    Toast.makeText(getContext(), "새 책이 '읽는 중 책'에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "책 추가 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
