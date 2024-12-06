package com.example.booking.repository;

import android.util.Log;

import com.example.booking.datasource.AladinDataSource;
import com.example.booking.datasource.NaverDataSource;
import com.example.booking.dto.response.BookSearchResponseDto;

import java.util.List;

import javax.inject.Inject;

public class BookRepository {
    private final NaverDataSource naverDataSource;
    private final AladinDataSource aladinDataSource;

    @Inject
    public BookRepository(NaverDataSource naverDataSource, AladinDataSource aladinDataSource) {
        this.naverDataSource = naverDataSource;
        this.aladinDataSource = aladinDataSource;
    }

    public BookSearchResponseDto fetchBooks(String query) throws Exception {
        BookSearchResponseDto naverResponse = naverDataSource.fetchBooks(query);

        List<BookSearchResponseDto.BookItemDto> items = naverResponse.getItems();
        for (BookSearchResponseDto.BookItemDto item : items) {
            try {
                Log.d("BookRepository", "알라딘에서 페이지 수 가져오기 시작: " + item.getTitle());
                int pageCount = aladinDataSource.fetchPageCount(item.getTitle());
                item.setPageCount(pageCount);
                Log.d("BookRepository", "알라딘에서 페이지 수 가져오기 완료: " + item.getTitle() + ", 페이지 수: " + pageCount);
            } catch (Exception e) {
                Log.e("BookRepository", "알라딘에서 페이지 수 가져오기 실패: " + item.getTitle(), e);
            }
        }

        return naverResponse;
    }
}