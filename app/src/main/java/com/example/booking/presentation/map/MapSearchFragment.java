package com.example.booking.presentation.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booking.R;
import com.example.booking.presentation.registration.PlaceSearchKeyword;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapSearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MapPlaceAdapter adapter;
    private List<PlaceSearchKeyword.Place> placeList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate for this view
        View view = inflater.inflate(R.layout.fragment_map_search, container, false);

        // UI 초기화
        EditText etSearch = view.findViewById(R.id.et_map_search);
        ImageView ivSearch = view.findViewById(R.id.iv_map_search);
        RecyclerView recyclerView = view.findViewById(R.id.rv_map_search);

        // RecyclerView 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MapPlaceAdapter(placeList);
        recyclerView.setAdapter(adapter);


        // 네비게이션 가져오기
        NavController navController = Navigation.findNavController(container);

        // 버튼 설정 및 클릭 리스너 구현
        ImageView ivBackArrow = view.findViewById(R.id.iv_back_arrow);
        ivBackArrow.setOnClickListener(v -> {
            navController.navigate(R.id.action_mapSearchFragment_to_mapFragment);
        });

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // 검색 버튼 클릭 리스너 추가
        ivSearch.setOnClickListener(v -> {
            String searchQuery = etSearch.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                searchRecords(searchQuery); // Firestore에서 검색
            } else {
                Toast.makeText(getContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 아이템 클릭 리스너
        adapter.setOnItemClickListener(place -> {

            Bundle bundle_toMap = new Bundle();
            bundle_toMap.putString("placeName",place.getAddress());
            bundle_toMap.putString("placeAddress", place.getPlaceAddress());
            bundle_toMap.putDouble("latitude",place.getLatitude());
            bundle_toMap.putDouble("longitude", place.getLongitude());

            navController.navigate(R.id.mapFragment, bundle_toMap);
        });
        return view;
    }

    private void searchRecords(String query) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore 쿼리 실행
        db.collection("users")
                .document(userId)
                .collection("books")
                .get()
                .addOnSuccessListener(books -> {
                    placeList.clear(); // 기존 데이터를 초기화
                    Set<String> uniqueAddresses = new HashSet<>(); // 중복 필터링을 위한 Set

                    for (DocumentSnapshot book : books.getDocuments()) {
                        String bookId = book.getId();

                        db.collection("users")
                                .document(userId)
                                .collection("books")
                                .document(bookId)
                                .collection("records")
                                .whereGreaterThanOrEqualTo("address", query)
                                .whereLessThanOrEqualTo("address", query + "\uf8ff")
                                .get()
                                .addOnSuccessListener(records -> {
                                    for (DocumentSnapshot record : records.getDocuments()) {
                                        PlaceSearchKeyword.Place place = record.toObject(PlaceSearchKeyword.Place.class);

                                        if (place != null && place.getAddress() != null) {
                                            // 중복된 주소는 추가하지 않음
                                            if (!uniqueAddresses.contains(place.getAddress())) {
                                                uniqueAddresses.add(place.getAddress()); // Set에 주소 추가
                                                placeList.add(place); // 리스트에 추가
                                                Log.d("MapSearchFragment", "추가된 주소: " + place.getAddress());
                                            } else {
                                                Log.d("MapSearchFragment", "중복된 주소 생략: " + place.getAddress());
                                            }
                                        }
                                    }
                                    adapter.notifyDataSetChanged(); // RecyclerView 업데이트
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "검색 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MapSearchFragment", "데이터 로드 실패", e);
                    Toast.makeText(getContext(), "데이터 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}