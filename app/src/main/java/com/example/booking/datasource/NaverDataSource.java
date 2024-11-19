package com.example.booking.datasource;

import com.example.booking.dto.response.BookSearchResponseDto;

public interface NaverDataSource {
    BookSearchResponseDto fetchBooks(String query) throws Exception;
}