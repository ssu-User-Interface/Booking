package com.example.booking.presentation.record;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.booking.R;
import com.example.booking.data.model.Book;
import com.example.booking.presentation.record.adapter.BookAdapter;
import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerView_book_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 두 개의 열로 그리드 구성

        // 데이터 준비
        bookList = getSampleBooks();

        // 어댑터 설정
        bookAdapter = new BookAdapter(bookList);
        recyclerView.setAdapter(bookAdapter);

        return view;
    }

    // 샘플 데이터를 위한 메서드
    private List<Book> getSampleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(R.drawable.img_book, "책 제목 1", "저자 1"));
        books.add(new Book(R.drawable.img_book, "책 제목 2", "저자 2"));
        books.add(new Book(R.drawable.img_book, "책 제목 3", "저자 3"));
        books.add(new Book(R.drawable.img_book, "책 제목 4", "저자 4"));
        return books;
    }
}
