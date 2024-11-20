package com.example.booking.service;

import com.example.booking.dto.response.BookSearchResponseDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NaverApiService {
    @GET("v1/search/book.json")
    Call<BookSearchResponseDto> searchBooks(
            @Query("query") String query
    );
}
