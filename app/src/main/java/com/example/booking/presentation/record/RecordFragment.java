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
import com.example.booking.data.model.Book;
import com.example.booking.presentation.record.adapter.BookAdapter;
import java.util.ArrayList;
import java.util.List;


public class RecordFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList; // 전체 책 목록
    private List<Book> booksWillRead; // "읽을 책" 리스트
    private List<Book> booksReading;  // "읽는 책" 리스트
    private List<Book> booksRead;     // "읽은 책" 리스트
    private Typeface boldFont, regularFont;
    private TextView tvRecordWill, tvRecordIng, tvRecordPast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerView_book_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 폰트 초기화 (Pretendard를 예시로 사용, res/font 경로에 추가되어 있어야 함)
        boldFont = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold);
        regularFont = ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular);

        // TextView 초기화
        tvRecordWill = view.findViewById(R.id.tv_record_will);
        tvRecordIng = view.findViewById(R.id.tv_record_ing);
        tvRecordPast = view.findViewById(R.id.tv_record_past);


        // 전체 데이터 준비
        bookList = getSampleBooks();

        // 각 카테고리별 데이터 준비
        booksWillRead = getBooksByCategory("읽을 책");
        booksReading = getBooksByCategory("읽는 책");
        booksRead = getBooksByCategory("읽은 책");

        // 기본 어댑터 설정 (예: "읽을 책" 리스트로 시작)
        bookAdapter = new BookAdapter(booksWillRead);
        recyclerView.setAdapter(bookAdapter);

        // 버튼 초기화 및 클릭 이벤트 설정
        Button addButton = view.findViewById(R.id.btn_main_record_add_book);
        addButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(container);
            navController.navigate(R.id.action_recordFragment_to_searchFragment);
        });

        // 기본값으로 "읽는 책" 리스트를 설정하고, "읽는 책" 텍스트를 볼드로 설정
        bookAdapter.updateBooks(booksReading);
        setFont(tvRecordIng);

        // "읽을 책" 버튼 클릭 이벤트 설정
        view.findViewById(R.id.tv_record_will).setOnClickListener(v -> {
            bookAdapter.updateBooks(booksWillRead);
            setFont(tvRecordWill);
        });

        // "읽는 책" 버튼 클릭 이벤트 설정
        view.findViewById(R.id.tv_record_ing).setOnClickListener(v -> {
            bookAdapter.updateBooks(booksReading);
            setFont(tvRecordIng);
        });

        // "읽은 책" 버튼 클릭 이벤트 설정
        view.findViewById(R.id.tv_record_past).setOnClickListener(v -> {
            bookAdapter.updateBooks(booksRead);
            setFont(tvRecordPast);
        });

        return view;
    }


    // 카테고리별 샘플 데이터를 가져오는 메서드
    private List<Book> getBooksByCategory(String category) {
        List<Book> books = new ArrayList<>();
        switch (category) {
            case "읽을 책":
                books.add(new Book(R.drawable.img_book, "읽을 책 1", "저자 A"));
                books.add(new Book(R.drawable.img_book, "읽을 책 2", "저자 B"));
                books.add(new Book(R.drawable.img_book, "읽을 책 3", "백종원"));
                books.add(new Book(R.drawable.img_book, "읽을 책 4", "이수민"));
                break;
            case "읽는 책":
                books.add(new Book(R.drawable.img_book, "읽는 책 1", "저자 C"));
                books.add(new Book(R.drawable.img_book, "읽는 책 2", "저자 D"));
                break;
            case "읽은 책":
                books.add(new Book(R.drawable.img_book, "읽은 책 1", "저자 E"));
                books.add(new Book(R.drawable.img_book, "읽은 책 2", "저자 F"));
                books.add(new Book(R.drawable.img_book, "읽은 책 3", "장효원"));
                break;
        }
        return books;
    }

    // 선택된 TextView에만 폰트를 변경하는 메서드
    private void setFont(TextView selectedTextView) {
        // 모든 버튼에 기본 폰트 설정
        tvRecordWill.setTypeface(regularFont);
        tvRecordIng.setTypeface(regularFont);
        tvRecordPast.setTypeface(regularFont);

        // 선택된 버튼에 볼드 폰트 설정
        selectedTextView.setTypeface(boldFont);
    }

    private List<Book> getSampleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(R.drawable.img_book, "책 제목 1", "저자 1"));
        books.add(new Book(R.drawable.img_book, "책 제목 2", "저자 2"));
        books.add(new Book(R.drawable.img_book, "책 제목 3", "저자 3"));
        books.add(new Book(R.drawable.img_book, "책 제목 4", "저자 4"));
        return books;
    }
}

