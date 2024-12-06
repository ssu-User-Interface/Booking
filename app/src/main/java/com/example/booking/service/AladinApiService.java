package com.example.booking.service;

import com.example.booking.dto.response.BookSearchPageResponseDto;
import com.example.booking.dto.response.BookSearchResponseDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladinApiService {
    @GET("ItemSearch.aspx")
    Call<ResponseBody> searchBooks(
            @Query("ttbkey") String ttbKey,
            @Query("Query") String query,
            @Query("output") String output
    );
}