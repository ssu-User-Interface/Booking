package com.example.booking.repository;

import com.example.booking.datasource.NaverDataSource;
import com.example.booking.dto.response.BookSearchResponseDto;

import javax.inject.Inject;

public class BookRepository {
    private final NaverDataSource dataSource;

    @Inject
    public BookRepository(NaverDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public BookSearchResponseDto fetchBooks(String query) throws Exception {
        return dataSource.fetchBooks(query);
    }
}