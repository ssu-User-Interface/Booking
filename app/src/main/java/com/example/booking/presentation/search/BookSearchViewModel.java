package com.example.booking.presentation.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.booking.dto.response.BookSearchResponseDto;
import com.example.booking.repository.BookRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BookSearchViewModel extends ViewModel {
    private final BookRepository repository;
    private final MutableLiveData<List<BookSearchResponseDto.BookItemDto>> books = new MutableLiveData<>();
    private final MutableLiveData<String> currentQuery = new MutableLiveData<>(); // 검색 쿼리 상태

    @Inject
    public BookSearchViewModel(BookRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<BookSearchResponseDto.BookItemDto>> getBooks() {
        return books;
    }

    public LiveData<String> getCurrentQuery() {
        return currentQuery;
    }

    public void searchBooks(String query) {
        currentQuery.setValue(query);
        new Thread(() -> {
            try {
                BookSearchResponseDto response = repository.fetchBooks(query);
                books.postValue(response.getItems());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

