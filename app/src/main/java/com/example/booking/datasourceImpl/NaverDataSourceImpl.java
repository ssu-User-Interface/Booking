package com.example.booking.datasourceImpl;

import com.example.booking.datasource.NaverDataSource;
import com.example.booking.dto.response.BookSearchResponseDto;
import com.example.booking.service.NaverApiService;

import javax.inject.Inject;

public class NaverDataSourceImpl implements NaverDataSource {

    private final NaverApiService naverService;

    @Inject
    public NaverDataSourceImpl(NaverApiService naverService) {
        this.naverService = naverService;
    }

    @Override
    public BookSearchResponseDto fetchBooks(String query) throws Exception {
        return naverService.searchBooks(query).execute().body();
    }
}
