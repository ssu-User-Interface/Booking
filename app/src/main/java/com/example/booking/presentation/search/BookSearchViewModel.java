package com.example.booking.presentation.search;

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

    @Inject
    public BookSearchViewModel(BookRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<BookSearchResponseDto.BookItemDto>> getBooks() {
        return books;
    }

    public void searchBooks(String query) {
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
