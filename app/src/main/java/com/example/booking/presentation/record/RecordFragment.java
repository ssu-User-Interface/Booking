package com.example.booking.presentation.record;

import android.graphics.Typeface;
import android.os.Bundle;
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

import com.example.booking.R;
import com.example.booking.dto.response.BookSearchResponseDto;
import com.example.booking.presentation.record.adapter.BookAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<BookSearchResponseDto.BookItemDto> bookList;
    private Typeface boldFont, regularFont;
    private TextView tvRecordWill, tvRecordIng, tvRecordPast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

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

        // 기본 데이터 준비
        bookList = getBooksByCategory("읽는 책");

        // 어댑터 설정
        bookAdapter = new BookAdapter(bookList);
        recyclerView.setAdapter(bookAdapter);

        // 기본 선택된 카테고리 폰트 설정
        setFont(tvRecordIng);

        // 카테고리별 버튼 클릭 이벤트 설정
        view.findViewById(R.id.tv_record_will).setOnClickListener(v -> {
            List<BookSearchResponseDto.BookItemDto> willReadBooks = getBooksByCategory("읽을 책");
            bookAdapter.updateBooks(willReadBooks);
            setFont(tvRecordWill);
        });

        view.findViewById(R.id.tv_record_ing).setOnClickListener(v -> {
            List<BookSearchResponseDto.BookItemDto> readingBooks = getBooksByCategory("읽는 책");
            bookAdapter.updateBooks(readingBooks);
            setFont(tvRecordIng);
        });

        view.findViewById(R.id.tv_record_past).setOnClickListener(v -> {
            List<BookSearchResponseDto.BookItemDto> readBooks = getBooksByCategory("읽은 책");
            bookAdapter.updateBooks(readBooks);
            setFont(tvRecordPast);
        });

        // 책 추가 버튼 클릭 이벤트 설정
        Button addButton = view.findViewById(R.id.btn_main_record_add_book);
        addButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(container);
            navController.navigate(R.id.action_recordFragment_to_searchFragment);
        });

        // 아이템 클릭 이벤트 설정
        NavController navController = Navigation.findNavController(container);
        bookAdapter.setOnItemClickListener(book -> {
            Bundle bundle = new Bundle();
            bundle.putString("bookTitle", book.getTitle());
            bundle.putString("bookAuthor", book.getAuthor());
            navController.navigate(R.id.action_recordFragment_to_recordSpecificFragment, bundle);
        });

        return view;
    }

    // 카테고리별 샘플 데이터를 가져오는 메서드
    private List<BookSearchResponseDto.BookItemDto> getBooksByCategory(String category) {
        List<BookSearchResponseDto.BookItemDto> books = new ArrayList<>();
        switch (category) {
            case "읽을 책":
                books.add(createBookItem("읽을 책 1", "저자 A", "https://example.com/image1.jpg"));
                books.add(createBookItem("읽을 책 2", "저자 B", "https://example.com/image2.jpg"));
                books.add(createBookItem("읽을 책 3", "백종원", "https://example.com/image3.jpg"));
                books.add(createBookItem("읽을 책 4", "이수민", "https://example.com/image4.jpg"));
                break;
            case "읽는 책":
                books.add(createBookItem("읽는 책 1", "저자 C", "https://example.com/image5.jpg"));
                books.add(createBookItem("읽는 책 2", "저자 D", "https://example.com/image6.jpg"));
                break;
            case "읽은 책":
                books.add(createBookItem("읽은 책 1", "저자 E", "https://example.com/image7.jpg"));
                books.add(createBookItem("읽은 책 2", "저자 F", "https://example.com/image8.jpg"));
                books.add(createBookItem("읽은 책 3", "장효원", "https://example.com/image9.jpg"));
                break;
        }
        return books;
    }

    // BookItemDto 객체 생성 헬퍼 메서드
    private BookSearchResponseDto.BookItemDto createBookItem(String title, String author, String imageUrl) {
        BookSearchResponseDto.BookItemDto book = new BookSearchResponseDto.BookItemDto();
        book.setTitle(title);
        book.setAuthor(author);
        book.setImage(imageUrl);
        return book;
    }

    // 선택된 TextView에만 폰트를 변경하는 메서드
    private void setFont(TextView selectedTextView) {
        tvRecordWill.setTypeface(regularFont);
        tvRecordIng.setTypeface(regularFont);
        tvRecordPast.setTypeface(regularFont);

        selectedTextView.setTypeface(boldFont);
    }
}

