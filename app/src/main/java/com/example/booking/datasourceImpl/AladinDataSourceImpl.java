package com.example.booking.datasourceImpl;

import android.util.Log;

import com.example.booking.datasource.AladinDataSource;
import com.example.booking.dto.response.BookSearchPageResponseDto;
import com.example.booking.service.AladinApiService;
import com.example.booking.service.AladinDetailApiService;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class AladinDataSourceImpl implements AladinDataSource {
    private final AladinApiService aladinApiService;
    private final AladinDetailApiService aladinDetailApiService;

    @Inject
    public AladinDataSourceImpl(AladinApiService aladinApiService,
                                AladinDetailApiService aladinDetailApiService) {
        this.aladinApiService = aladinApiService;
        this.aladinDetailApiService = aladinDetailApiService;
    }

    @Override
    public int fetchPageCount(String title) throws IOException {
        String ttbKey = "ttbally02042138001";

        Call<ResponseBody> searchCall = aladinApiService.searchBooks(ttbKey, title, "JS");
        Response<ResponseBody> searchResponse = searchCall.execute();

        if (searchResponse.isSuccessful() && searchResponse.body() != null) {
            String searchJsonString = searchResponse.body().string();
            Log.d("Aladin API Search Response", "검색 결과 JSON: " + searchJsonString);

            searchJsonString = cleanJsonString(searchJsonString);

            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new StringReader(searchJsonString));
            jsonReader.setLenient(true);
            BookSearchPageResponseDto searchDto = gson.fromJson(jsonReader, BookSearchPageResponseDto.class);

            if (searchDto.getItems() != null && !searchDto.getItems().isEmpty()) {
                String isbn = searchDto.getItems().get(0).getIsbn();
                Log.d("Aladin API", "검색된 ISBN: " + isbn);

                return fetchPageCountFromIsbn(ttbKey, isbn);
            } else {
                return 365;
            }
        } else {
            throw new IOException("API 호출 실패: " + searchResponse.message());
        }
    }

    private int fetchPageCountFromIsbn(String ttbKey, String isbn) throws IOException {
        Call<ResponseBody> itemDetailCall = aladinDetailApiService.getItemDetails(ttbKey,isbn,"JS");
        Response<ResponseBody> itemDetailResponse = itemDetailCall.execute();

        if (itemDetailResponse.isSuccessful() && itemDetailResponse.body() != null) {
            String itemDetailJsonString = itemDetailResponse.body().string();
            return extractItemPage(itemDetailJsonString);
        } else {
            throw new IOException("상품 상세 조회 API 호출 실패: " + itemDetailResponse.message());
        }
    }

    private int extractItemPage(String jsonString) {
        Gson gson = new Gson();
        try {
            Log.d("Aladin API", "상품 상세 정보 JSON: " + jsonString);

            jsonString = cleanJsonString(jsonString);

            JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
            jsonReader.setLenient(true);

            BookSearchPageResponseDto detailDto = gson.fromJson(jsonReader, BookSearchPageResponseDto.class);

            if (detailDto != null && detailDto.getItems() != null && !detailDto.getItems().isEmpty()) {
                BookSearchPageResponseDto.BookItemDto item = detailDto.getItems().get(0);
                if (item != null && item.getBookInfo() != null) {
                    int itemPage = item.getBookInfo().getItemPage();
                    return itemPage;
                } else {
                    Log.e("Aladin API", "상품 상세 정보에서 itemPage 추출 실패: bookInfo가 null");
                }
            } else {
                Log.e("Aladin API", "상품 상세 정보에서 itemPage 추출 실패: item이 비어있거나 null");
            }
        } catch (Exception e) {
            Log.e("Aladin API", "상품 상세 정보에서 itemPage 추출 실패", e);
        }
        return 365;
    }

    private String cleanJsonString(String jsonString) {
        return jsonString.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");
    }
}
