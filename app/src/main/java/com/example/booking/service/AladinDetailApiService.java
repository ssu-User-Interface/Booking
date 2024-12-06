package com.example.booking.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladinDetailApiService {
    @GET("ItemLookUp.aspx")
    Call<ResponseBody> getItemDetails(
            @Query("ttbkey") String ttbKey,
            @Query("ItemId") String itemId,
            @Query("output") String output
    );
}
