package com.example.booking.presentation.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.R;

import java.util.ArrayList;
import java.util.List;

public class BookSearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookSearchRVA adapter;
    private List<Book> bookList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_search, container, false);

        // RecyclerView 설정
        recyclerView = view.findViewById(R.id.rv_book_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 데이터 초기화 및 어댑터 설정
        initializeData();
        adapter = new BookSearchRVA(bookList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void initializeData() {
        bookList = new ArrayList<>();
        bookList.add(new Book("책 제목 1", "저자 1", "출판사 1", "2021", "https://images.pexels.com/photos/414612/pexels-photo-414612.jpeg"));
        bookList.add(new Book("책 제목 2", "저자 2", "출판사 2", "2020", "https://images.pexels.com/photos/414612/pexels-photo-414612.jpeg"));
        bookList.add(new Book("책 제목 3", "저자 3", "출판사 3", "2019", "https://images.pexels.com/photos/414612/pexels-photo-414612.jpeg"));
    }
}
