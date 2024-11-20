package com.example.booking.presentation.registration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.R;
import com.example.booking.adapter.MapSearchAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordRegistrationMapSearchFragment extends Fragment {

    private static final String BASE_URL = "https://dapi.kakao.com/";
    private static final String API_KEY = "KakaoAK 3c022649ad8de389859c169d2e306c8e"; // REST API 키
    private MapSearchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate for this view
        View view = inflater.inflate(R.layout.fragment_record_registration_map_search, container, false);

        // 네비게이션 설정
        NavController navController = Navigation.findNavController(container);

        // 버튼 설정 및 클릭 리스너 설정
        ImageView backbutton = view.findViewById(R.id.iv_back_arrow);
        backbutton.setOnClickListener(v -> {
            navController.navigate(R.id.action_recordRegistrationMapSearchFragment_to_recordRegistrationFragment);
        });

        // RecyclerView 설정
        RecyclerView recyclerView = view.findViewById(R.id.rv_record_map_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // EditText와 검색 버튼
        EditText etSearchPlace = view.findViewById(R.id.et_record_map_search);
        ImageView ivSearchPlace = view.findViewById(R.id.iv_record_map_search_ic);

        ivSearchPlace.setOnClickListener(v -> {
            String keyword = etSearchPlace.getText().toString().trim();
            if(!keyword.isEmpty()) {
                searchKeyword(keyword, recyclerView);
            } else {
                Log.w("Search Place","검색어가 비어있습니다.");
            }
        });


        adapter = new MapSearchAdapter(new ArrayList<>()); // 어댑터 생성
        recyclerView.setAdapter(adapter);

        Button btnAddPlace = view.findViewById(R.id.btn_add_place);
        btnAddPlace.setVisibility(View.GONE);

        adapter.setOnItemClickListener(place -> {
            btnAddPlace.setVisibility(View.VISIBLE); // 버튼 보이기
            Log.d("PlaceSelected", "선택된 장소: " + place.getName());
        });


        etSearchPlace.setOnClickListener(v-> {
            btnAddPlace.setVisibility(View.GONE);
        });


        return view;
    }

    // 키워드 검색 함수
    private void searchKeyword(String keyword, RecyclerView recyclerView) {
        Retrofit retrofit = new Retrofit.Builder() // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KakaoAPI api = retrofit.create(KakaoAPI.class); // 통신 인터페이스를 객체로 생성
        Call<PlaceSearchKeyword> call = api.getSearchKeyword(API_KEY, keyword); // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(new Callback<PlaceSearchKeyword>() {
            @Override
            public void onResponse(Call<PlaceSearchKeyword> call, Response<PlaceSearchKeyword> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlaceSearchKeyword.Place> places = response.body().getPlaces();
                    adapter.updateData(places);
                    // 통신 성공 (검색 결과는 response.body()에 담겨있음)
//                    Log.d("RecordSearch", "Raw: " + response.raw());
//                    Log.d("RecordSearch", "Body: " + response.body());
                } else {
                    Log.w("RecordSearch", "응답 실패: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PlaceSearchKeyword> call, Throwable t) {
                // 통신 실패
                Log.e("RecordSearch", "통신 실패: " + t.getMessage());
            }
        });
    }
}